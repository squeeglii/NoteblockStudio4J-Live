package dev.cg360.nbs.format.nbs4;

public class NBSVersion4LayerData {

    protected String name;
    protected byte lock;
    protected byte volume;
    protected byte panning;

    public NBSVersion4LayerData(String name, byte lock, byte volume, byte panning) {
        this.name = name;
        this.lock = lock;
        this.volume = volume;
        this.panning = panning;
    }

    public String getName() { return name; }
    public byte getLock() { return lock; }
    public byte getVolume() { return volume; }
    public byte getPanning() { return panning; }
}
