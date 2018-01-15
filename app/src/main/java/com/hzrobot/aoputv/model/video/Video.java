package com.hzrobot.aoputv.model.video;

/**
 * Created by shijiwei on 2018/1/11.
 *
 * @VERSION 1.0
 */

public class Video {

    private String path;
    private String name;
    private int playSeek;
    private boolean isPlaying = false;  /* 0为未播放状态，非0则处于播放状态*/

    public Video() {
    }

    public Video(String path, String name) {
        this(path, name, 0, false);
    }

    public Video(String path, String name, int playSeek, boolean isPlaying) {
        this.path = path;
        this.name = name;
        this.playSeek = playSeek;
        this.isPlaying = isPlaying;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaySeek() {
        return playSeek;
    }

    public void setPlaySeek(int playSeek) {
        this.playSeek = playSeek;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
