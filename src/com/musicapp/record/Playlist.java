package com.musicapp.record;

import com.musicapp.player.Playable;
import java.io.File;
import java.util.*;
import javafx.util.*;

public class Playlist implements java.io.Serializable, Playable{
    public static final String MAX_PLAYLIST_NUMBER_REACHED = "Error - Maximum playlist count reached.";

    private String name;
    private ArrayList<Record> records;

    /*Constructors*/
    public Playlist(String name){
        setName(name);
        records = new ArrayList<>();
    }

    public Playlist(String name, ArrayList<Record> records){
        this(name);
        setRecords(records);
    }

    /*Setters*/
    public void setName(String name){
        this.name = name;
    }

    public void setRecords(ArrayList<Record> records){
        this.records.addAll(records);
    }

    /*Getters*/
    public String getName(){
        return name;
    }

    public ArrayList<Record> getRecords(){
        return records;
    }

    /*Other methods*/
    /**THREADING USED HERE*/
    public void addRecordsFromFile(ArrayList<File> files){
        if(files == null)
            return;

        //Starting multiple threads for loading each file
        try {
            for (File f : files) {
                if (f == null)
                    continue;

                Runnable recFromFile = new RecordsFromFile(f, this);
                Thread recFromFileThread = new Thread(recFromFile);
                recFromFileThread.start();

                //Making sure the thread will finish working before the other one starts
                recFromFileThread.join();
            }
        }
        catch (InterruptedException e){
            //Do nothing
        }
    }

    public void deleteRecords(ArrayList<Record> records){
        for (Record r : records)
            this.records.remove(r);
    }

    public void deleteAllRecords(){
        deleteRecords(records);
    }

    private String getRecordString(){
        /*Using StringBuilder for concatenation, since doing String + string is not efficient in loops*/
        StringBuilder builder = new StringBuilder();

        for (Record r : records)
            builder.append(r).append("\n");

        return builder.toString();
    }

    public ArrayList<Pair<String, String>> getPaths(){
        ArrayList<Pair<String,String>> list = new ArrayList<>();

        for(Record r : records)
            list.addAll(r.getPaths());

        return list;
    }

    @Override
    public String toString(){
        return "Playlist - " + name + "\n" + "Records:" + "\n" + getRecordString();
    }
}
