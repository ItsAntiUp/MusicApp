package com.musicapp.saveload;

import com.musicapp.exception.FailedSaveLoadException;
import com.musicapp.record.Playlist;

import java.io.*;
import java.util.ArrayList;

public class SaveManager extends SaveLoadManager {
    public static final String SAVE_SUCCESSFUL_MSG = "Progress saved successfully! - file: ";
    public static final String SAVE_FAILED_MSG = "Failed to save contents to file: ";

    public SaveManager(ArrayList<Playlist> playlists){
        super(playlists);
    }

    @Override
    protected void execute(final String filename) throws FailedSaveLoadException {
        try {
            FileOutputStream fileStream = new FileOutputStream(filename);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(getAllPlaylists());
        }
        catch (IOException e) {
            //Rethrow new exception to the upper class
            throw new FailedSaveLoadException(SAVE_FAILED_MSG + filename, e);
        }
    }

    public void save(final String filename) throws FailedSaveLoadException{
        execute(filename);
    }
}
