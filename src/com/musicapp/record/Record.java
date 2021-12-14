package com.musicapp.record;

import com.musicapp.artist.Artist;
import com.musicapp.artist.Musician;
import com.musicapp.player.Playable;
import com.musicapp.utility.DateManager;
import javafx.util.Duration;
import javafx.util.Pair;

import java.lang.String;
import java.util.*;

public class Record implements java.io.Serializable, Playable {
    /*Constants*/
    public static final String FORMAT_DESCRIPTION = "Music files";
    public static final String MP3 = "mp3";
    public static final String WAV = "wav";
    public static final String FLAC = "flac";
    public static final String M4A = "m4a";

    public static final String UNTITLED_RECORD = "Untitled Record";

    private String title;
    private String name;
    private ArrayList<Artist> artists;
    private String filePath;
    private Date length;
    private Date dateReleased;
    private float rating;
    private boolean isRoyaltyFree;

    /*Constructor*/
    Record(String title, String name, ArrayList<Artist> artists, String filePath, Date length, Date dateReleased, float rating, boolean isRoyaltyFree){
        setTitle(title);
        setArtists(artists);
        setName(name);
        setLength(length);
        setFilePath(filePath);
        setDateReleased(dateReleased);
        setRating(rating);
        setIsRoyaltyFree(isRoyaltyFree);
    }

    /*Setters*/
    public void setTitle(String title){
        this.title = title;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setArtists(ArrayList<Artist> artists){
        this.artists = new ArrayList<>();
        this.artists.addAll(artists);
    }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public void setLength(Date length){
        this.length = length;
    }

    public void setDateReleased(Date dateReleased) { this.dateReleased = dateReleased; }

    //TODO: user should be able to set the rating
    public void setRating(float rating){
        this.rating = rating;
    }

    public void setIsRoyaltyFree(boolean isRoyaltyFree){
        this.isRoyaltyFree = isRoyaltyFree;
    }

    /*Getters*/
    public String getTitle(){
        return title;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Artist> getArtists(){
        return artists;
    }

    public Date getLength(){
        return length;
    }

    public Date getDateReleased(){
        return dateReleased;
    }

    public float getRating(){
        return rating;
    }

    public boolean getIsRoyaltyFree(){
        return isRoyaltyFree;
    }

    /*Other methods*/
    public String getLengthString(){
        return DateManager.getTimeString(length);
    }

    public String getDateReleasedString(){
        return DateManager.getDateString(dateReleased);
    }

    /**THREADING USED HERE*/
    public static Record generateRandomRecord(){
        RandomRecord randomRecord = new RandomRecord();
        Thread randomRecordThread = new Thread(randomRecord);

        try {
            randomRecordThread.start();
            randomRecordThread.join();
        }
        catch (InterruptedException e){
            //Do nothing
        }

        return randomRecord.getRecord();
    }

    private String getIsRoyaltyFreeString(){
        return isRoyaltyFree ? "Royalty free" : "Commercial";
    }

    private String getArtistsString(){
        /*Using StringBuilder for concatenation, since doing String + string is not efficient in loops*/
        StringBuilder builder = new StringBuilder();

        for (Artist a : artists)
            builder.append(a).append("<br>");

        return builder.toString();
    }

    public String getArtistInfoString(){
        return getArtistsString();
    }

    public String getRecordInfoString(){
        return getName() + "<br>" + "Date released: " + getDateReleasedString() + "<br>" + "Length: " + getLengthString() +  "<br>" + "Rating: " + rating + "<br>" + "License: " + getIsRoyaltyFreeString();
    }

    @Override
    public ArrayList<Pair<String, String>> getPaths(){
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>(getTitle(), filePath));

        return list;
    }

    @Override
    public String toString() {
        return title;
    }
}