package com.ritik.emojireactionview;

/**
 * This class is the object for displaying sample feeds
 */

class Feed {
    private String name;
    private int picAddress;
    private String time;
    private String message;
    private int clickedEmoji;

    Feed(String name, int picAddress, String time, String message, int clickedEmoji) {
        this.name = name;
        this.picAddress = picAddress;
        this.time = time;
        this.message = message;
        this.clickedEmoji = clickedEmoji;
    }

    public int getClickedEmoji() {
        return clickedEmoji;
    }

    public void setClickedEmoji(int clickedEmoji) {
        this.clickedEmoji = clickedEmoji;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(int picAddress) {
        this.picAddress = picAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
