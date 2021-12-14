package com.musicapp;

import com.musicapp.exception.FailedSaveLoadException;
import com.musicapp.player.Player;
import com.musicapp.record.Playlist;
import com.musicapp.record.Record;
import com.musicapp.saveload.LoadManager;
import com.musicapp.saveload.SaveManager;
import com.musicapp.utility.FileManager;
import com.musicapp.utility.FilterManager;
import com.musicapp.utility.SortManager;
import javafx.scene.media.MediaException;
import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainFrame extends JFrame{
    /*Unused components*/
    private JPanel playlistActionPanel;
    private JPanel recordActionPanel;
    private JPanel InfoPanel;
    private JPanel sortFilterPanel;
    private JPanel playerPanel;

    /*Actual components*/
    private JPanel mainPanel;
    private JButton addRecordsButton;
    private JLabel artistInfoText;
    private JLabel songInfoText;
    private JButton addPlaylistButton;
    private JButton deleteRecordsButton;
    private JButton saveProgressButton;

    private JLabel sortByLabel;
    private JLabel filterByLabel;
    private JComboBox<String> sortComboBox;
    private JComboBox<String> filterComboBox;

    private JTabbedPane playlistTab;
    private JButton deletePlaylistButton;

    private JPanel currentlyPlayingPanel;
    private JButton playSelectedButton;
    private JButton playStopButton;
    private JButton nextRecordButton;
    private JButton previousRecordButton;
    private JCheckBox shuffleCheckBox;

    /*Components used directly in the Player class*/
    private JLabel currentlyPlayingLabel;
    private JLabel currentlyPlayingValue;
    private JSlider currentlyPlayingSlider;
    private JLabel currentlyPlayingTime;
    private JSlider volumeSlider;
    private JLabel volumePercentage;

    private JLabel artistInfoTitle;
    private JLabel recordInfoTitle;

    private ArrayList<Playlist> playlists;

    private Player player;

    private static final String SAVE_FILE_NAME = "savefile.txt";

    /*Constructor*/
    public MainFrame(String appName, String iconPath){
        /*Setting the main settings of the frame*/
        initValues(appName, iconPath);
        enableAll(false);

        player = Player.getInstance();
        player.bindComponents(currentlyPlayingLabel, currentlyPlayingValue, currentlyPlayingSlider, currentlyPlayingTime, volumeSlider, volumePercentage);

        /*Adding listeners to various components in the jframe*/
        addPlaylistButton.addActionListener(e -> addPlaylist());
        deletePlaylistButton.addActionListener(e -> deletePlaylist());
        addRecordsButton.addActionListener(e -> addRecords());
        deleteRecordsButton.addActionListener(e -> deleteRecords());

        sortComboBox.setModel(new DefaultComboBoxModel<>(SortManager.sortByValues));
        filterComboBox.setModel(new DefaultComboBoxModel<>(FilterManager.filterByValues));
        sortComboBox.addActionListener(e -> sortPlaylist());
        filterComboBox.addActionListener(e -> filterPlaylist());

        /*Load and Save functions*/
        loadProgress();
        saveProgressButton.addActionListener(e -> saveProgress());

        playSelectedButton.addActionListener(e -> playSelected());
        playStopButton.addActionListener(e -> player.playStop());
        nextRecordButton.addActionListener(e -> player.playNext());
        previousRecordButton.addActionListener(e -> player.playPrevious());
        shuffleCheckBox.addActionListener(e -> player.setIsShuffled(shuffleCheckBox.isSelected()));
    }

    private void initValues(String appName, String iconPath){
        setContentPane(mainPanel);
        setTitle(appName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1024, 640);

        ImageIcon img = new ImageIcon(iconPath);
        setIconImage(img.getImage());

        playlists = new ArrayList<>();
    }

    private void addPlaylist(){
        if(playlists.size() >= 10){
            JOptionPane.showMessageDialog(mainPanel, new JLabel(Playlist.MAX_PLAYLIST_NUMBER_REACHED));
            return;
        }

        DefaultListModel<Record> default_model = new DefaultListModel<>();
        JList<Record> list = new JList<>();
        list.setModel(default_model);

        //TODO: the user should be able to change the name of the playlist
        Playlist playlist = new Playlist("Playlist: " + (playlistTab.getTabCount() + 1));

        deletePlaylistButton.setVisible(true);
        addRecordsButton.setVisible(true);

        /*If a particular record is selected, display its information.*/
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                onListAction(list);
        });

        list.setSelectedIndex(0);
        playlists.add(playlist);

        JScrollPane pane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        playlistTab.addTab(playlist.getName(), pane);
        playlistTab.addChangeListener(e -> onSwitchedTabs());
    }

    private void deletePlaylist(){
        int index = playlistTab.getSelectedIndex();
        /*playlists.get(index).deleteAllRecords();*/

        removeDeletedPlayerRecords(playlists.get(index).getRecords());

        playlists.remove(index);
        playlistTab.remove(index);

        if(playlists.isEmpty()){
            enableAll(false);
            return;
        }

        /*Renaming the playlists*/
        for(int i = index; i < playlists.size(); ++i) {
            String name = "Playlist: " + (i + 1);
            playlists.get(i).setName(name);
            playlistTab.setTitleAt(index, name);
        }
    }

    private void addRecords(){
        int index = playlistTab.getSelectedIndex();
        JList<Record> list = (JList<Record>)(((JScrollPane)playlistTab.getComponentAt(index)).getViewport().getComponent(0));

        int elements = list.getModel().getSize();

        Playlist currentPlaylist = getCurrentPlaylist();
        ArrayList<File> files = FileManager.getFiles(Record.FORMAT_DESCRIPTION, Record.MP3, Record.WAV, Record.FLAC, Record.M4A);

        if(files.size() > 0) {
            currentPlaylist.addRecordsFromFile(files);
            updatePlaylist(list, currentPlaylist.getRecords());

            /*If the list was empty before, enable the options*/
            if (elements == 0 && currentPlaylist.getRecords().size() != 0)
                enableSortFilter(true);

            resetSortFilterBoxes();
        }
    }

    private void deleteRecords() {
        int index = playlistTab.getSelectedIndex();
        JList<Record> list = (JList<Record>) (((JScrollPane) playlistTab.getComponentAt(index)).getViewport().getComponent(0));

        if (list.getModel().getSize() <= 0)
            return;

        ArrayList<Record> selectedRecords = new ArrayList<>(list.getSelectedValuesList());
        removeDeletedPlayerRecords(selectedRecords);

        Playlist currentPlaylist = getCurrentPlaylist();
        currentPlaylist.deleteRecords(selectedRecords);
        updatePlaylist(list, currentPlaylist.getRecords());

        resetSortFilterBoxes();
    }

    private void playSelected(){
        try {
            int index = playlistTab.getSelectedIndex();
            ArrayList<Record> selectedValues = new ArrayList<>();
            selectedValues.addAll(((JList<Record>) (((JScrollPane) playlistTab.getComponentAt(index)).getViewport().getComponent(0))).getSelectedValuesList());
            Playlist toPlay = new Playlist("temp", selectedValues);

            player.setValues(toPlay, shuffleCheckBox.isSelected());
            player.playAll();

            currentlyPlayingPanel.setVisible(true);
        }
        catch(MediaException e){
            JOptionPane.showMessageDialog(mainPanel, new JLabel(e.getLocalizedMessage()));
        }
    }

    private void removeDeletedPlayerRecords(ArrayList<Record> records){
        if(player.getPaths() == null) //If player is not initialized properly
            return;

        ArrayList<Pair<String, String>> playerPaths = new ArrayList<>(player.getPaths());

        if(playerPaths.size() <= 0)
            return;

        boolean containsAtLeastOne = false;
        boolean isRecordCurrentlyPlaying = false;

        for(Record r : records){
            Pair<String,String> recordPath = r.getPaths().get(0);

            if(playerPaths.contains(recordPath)) {
                if(recordPath.equals(player.getCurrentlyPlayingPath()))
                    isRecordCurrentlyPlaying = true;

                containsAtLeastOne = true;
                playerPaths.remove(recordPath);
            }
        }

        if(containsAtLeastOne) {
            //TODO:indexes are not entirely correct
            if(playerPaths.size() == 0){
                player.stopPlaying();
                currentlyPlayingPanel.setVisible(false);
                return;
            }

            player.setPaths(playerPaths);
            player.setCurrentlyPlayingIndex(0);
            player.updateCurrentlyPlayingTitle();

            if(isRecordCurrentlyPlaying) {
                player.stopPlaying();
                player.playAll();
            }
        }
    }

    private Playlist getCurrentPlaylist(){
        int index = playlistTab.getSelectedIndex();
        return playlists.get(index);
    }

    /*Displays all the playlist records on the jlist.*/
    private void updatePlaylist(JList<Record> list, ArrayList<Record> records){
        DefaultListModel<Record> default_model = new DefaultListModel<>();

        for(Record r : records)
            default_model.addElement(r);

        list.setModel(default_model);
    }

    private void onListAction(JList<Record> list){
        Record selected = list.getSelectedValue();

        if(selected != null) {
            artistInfoText.setText("<html>" + selected.getArtistInfoString() + "</html>");
            songInfoText.setText("<html>" + selected.getRecordInfoString() + "</html>");
            enableRecordActions(true);
        }
        else
            enableRecordActions(false);
    }

    private void onSwitchedTabs(){
        if(playlistTab.getTabCount() == 0)
            return;

        int index = playlistTab.getSelectedIndex();
        JList<Record> listNew = (JList<Record>)(((JScrollPane)playlistTab.getComponentAt(index)).getViewport().getComponent(0));
        updatePlaylist(listNew, getCurrentPlaylist().getRecords());
        resetSortFilterBoxes();

        boolean enableSortFilterBool = listNew.getModel().getSize() != 0;
        enableSortFilter(enableSortFilterBool);
        onListAction(listNew);
    }

    private void sortPlaylist(){
        int index = playlistTab.getSelectedIndex();
        JList<Record> list = (JList<Record>)(((JScrollPane)playlistTab.getComponentAt(index)).getViewport().getComponent(0));
        Playlist currentPlaylist = getCurrentPlaylist();

        ArrayList<Record> sortedValues = SortManager.getSortedValues(currentPlaylist.getRecords(), sortComboBox.getSelectedIndex());

        if(filterComboBox.getSelectedIndex() != 0)
            sortedValues = FilterManager.getFilteredValues(sortedValues, filterComboBox.getSelectedIndex());

        updatePlaylist(list, sortedValues);
    }

    private void filterPlaylist(){
        int index = playlistTab.getSelectedIndex();
        JList<Record> list = (JList<Record>)(((JScrollPane)playlistTab.getComponentAt(index)).getViewport().getComponent(0));
        Playlist currentPlaylist = getCurrentPlaylist();

        ArrayList<Record> filteredValues = FilterManager.getFilteredValues(currentPlaylist.getRecords(), filterComboBox.getSelectedIndex());

        if(sortComboBox.getSelectedIndex() != 0)
            filteredValues = SortManager.getSortedValues(filteredValues, sortComboBox.getSelectedIndex());

        updatePlaylist(list, filteredValues);
    }

    private void saveProgress(){
        try {
            SaveManager saveManager = new SaveManager(playlists);
            saveManager.save(SAVE_FILE_NAME);
            JOptionPane.showMessageDialog(mainPanel, new JLabel(SaveManager.SAVE_SUCCESSFUL_MSG + SAVE_FILE_NAME));
        }
        catch(FailedSaveLoadException e){
            //Getting the cause message (if there is one)
            JOptionPane.showMessageDialog(mainPanel, new JLabel(e.getLocalizedMessage()));
        }
    }

    private void loadProgress(){
        ArrayList<Playlist> tempPlaylists;

        try {
            LoadManager loadManager = new LoadManager(playlists);
            loadManager.load(SAVE_FILE_NAME);
            tempPlaylists = loadManager.getAll();
        }
        catch(FailedSaveLoadException e){
            //Do nothing, if the cause is file not found.
            if(e.getCause() instanceof IOException)
                return;

            //Otherwise, print a message
            JOptionPane.showMessageDialog(mainPanel, new JLabel(e.getMessage()));
            return;
        }

        //If nothing is loaded (without exception) - return
        if(tempPlaylists.size() == 0)
            return;

        boolean isEnabled = false;

        for (int i = 0; i < tempPlaylists.size(); ++i) {
            addPlaylist();
            playlists.set(i, tempPlaylists.get(i));
            JList<Record> list = (JList<Record>) (((JScrollPane) playlistTab.getComponentAt(i)).getViewport().getComponent(0));
            updatePlaylist(list, playlists.get(i).getRecords());

            if(playlists.get(i).getRecords().size() != 0)
                isEnabled = true;
        }

        enableSortFilter(isEnabled);
        resetSortFilterBoxes();
    }

    private void enableAll(boolean isEnabled){
        deletePlaylistButton.setVisible(isEnabled);
        playSelectedButton.setVisible(isEnabled);
        currentlyPlayingPanel.setVisible(isEnabled);
        deleteRecordsButton.setVisible(isEnabled);
        addRecordsButton.setVisible(isEnabled);

        enableSortFilter(isEnabled);
        enableRecordActions(isEnabled);
    }

    //On record clicked
    private void enableRecordActions(boolean isEnabled){
        artistInfoTitle.setVisible(isEnabled);
        recordInfoTitle.setVisible(isEnabled);
        artistInfoText.setVisible(isEnabled);
        songInfoText.setVisible(isEnabled);
        playSelectedButton.setVisible(isEnabled);
        deleteRecordsButton.setVisible(isEnabled);
    }

    private void enableSortFilter(boolean isEnabled){
        sortByLabel.setVisible(isEnabled);
        filterByLabel.setVisible(isEnabled);
        sortComboBox.setVisible(isEnabled);
        filterComboBox.setVisible(isEnabled);
    }

    private void resetSortFilterBoxes(){
        sortComboBox.setSelectedIndex(0);
        filterComboBox.setSelectedIndex(0);
    }
}

