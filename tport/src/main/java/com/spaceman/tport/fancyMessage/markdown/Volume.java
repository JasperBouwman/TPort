package com.spaceman.tport.fancyMessage.markdown;

import java.util.ArrayList;

public class Volume {
    
    private ArrayList<Chapter> chapters;
    private int volumeNumber;
    
    public Volume(ArrayList<Chapter> chapters, int volumeNumber) {
        this.chapters = chapters;
        this.volumeNumber = volumeNumber;
    }
    
    public ArrayList<Chapter> getChapters() {
        return chapters;
    }
    
    public int getVolumeNumber() {
        return volumeNumber;
    }
}
