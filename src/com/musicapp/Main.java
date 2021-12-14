package com.musicapp;

import javafx.application.Platform;

/**
 * Music App, version 1.0
 * This application allows the user to create playlists and manage records inside them.
 * Made by: Kostas Ragauskas, group 3
 *
 * Task 8 - UML diagrams
 *
 * What's new:
 * --- Drawn UML class, sequence and use case diagrams.
 * --- Minor bugfixes.
 * */

public class Main{
    /*Constants*/
    private static final String APP_NAME = "Music Manager";
    private static final String ICON_PATH = "Icon.png";

    public static void main(String[] args) {
        //Initializing JavaFX on startup (plus the added modules in the VM options) fixes the warning.
        Platform.startup(()->{});

        MainFrame mainFrame = new MainFrame(APP_NAME, ICON_PATH);
        mainFrame.setVisible(true);
    }
}
