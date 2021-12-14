package com.musicapp.record;

import com.musicapp.artist.Artist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.musicapp.record.Record.UNTITLED_RECORD;

public class RandomRecord implements Runnable {
    private Record record;

    /**Volatile variables just so all threads could see the most recent values of them.*/
    private volatile String artistName;
    private volatile String recordName;
    private volatile String title;
    private volatile String recordPath;

    public Record getRecord(){
        return record;
    }

    public void run(){
        /**Synchronization here is done purely by volatile variables (since these operations are all atomic).*/
        artistName = Artist.UNTITLED_ARTIST;
        recordName = UNTITLED_RECORD;
        recordPath = "";

        /**Synchronizing this block so that only one thread could modify it at the time.*/
        synchronized (this) {
            Random rand = new Random();

            title = artistName + " " + recordName; //Not an atomic operation, since we cannot use synchronization by volatile variables.

            /*Calendar for generating dates*/
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 1910);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date length = calendar.getTime();

            calendar.set(Calendar.YEAR, 1900);
            Date dateReleased = calendar.getTime();

            ArrayList<Artist> artists = new ArrayList<>();

            float rating = Math.round(rand.nextFloat() * 10 * 100) / 100.0f;
            boolean isRoyaltyFree = rand.nextBoolean();

            record = new Record(title, recordName, artists, recordPath, length, dateReleased, rating, isRoyaltyFree);
        }
    }
}
