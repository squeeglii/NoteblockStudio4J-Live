package dev.cg360.nbs.format.nbs4;

public class NBSVersion4Instrument {

    protected String name;
    protected String sound;
    protected byte key;
    protected byte showKeyPress;

    public NBSVersion4Instrument(String name, String sound, byte key, byte showKeyPress) {
        this.name = name;
        this.sound = sound;
        this.key = key;
        this.showKeyPress = showKeyPress;
    }

    public String getName() { return name; }
    public String getSound() { return sound; }
    public byte getKey() { return key; }
    public byte getShowKeyPress() { return showKeyPress; }

}
