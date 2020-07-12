package dev.cg360.nbs.format.nbs4;

import dev.cg360.nbs.exception.InvalidNBSHeaderException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    protected NBSVersion4Header readHeader(ByteBuffer buffer){
        NBSVersion4Header header = initialVersionChecks(buffer);
        return readHeaderPostCheck(buffer, header);
    }

    protected NBSVersion4Header initialVersionChecks(ByteBuffer buffer){
        NBSVersion4Header header = new NBSVersion4Header();

        if(buffer.getShort() != 0) throw new InvalidNBSHeaderException("The specified file does not follow an modern *.NBS format.");

        byte version = buffer.get();
        if(!Arrays.asList(getSupportedVersions()).contains(version)) throw new InvalidNBSHeaderException(String.format("The specified file (Version %s) does not use a supported NBS version.", version));
        header.setVersion(version);
        return header;
    }

    protected NBSVersion4Header readHeaderPostCheck(ByteBuffer buffer, NBSVersion4Header checkedHeader){
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

    protected NBSVersion4File readBody(NBSVersion4Header header, ByteBuffer file) {

    }

    protected NBSVersion4Tick[] readNotes(ByteBuffer file) {
        int tick = -1;
        int jumps = ;
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
        int length = buffer.getInt();
        char[] string = new char[length];
        for(int i = 0; i < length; i++){
            string[i] = buffer.getChar();
        }
        return String.valueOf(string);
    }

    protected static byte flipByteBitOrder(byte flipByte){
        return (byte) (Integer.reverse(flipByte) >>> (Integer.SIZE - Byte.SIZE));
    }
}
