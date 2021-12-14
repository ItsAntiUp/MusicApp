package com.musicapp.record;

import com.musicapp.artist.Musician;
import com.musicapp.artist.Producer;
import com.musicapp.artist.Vocalist;
import com.musicapp.utility.DateManager;
import com.musicapp.utility.FileManager;
import javafx.util.Pair;
import javafx.scene.media.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.sql.Time;
import java.util.*;

public class RecordsFromFile implements Runnable{
    private final File currentFile;
    private Playlist playlist;

    RecordsFromFile(File file, Playlist playlist){
        this.playlist = playlist;
        currentFile = file;
    }

    public void run(){
        /*If file is not null, generating random values and setting them to our record.*/
        Record record = Record.generateRandomRecord();

        /*Splitting the record title to artists and the record name (by multiple conditions).*/
        ArrayList<Object> recordValues = FileManager.splitFilename(currentFile.getName());

        if(recordValues.size()== 0)
            return;

        /*Generating a date (for now it is always the same value)*/
        Date dateOfBirth = record.getDateReleased();
        ArrayList<Pair<String, String>> artistData = (ArrayList<Pair<String, String>>)recordValues.get(0);

        /*Determining whether the extracted artist is a producer, a vocalist, or other?*/
        for(Pair<String, String> pair : artistData){
            if(pair.getKey().equals("Vocalist"))
                record.getArtists().add(new Vocalist(pair.getValue(), dateOfBirth));
            else if(pair.getKey().equals("Producer"))
                record.getArtists().add(new Producer(pair.getValue(), dateOfBirth));
            else
                record.getArtists().add(new Musician(pair.getValue(), dateOfBirth));
        }

        /*Setting the actual record name, the final title and file path.*/
        record.setName((String) recordValues.get(1));
        record.setTitle((String) recordValues.get(2));
        record.setFilePath(currentFile.getPath());
        record.setDateReleased(DateManager.getLastModifiedDate(currentFile));

        //TODO: Needs support for other file types (now only WAV supported)
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(currentFile);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            record.setLength(DateManager.getTimeFromDouble(durationInSeconds).getTime());
        }
        catch(java.io.IOException ioExc){
            //do nothing
        }
        catch (javax.sound.sampled.UnsupportedAudioFileException e){
            //do nothing again
        }

        playlist.getRecords().add(record);
    }
}
