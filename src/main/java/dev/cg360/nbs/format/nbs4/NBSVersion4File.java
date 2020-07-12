package dev.cg360.nbs.format.nbs4;

import java.util.ArrayList;

public class NBSVersion4File {

    protected NBSVersion4Header header;
    protected ArrayList<NBSVersion4Tick> ticks;

    public NBSVersion4File(NBSVersion4Header header, ArrayList<NBSVersion4Tick> ticks) {
        this.header = header;
        this.ticks = ticks;
    }

    public void outputFileToLog(){
        System.out.println(header.toString());
        for(NBSVersion4Tick tick: ticks){
            System.out.println(tick.toString());
        }
    }
}
