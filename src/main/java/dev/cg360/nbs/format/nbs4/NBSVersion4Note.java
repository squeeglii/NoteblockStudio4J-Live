package dev.cg360.nbs.format.nbs4;

public class NBSVersion4Note {

    protected byte instrument;
    protected byte key;
    protected byte volume;
    protected byte panning;
    protected short pitch;

    public NBSVersion4Note(byte instrument, byte key, byte volume, byte panning, short pitch) {
        this.instrument = instrument;
        this.key = key;
        this.volume = volume;
        this.panning = panning;
        this.pitch = pitch;
    }

    public byte getInstrument() { return instrument; }
    public byte getKey() { return key; }
    public byte getVolume() { return volume; }
    public byte getPanning() { return panning; }
    public short getPitch() { return pitch; }

}
