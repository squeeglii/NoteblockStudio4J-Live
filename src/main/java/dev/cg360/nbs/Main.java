package dev.cg360.nbs;

import dev.cg360.nbs.reader.NBS4JReader;

public class Main {

    public static void main(String[] args){
        if(args.length < 1) throw new IllegalArgumentException("Missing 1 file path argument.");
        try {
            NBS4JReader reader = new NBS4JReader(args[0]);
            reader.read();
            System.out.println(reader.getHeader());
        } catch (Exception err){
            err.printStackTrace();
        }
    }

}
