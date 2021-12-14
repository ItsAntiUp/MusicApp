package com.musicapp.player;

import javafx.util.Pair;

import java.util.ArrayList;

public interface Playable {
    //Record and Playlist classes implement the playable interface (so we can play the contents in the music player)
    ArrayList<Pair<String,String>> getPaths();
}
