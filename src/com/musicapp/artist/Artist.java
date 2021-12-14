package com.musicapp.artist;

import com.musicapp.utility.DateManager;

import java.util.Date;

public abstract class Artist implements java.io.Serializable {
    private String name;

    /*Constants*/
    public static final String UNTITLED_ARTIST = "Untitled Artist";

    /*Constructors*/
    public Artist(){
        this(UNTITLED_ARTIST, new Date());
    }

    public Artist(String name, Date dateOfBirth){
        setName(name);
    }

    /*Setters*/
    public void setName(String name){
        this.name = name;
    }

    /*Getters*/
    public String getName(){
        return name;
    }

    /*Other methods*/
    public abstract String getFieldOfWorkString();

    @Override
    public String toString(){
        return name + ", " + getFieldOfWorkString();
    }
}
