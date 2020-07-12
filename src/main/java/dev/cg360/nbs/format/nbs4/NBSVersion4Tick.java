package dev.cg360.nbs.format.nbs4;

import java.util.Arrays;

public class NBSVersion4Tick {

    protected int startingTick;
    protected NBSVersion4Note[] layers;

    public NBSVersion4Tick(int startingTick, NBSVersion4Note[] layers){
        this.startingTick = startingTick;
        this.layers = layers;
    }

    public int getStartingTick() { return startingTick; }
    public NBSVersion4Note[] getLayers() { return layers; }

    @Override
    public String toString() {
        return "{NBSVersion4Tick: Start="+startingTick+"; Notes="+ Arrays.toString(layers)+"}";
    }
}
