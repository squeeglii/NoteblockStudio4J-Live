package dev.cg360.nbs.format.nbs4;

public class NBSVersion4Tick {

    protected int startingTick;
    protected NBSVersion4Note[] layers;

    public NBSVersion4Tick(int startingTick, NBSVersion4Note[] layers){
        this.startingTick = startingTick;
        this.layers = layers;
    }

    public int getStartingTick() { return startingTick; }
    public NBSVersion4Note[] getLayers() { return layers; }
}
