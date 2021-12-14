package com.musicapp.player;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Player {
    //The player itself is static, just so we can stop the current player before playing another song.

    private static Player instance;

    private ArrayList<Pair<String, String>> paths;
    private ArrayList<Pair<String, String>> playablePaths;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int currentlyPlayingIndex;

    private JLabel currentlyPlayingTitle;
    private JLabel currentlyPlayingText;
    private JSlider currentlyPlayingSlider;
    private JLabel currentlyPlayingTime;
    private JSlider volumeSlider;
    private JLabel volumePercentage;

    //Singleton design patterns
    private Player(){
    }

    public static Player getInstance(){
        instance = (instance == null) ? new Player() : instance;
        return instance;
    }

    public void bindComponents(JLabel title, JLabel text, JSlider slider, JLabel time, JSlider volume, JLabel percentage){
        currentlyPlayingTitle = title;
        currentlyPlayingText = text;
        currentlyPlayingSlider = slider;
        currentlyPlayingTime = time;
        volumeSlider = volume;
        volumePercentage = percentage;
        currentlyPlayingIndex = 0;
    }

    public void setValues(Playable playable, boolean shuffle){
        stopPlaying();
        currentlyPlayingIndex = 0;

        //Setting two ArrayLists, just so we can bring back the original one after shuffling the records.
        setPaths(playable.getPaths());
        setIsShuffled(shuffle);

        currentlyPlayingSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e){
                currentlyPlayingSlider.setValue(getLocationInSlider(e, currentlyPlayingSlider, true));
                currentlyPlayingTime.setText(getTimeString(getLocationInSlider(e, currentlyPlayingSlider, true)));
                mediaPlayer.seek(Duration.seconds(currentlyPlayingSlider.getValue()));
            }
        });

        volumeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e){
                volumeSlider.setValue(getLocationInSlider(e, volumeSlider, false));
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0f);
                volumePercentage.setText(volumeSlider.getValue() + "%");
            }
        });

        volumeSlider.addChangeListener(e -> {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0f);
            volumePercentage.setText(volumeSlider.getValue() + "%");
        });
    }

    public void playAll() throws MediaException{
        if(currentlyPlayingIndex == playablePaths.size() || currentlyPlayingIndex < 0) {
            //Stop if there was only one record playing
            if(playablePaths.size() == 1){
                stopPlaying();
                return;
            }

            //Otherwise, loop the playlist
            currentlyPlayingIndex = (currentlyPlayingIndex >= playablePaths.size()) ? 0 : (playablePaths.size() - 1);
        }

        try {
            Media playableRecord = new Media(new File(playablePaths.get(currentlyPlayingIndex).getValue()).toURI().toString());
            mediaPlayer = new MediaPlayer(playableRecord);

            //Playing the actual record only when the player is ready (to avoid errors)
            mediaPlayer.setOnReady(() -> {
                currentlyPlayingSlider.setMaximum((int)mediaPlayer.getTotalDuration().toSeconds());
                currentlyPlayingSlider.setValue(0);

                mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
                    int seconds = (int)newValue.toSeconds();
                    currentlyPlayingSlider.setValue(seconds);
                    currentlyPlayingTime.setText(getTimeString(seconds));
                });

                //When the record ends, play the next one until there are no left.
                mediaPlayer.setOnEndOfMedia(this::playNext);

                mediaPlayer.play();
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0f);
                volumePercentage.setText(volumeSlider.getValue() + "%");

                isPlaying = true;
                updateCurrentlyPlayingTitle();
                updateCurrentlyPlayingText(playablePaths.get(currentlyPlayingIndex).getKey());
            });
        }
        catch(MediaException e){
            //Rethrowing the exception for the MainFrame to catch
            throw e;
        }
    }

    public void playNext(){
        stopPlaying();

        if(currentlyPlayingIndex < playablePaths.size() && currentlyPlayingIndex >= 0)
             ++currentlyPlayingIndex;

        playAll();
    }

    public void playPrevious(){
        stopPlaying();

        if(currentlyPlayingIndex < playablePaths.size() && currentlyPlayingIndex >= 0)
            --currentlyPlayingIndex;

        playAll();
    }

    private void updateCurrentlyPlayingText(final String text){
        currentlyPlayingText.setText(text);
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void stopPlaying(){
        if(mediaPlayer != null)
            mediaPlayer.pause();

        isPlaying = false;
    }

    public void continuePlaying(){
        if(mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    public void playStop(){
        if(getIsPlaying())
            stopPlaying();
        else
            continuePlaying();
    }

    public void setIsShuffled(boolean shuffle){
        if(playablePaths.size() > 1) {
            if(shuffle)
                Collections.shuffle(playablePaths);
            else {
                currentlyPlayingIndex = paths.indexOf(playablePaths.get(currentlyPlayingIndex));
                playablePaths = new ArrayList<>(paths);
            }
        }
    }

    public void updateCurrentlyPlayingTitle(){
        currentlyPlayingTitle.setText("Currently playing: (" + (paths.indexOf(playablePaths.get(currentlyPlayingIndex)) + 1) + "/" + playablePaths.size() + ")");
    }

    public Pair<String, String> getCurrentlyPlayingPath(){
        return playablePaths.get(currentlyPlayingIndex);
    }

    public ArrayList<Pair<String, String>> getPaths(){
        return paths;
    }

    public void setPaths(ArrayList<Pair<String, String>> paths){
        this.paths = new ArrayList<>(paths);
        playablePaths = new ArrayList<>(paths);
    }

    public void setCurrentlyPlayingIndex(int index){
        currentlyPlayingIndex = index;
    }

    private int getLocationInSlider(MouseEvent e, JSlider slider, boolean isHorizontal){
        Point p = e.getPoint();

        double widthHeight = isHorizontal ? (double)slider.getWidth() : (double)slider.getHeight();
        int xy = isHorizontal ? p.x : p.y;

        double percent = xy / (widthHeight);
        int range = slider.getMaximum() - slider.getMinimum();
        double newVal = range * percent;
        int result = (int)Math.round(slider.getMinimum() + newVal);

        return isHorizontal ? result : 100 - result;
    }


    private String getTimeString(int totalSeconds){
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = (totalSeconds % 3600) % 60;

        return hours == 0 ? String.format("%02d:%02d", minutes, seconds) : String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
