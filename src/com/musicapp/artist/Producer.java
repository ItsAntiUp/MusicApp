package com.musicapp.artist;

import java.util.Date;

public class Producer extends Artist {
    public Producer(String name, Date dateOfBirth){
        super(name, dateOfBirth);
    }

    /*Other methods*/
    @Override
    public String getFieldOfWorkString(){
        return "Field of work: " + "Producer";
    }
}
