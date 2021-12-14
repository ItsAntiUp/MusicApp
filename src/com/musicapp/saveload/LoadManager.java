package com.musicapp.saveload;

import com.musicapp.exception.FailedSaveLoadException;
import com.musicapp.record.Playlist;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LoadManager extends SaveLoadManager {
    public static final String FILE_NOT_FOUND_MSG = "File not found: ";
    public static final String LOAD_FAILED_MSG = "Failed to load contents from file: ";

    public LoadManager(ArrayList<Playlist> playlists){
        super(playlists);
    }

    public ArrayList<Playlist> getAll(){
        return getAllPlaylists();
    }

    @Override
    protected void execute(final String filename) throws FailedSaveLoadException {
        try {
            FileInputStream fileStream = new FileInputStream(filename);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            setAllPlaylists((ArrayList<Playlist>)objectStream.readObject());
        }
        catch (IOException e) {
            setAllPlaylists(new ArrayList<>());

            //Rethrow to the upper class
            throw new FailedSaveLoadException(FILE_NOT_FOUND_MSG + filename, e);
        }
        catch (ClassNotFoundException e) {
            setAllPlaylists(new ArrayList<>());

            //Rethrow to the upper class
            throw new FailedSaveLoadException(LOAD_FAILED_MSG + filename, e);
        }
    }

    public void load(final String filename) throws FailedSaveLoadException{
        execute(filename);
    }
}
