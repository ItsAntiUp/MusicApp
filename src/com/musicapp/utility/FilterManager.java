package com.musicapp.utility;

import com.musicapp.record.Record;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public class FilterManager {
    public static final String[] filterByValues = {
            "Default",
            "Royalty free",
            "Has more than one artist",
            "Released in the last 30 days"
    };

    //Using stream().filter, since we need to create a new sorted list and leave the input playlist unchanged.
    public static ArrayList<Record> getFilteredValues(ArrayList<Record> playlist, int filterByIndex){
        switch(filterByIndex){
            case 1:
                return playlist.stream().filter(Record::getIsRoyaltyFree).collect(Collectors.toCollection(ArrayList::new));
            case 2:
                return playlist.stream().filter(record -> record.getArtists().size() > 1).collect(Collectors.toCollection(ArrayList::new));
            case 3: {
                //Setting a date, which is a month earlier than the current one
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);

                return playlist.stream().filter(record -> record.getDateReleased().getTime() > calendar.getTime().getTime()).collect(Collectors.toCollection(ArrayList::new));
            }
            default:
                return playlist;
        }
    }
}
