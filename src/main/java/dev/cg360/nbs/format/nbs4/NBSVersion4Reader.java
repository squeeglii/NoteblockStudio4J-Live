package dev.cg360.nbs.format.nbs4;

import dev.cg360.nbs.exception.InvalidNBSHeaderException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Loosely based off the VS2010 C# Project provided at
 * https://www.stuffbydavid.com/mcnbs/format.
 * It has a bunch of information about the format there. Super
 * useful!
 */
public class NBSVersion4Reader {

    public Byte[] getSupportedVersions() { return new Byte[]{ 4 }; }
    public ByteOrder getFileByteOrder() { return ByteOrder.LITTLE_ENDIAN; }

    public static NBSVersion4File read(String filepath) throws IOException {
        NBSVersion4Reader reader = new NBSVersion4Reader();

        byte[] bytes = Files.readAllBytes(Paths.get(filepath));
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(reader.getFileByteOrder());

        NBSVersion4Header header = reader.readHeader(buffer);
        NBSVersion4File file = reader.readBody(header, buffer);
        buffer.clear();
        return file;
    }

    protected NBSVersion4Header readHeader(ByteBuffer buffer) {
        NBSVersion4Header header = initialVersionChecks(buffer);
        return readHeaderPostCheck(buffer, header);
    }

    protected NBSVersion4Header initialVersionChecks(ByteBuffer buffer) {
        NBSVersion4Header header = new NBSVersion4Header();

        if(buffer.getShort() != 0) throw new InvalidNBSHeaderException("The specified file does not follow an modern *.NBS format.");

        byte version = buffer.get();
        if(!Arrays.asList(getSupportedVersions()).contains(version)) throw new InvalidNBSHeaderException(String.format("The specified file (Version %s) does not use a supported NBS version.", version));
        header.setVersion(version);
        return header;
    }

    protected NBSVersion4Header readHeaderPostCheck(ByteBuffer buffer, NBSVersion4Header checkedHeader) {
        checkedHeader.setVanillaInstrumentCount(buffer.get());
        checkedHeader.setSongLength(readUnsignedShort(buffer));
        checkedHeader.setLayers(readUnsignedShort(buffer));
        checkedHeader.setTitle(readNBSString(buffer));
        checkedHeader.setAuthor(readNBSString(buffer));
        checkedHeader.setOriginalAuthor(readNBSString(buffer));
        checkedHeader.setDescription(readNBSString(buffer));
        checkedHeader.setTempo(readUnsignedShort(buffer));
        checkedHeader.setAutosave(buffer.get());
        checkedHeader.setAutosaveDuration(buffer.get());
        checkedHeader.setTimeSignature(buffer.get());
        checkedHeader.setMinutesSpent(readUnsignedInt(buffer));
        checkedHeader.setLeftClicks(readUnsignedInt(buffer));
        checkedHeader.setRightClicks(readUnsignedInt(buffer));
        checkedHeader.setNotesAdded(readUnsignedInt(buffer));
        checkedHeader.setNotesRemoved(readUnsignedInt(buffer));
        checkedHeader.setMidiSchemName(readNBSString(buffer));
        checkedHeader.setLoopEnabled(buffer.get());
        checkedHeader.setMaxLoops(buffer.get());
        checkedHeader.setLoopStartTick(readUnsignedShort(buffer));
        return checkedHeader;
    }

    protected NBSVersion4File readBody(NBSVersion4Header header, ByteBuffer buffer) {
        NBSVersion4Tick[] ticks = readNotes(header.getLayers(), buffer).toArray(new NBSVersion4Tick[0]);
        NBSVersion4LayerData[] layers = readDetailedLayerDataEntries(header.getLayers(), buffer);
        NBSVersion4Instrument[] customInstruments = readInstrumentDataEntries(buffer);

        return new NBSVersion4File(header, ticks, layers, customInstruments);
    }

    protected ArrayList<NBSVersion4Tick> readNotes(int layers, ByteBuffer buffer) {
        int currentTick = -1;
        ArrayList<NBSVersion4Tick> ticks = new ArrayList<>();

        while (true){
            int tickJumps = readUnsignedShort(buffer);
            if(tickJumps == 0){ break; }
            currentTick += tickJumps;
            NBSVersion4Note[] notes = readTickNoteLayers(layers, buffer);
            ticks.add(new NBSVersion4Tick(currentTick, notes));
        }
        return ticks;
    }

    protected NBSVersion4Note[] readTickNoteLayers(int layers, ByteBuffer buffer) {
        NBSVersion4Note[] notes = new NBSVersion4Note[layers];
        int currentLayer = -1;
        while (true){
            int layerJumps = readUnsignedShort(buffer);
            if(layerJumps == 0){ break; }
            currentLayer += layerJumps;

            NBSVersion4Note note = readNote(buffer);
            notes[currentLayer] = note;
        }
        return notes;
    }

    protected NBSVersion4Note readNote(ByteBuffer buffer) {
        byte instrument = buffer.get();
        byte key = buffer.get();
        byte volume = buffer.get();
        byte panning = buffer.get();
        short finepitch = buffer.getShort();
        return new NBSVersion4Note(instrument, key, volume, panning, finepitch);
    }

    protected NBSVersion4LayerData[] readDetailedLayerDataEntries(int layers, ByteBuffer buffer) {
        NBSVersion4LayerData[] data = new NBSVersion4LayerData[layers];
        for(int i = 0; i < layers; i++){
            data[i] = readDetailedLayerData(buffer);
        }
        return data;
    }

    protected NBSVersion4LayerData readDetailedLayerData(ByteBuffer buffer) {
        String name = readNBSString(buffer);
        byte lock = buffer.get();
        byte volume = buffer.get();
        byte panning = buffer.get();
        return new NBSVersion4LayerData(name, lock, volume, panning);
    }

    protected NBSVersion4Instrument[] readInstrumentDataEntries(ByteBuffer buffer) {
        byte instrumentCount = buffer.get();
        NBSVersion4Instrument[] data = new NBSVersion4Instrument[instrumentCount];
        for(int i = 0; i < data.length; i++){
            data[i] = readInstrumentData(buffer);
        }
        return data;
    }

    protected NBSVersion4Instrument readInstrumentData(ByteBuffer buffer) {
        String name = readNBSString(buffer);
        String sound = readNBSString(buffer);
        byte key = buffer.get();
        byte showKeyPress = buffer.get();
        return new NBSVersion4Instrument(name, sound, key, showKeyPress);
    }

    // What has Java got against unsigned types TwT
    protected static int readUnsignedShort(ByteBuffer buffer){
        byte[] bytes = new byte[2];
        buffer.get(bytes);

        int integerVal = 0;
        for(int i = bytes.length - 1; i >= 0; i--){
            byte b = bytes[i];
            integerVal += Byte.toUnsignedInt(b) << (i*8);
        }
        return integerVal;
    }

    protected static long readUnsignedInt(ByteBuffer buffer){
        byte[] bytes = new byte[4];
        buffer.get(bytes);

        long longVal = 0;
        for(int i = bytes.length - 1; i >= 0; i--){
            byte b = bytes[i];
            longVal += Byte.toUnsignedInt(b) << (i*8);
        }
        return longVal;
    }

    protected static String readNBSString(ByteBuffer buffer){
        int length = (int) readUnsignedInt(buffer);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        char[] string = new char[length];
        charBuffer.get(string);
        return String.valueOf(string);
    }

    protected static byte flipByteBitOrder(byte flipByte){
        return (byte) (Integer.reverse(flipByte) >>> (Integer.SIZE - Byte.SIZE));
    }
}
