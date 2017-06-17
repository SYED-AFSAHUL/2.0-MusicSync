package com.example.afsahulsyed.ms;

/**
 * Created by afsahulsyed on 8/6/17.
 */

public class Music {
    private final String title, artist, duration;

    public Music(String title, String artist, String duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }
}
