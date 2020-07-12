package dev.cg360.nbs;

import dev.cg360.nbs.format.nbs4.NBSVersion4File;
import dev.cg360.nbs.format.nbs4.NBSVersion4Reader;

public class Main {

    public static void main(String[] args){
        if(args.length < 1) throw new IllegalArgumentException("Missing 1 file path argument.");
        try {
            NBSVersion4File file = NBSVersion4Reader.read(args[0]);
            file.outputFileToLog();
        } catch (Exception err){
            err.printStackTrace();
        }
    }

}
