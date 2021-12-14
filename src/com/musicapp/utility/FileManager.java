package com.musicapp.utility;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javafx.util.*;

public class FileManager {
    public static ArrayList<File> getFiles(final String description, final String ...extensions){
        /*Choosing a file (or multiple files) with provided extensions from the user's PC*/
        ArrayList<File> result = new ArrayList<>();
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        chooser.setFileFilter(filter);

        int temp_val = chooser.showOpenDialog(null);
        if(temp_val == JFileChooser.APPROVE_OPTION)
            Collections.addAll(result, chooser.getSelectedFiles());

        return result;
    }

    public static ArrayList<Object> splitFilename(String fileName){
        /*All files should have an extension in the end (e.g .mp3)*/
        if(!fileName.contains("."))
            return null;

        /*Obtaining the file name without extension and creating a pair array for the artist values*/
        int extensionIndex = fileName.lastIndexOf('.');
        String nameWithoutFormat = fileName.substring(0, extensionIndex);

        ArrayList<Pair<String, String>> artistData = new ArrayList<>();
        ArrayList<String> parts = new ArrayList<>();
        String recordName;

        /*Firstly, trying to split the record title into two (or more) parts:*/
        if(nameWithoutFormat.contains(" — "))
            Collections.addAll(parts, nameWithoutFormat.split(" — "));
        else if(nameWithoutFormat.contains(" - "))
            Collections.addAll(parts, nameWithoutFormat.split(" - "));
        else
            parts.add(nameWithoutFormat);

        /*In each of the parts, searching for the vocalist (differentiating with ft. feat., etc.)*/
        ArrayList<String> delimiters = new ArrayList<>();
        String realDelimiter = "ft.";

        for(int i = 0; i < parts.size(); ++i) {
            String tempDelimiter = "";
            delimiters.clear();

            String artistStr = parts.get(i);

            if (artistStr.contains("ft."))
                tempDelimiter = "ft.";
            else if (artistStr.contains("Ft."))
                tempDelimiter = "Ft.";
            else if (artistStr.contains("feat."))
                tempDelimiter = "feat.";
            else if (artistStr.contains("Feat."))
                tempDelimiter = "Feat.";

            if(tempDelimiter != "") {
                parts.set(i, artistStr.split(tempDelimiter)[0]);
                artistStr = artistStr.split(tempDelimiter)[1];

                if (artistStr.contains(", "))
                    delimiters.add(", ");
                if (artistStr.contains(" x "))
                    delimiters.add(" x ");
                if (artistStr.contains(" & "))
                    delimiters.add(" & ");

                ArrayList<String> tempList = new ArrayList<>();

                for (String s : delimiters)
                    artistStr = artistStr.replace(s, realDelimiter);

                Collections.addAll(tempList, artistStr.split(realDelimiter));

                /*Getting the vocalists from our temporary list*/
                Iterator<String> it = tempList.iterator();
                while (it.hasNext()) {
                    String s = it.next();

                    if (s.contains(")"))
                        s = s.substring(0, s.lastIndexOf(")"));

                    artistData.add(new Pair<>("Vocalist", s.trim()));
                }
            }
        }

        if(parts.size() == 1) {
            recordName = nameWithoutFormat.trim();
            artistData.add(new Pair<>("Unknown","Unknown artist"));
        }
        else {
            /*After the split, the remaining string will be either the record name or the producers*/
            delimiters.clear();
            realDelimiter = " & ";

            /*In the first part, searching for the producers (differentiating with x and , for now)*/
            if (parts.get(0).contains(" x "))
                delimiters.add(" x ");
            if (parts.get(0).contains(", "))
                delimiters.add(", ");

            /*If more than one artist is found*/
            ArrayList<String> tempStr = new ArrayList<>();

            for(String s : delimiters)
                parts.set(0, parts.get(0).replace(s, realDelimiter));

            Collections.addAll(tempStr, parts.get(0).split(realDelimiter));

            for (String s : tempStr)
                artistData.add(new Pair<>("Producer", s.trim()));

            //Getting the artist, who remixed the record (inside parentheses). (Mixes are an entirely different thing, hence they are excluded)
            if(parts.get(1).contains("(") && parts.get(1).contains(")") && !parts.get(1).contains("Mix")) {
                String remixArtist = parts.get(1).substring(parts.get(1).indexOf("(") + 1, parts.get(1).indexOf(")"));

                if(remixArtist.contains(" ")) {
                    remixArtist = remixArtist.substring(0, remixArtist.lastIndexOf(" "));
                    artistData.add(new Pair<>("Producer", remixArtist.trim()));

                    parts.set(1, parts.get(1).substring(0, parts.get(1).indexOf("(")));
                }
            }

            recordName = parts.get(1).trim();
        }

        if(recordName.contains("("))
            recordName = recordName.substring(0, recordName.lastIndexOf("(")).trim();

        ArrayList<Object> result = new ArrayList<>();
        result.add(artistData);
        result.add(recordName);
        result.add(nameWithoutFormat);

        /*Returning an object array, which contains the map with the artist values, the record name and the full title.*/
        return result;
    }
}
