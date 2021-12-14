package com.musicapp.artist;

import java.util.Date;

public class Musician extends Artist {
    public Musician(){
        super();
    }

    public Musician(String name, Date dateOfBirth){
        super(name, dateOfBirth);
    }

    /*Other methods*/
    @Override
    public String getFieldOfWorkString(){
        return "Field of work: " + "Musician";
    }
}
