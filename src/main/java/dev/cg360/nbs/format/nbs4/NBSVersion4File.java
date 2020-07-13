package dev.cg360.nbs.format.nbs4;

import java.util.ArrayList;

public class NBSVersion4File {

    protected NBSVersion4Header header;
    protected NBSVersion4Tick[] ticks;
    protected NBSVersion4LayerData[] layers;
    protected NBSVersion4Instrument[] customInstruments;

    public NBSVersion4File(NBSVersion4Header header, NBSVersion4Tick[] ticks, NBSVersion4LayerData[] layers, NBSVersion4Instrument[] customInstruments) {
        this.header = header;
        this.ticks = ticks;
        this.layers = layers;
        this.customInstruments = customInstruments;
    }

    public void outputFileToLog(){
        System.out.println(header.toString());
        for(NBSVersion4Tick tick: ticks) tick.printTick();
        for(NBSVersion4LayerData layer: layers) System.out.println(layer.toString());
    }
}
