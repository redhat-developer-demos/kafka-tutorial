package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SongStats {

    private String songName;
    private int count;

    public SongStats() {
    }

    public SongStats aggregate(String songName) {
    
        this.songName = songName;
        this.count++;

        return this;
    }

    public String getSongName() {
        return songName;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "PlayedSong [count=" + count + ", songName=" + songName + "]";
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setCount(int count) {
        this.count = count;
    }

}