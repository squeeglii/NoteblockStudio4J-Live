package dev.cg360.nbs.reader;

import dev.cg360.nbs.exception.InvalidHeaderException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Loosely based off the VS2010 C# Project provided at
 * https://www.stuffbydavid.com/mcnbs/format.
 * It has a bunch of information about the format there. Super
 * useful!
 */
public class NBS4JReader {

    public static final Byte[] SUPPORTED_VERSIONS = new Byte[]{ 4 };

    private boolean fileLoaded;
    private String filepath;

    private Header header;

    public NBS4JReader(String filepath){
        this.filepath = filepath;
    }

    public void read() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filepath));
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        this.header = readHeader(buffer);
        while(buffer.hasRemaining()){
            buffer.get();
        }
        buffer.clear();
        fileLoaded = true;
    }

    private Header readHeader(ByteBuffer buffer){
        Header header = new Header();

        if(buffer.getShort() != 0) throw new InvalidHeaderException("The specified file does not follow an modern *.NBS format.");

        byte version = buffer.get();
        if(!Arrays.asList(SUPPORTED_VERSIONS).contains(version)) throw new InvalidHeaderException(String.format("The specified file (Version %s) does not use a supported NBS version.", version));
        header.setVersion(version);
        header.setVanillaInstrumentCount(buffer.get());
        header.setSongLength(readUnsignedShort(buffer));
        header.setLayers(readUnsignedShort(buffer));
        header.setTitle(readNBSString(buffer));
        header.setAuthor(readNBSString(buffer));
        header.setOriginalAuthor(readNBSString(buffer));
        header.setDescription(readNBSString(buffer));
        header.setTempo(readUnsignedShort(buffer));
        header.setAutosave(buffer.get());
        header.setAutosaveDuration(buffer.get());
        header.setTimeSignature(buffer.get());
        header.setMinutesSpent(readUnsignedInt(buffer));
        header.setLeftClicks(readUnsignedInt(buffer));
        header.setRightClicks(readUnsignedInt(buffer));
        header.setNotesAdded(readUnsignedInt(buffer));
        header.setNotesRemoved(readUnsignedInt(buffer));
        header.setMidiSchemName(readNBSString(buffer));
        header.setLoopEnabled(buffer.get());
        header.setMaxLoops(buffer.get());
        header.setLoopStartTick(readUnsignedShort(buffer));
        return header;
    }

    // What has Java got against unsigned types TwT
    private int readUnsignedShort(ByteBuffer buffer){
        byte[] bytes = new byte[2];
        buffer.get(bytes);
        int integer = 0;
        for(int i = 0; i < 2; i++){
            byte b = bytes[i];
            integer += ((b & 0xffff) << i);
        }
        return integer;
    }

    private long readUnsignedInt(ByteBuffer buffer){
        byte[] bytes = new byte[4];
        buffer.get(bytes);
        long longVal = 0;
        for(int i = 0; i < 4; i++){
            byte b = bytes[i];
            longVal += ((b & 0xff) << i);
        }
        return longVal;
    }

    private String readNBSString(ByteBuffer buffer){
        int length = buffer.getInt();
        char[] string = new char[length];
        for(int i = 0; i < length; i++){
            string[i] = buffer.getChar();
        }
        return String.valueOf(string);
    }

    public Header getHeader(){
        if(!fileLoaded) throw new IllegalStateException("File is not loaded, thus, no header. What a shame.");
        return header;
    }

}
