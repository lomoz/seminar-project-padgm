package com.example.paul.hashtagworldmap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 11.09.16.
 */
public class SavedLocations {

    ArrayList<Data> infoList = new ArrayList<>();
    ArrayList<ArrayList<Data>> list = new ArrayList<>();

    public void setNewLocations(ArrayList<Data> infoList) {
        this.infoList = infoList;
        list.add(this.infoList);

    }

    public ArrayList<ArrayList<Data>> getSavedLocations() throws IOException {

        return list;

    }
}