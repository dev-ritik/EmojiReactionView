package com.ritik.emojireactionlibrary;

public interface ClickInterface {

    void onEmojiClicked(int emojiIndex,int x, int y);

    void onEmojiUnclicked(int emojiIndex,int x, int y);

}