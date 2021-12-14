package com.musicapp.saveload;

import com.musicapp.exception.FailedSaveLoadException;
import com.musicapp.record.Playlist;
import java.util.ArrayList;

public abstract class SaveLoadManager {
    private ArrayList<Playlist> allPlaylists;

    public SaveLoadManager(ArrayList<Playlist> playlists){
        setAllPlaylists(playlists);
    }

    //Protected methods, just so they are accessible to this package only
    protected void setAllPlaylists(ArrayList<Playlist> playlists){
        this.allPlaylists = playlists;
    }

    protected ArrayList<Playlist> getAllPlaylists(){
        return allPlaylists;
    }

    protected abstract void execute(final String filename) throws FailedSaveLoadException;
}
