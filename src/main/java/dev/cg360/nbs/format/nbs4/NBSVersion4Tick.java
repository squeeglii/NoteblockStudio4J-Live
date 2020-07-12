package dev.cg360.nbs.format.nbs4;

import dev.cg360.nbs.exception.InvalidNBSBodyException;

public class NBSVersion4Tick {

    protected NBSVersion4Note[] layeredNotes;
    protected int jumps;

    public NBSVersion4Tick(NBSVersion4Note[] notes, int jumps){
        if((notes == null) || (notes.length == 0)) throw new InvalidNBSBodyException("Notes array is empty. Where are all the notes for the tick?! Invalid format?");
        this.layeredNotes = notes;
        this.jumps = jumps;
    }
}
