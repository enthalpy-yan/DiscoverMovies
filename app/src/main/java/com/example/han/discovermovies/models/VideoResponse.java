package com.example.han.discovermovies.models;

import java.util.List;

public class VideoResponse {
    private int id;
    private List<Video> results;

    public List<Video> getResults() {
        return results;
    }
}
