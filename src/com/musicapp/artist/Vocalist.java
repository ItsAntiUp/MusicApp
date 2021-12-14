package com.musicapp.artist;

import java.util.Date;

public class Vocalist extends Artist {
    public Vocalist(String name, Date dateOfBirth){
        super(name, dateOfBirth);
    }

    /*Other methods*/
    @Override
    public String getFieldOfWorkString(){
        return "Field of work: " + "Vocalist";
    }
}
