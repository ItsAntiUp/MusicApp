package com.musicapp.utility;

import com.musicapp.record.Record;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SortManager {
    public static final String[] sortByValues = {
            "Default",
            "Record title (ascending)",
            "Record title (descending)",
            "Record name (ascending)",
            "Record name (descending)",
            "Date released (ascending)",
            "Date released (descending)",
            "Rating (ascending)",
            "Rating (descending)"
    };

    //Using stream().sorted and not Collections.sort(), since we need to create a new sorted list and leave the input playlist unchanged.
    public static ArrayList<Record> getSortedValues(ArrayList<Record> playlist, int sortByIndex){
        switch(sortByIndex){
            case 1:
                return playlist.stream().sorted(titleASC).collect(Collectors.toCollection(ArrayList::new));
            case 2:
                return playlist.stream().sorted(titleDSC).collect(Collectors.toCollection(ArrayList::new));
            case 3:
                return playlist.stream().sorted(nameASC).collect(Collectors.toCollection(ArrayList::new));
            case 4:
                return playlist.stream().sorted(nameDSC).collect(Collectors.toCollection(ArrayList::new));
            case 5:
                return playlist.stream().sorted(dateASC).collect(Collectors.toCollection(ArrayList::new));
            case 6:
                return playlist.stream().sorted(dateDSC).collect(Collectors.toCollection(ArrayList::new));
            case 7:
                return playlist.stream().sorted(ratingASC).collect(Collectors.toCollection(ArrayList::new));
            case 8:
                return playlist.stream().sorted(ratingDSC).collect(Collectors.toCollection(ArrayList::new));
            default:
                return playlist;
        }
    }

    private static Comparator<Record> titleASC = (o1, o2) -> o1.getTitle().compareTo(o2.getTitle());

    private static Comparator<Record> titleDSC = (o1, o2) -> -o1.getTitle().compareTo(o2.getTitle());

    private static Comparator<Record> nameASC = (o1, o2) -> o1.getName().compareTo(o2.getName());

    private static Comparator<Record> nameDSC = (o1, o2) -> -o1.getName().compareTo(o2.getName());

    private static Comparator<Record> dateASC = (o1, o2) -> o1.getDateReleased().compareTo(o2.getDateReleased());

    private static Comparator<Record> dateDSC = (o1, o2) -> -o1.getDateReleased().compareTo(o2.getDateReleased());

    private static Comparator<Record> ratingASC = (o1, o2) -> Float.compare(o1.getRating(), o2.getRating());

    private static Comparator<Record> ratingDSC = (o1, o2) -> -Float.compare(o1.getRating(), o2.getRating());
}
