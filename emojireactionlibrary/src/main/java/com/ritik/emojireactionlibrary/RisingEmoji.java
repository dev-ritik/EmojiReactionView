package com.ritik.emojireactionlibrary;

import android.graphics.Paint;
import android.graphics.Rect;

public class RisingEmoji {
    private Rect rect;
    private int halfSide;
    private int maxHeight;
    private int speed;
    private Paint paint;

    public RisingEmoji() {
    }

    public RisingEmoji(Rect rect, int halfSide) {
        this.rect = rect;
        this.halfSide = halfSide;
    }

    public RisingEmoji(Rect rect, int halfSide, int maxHeight, int speed) {
        this.rect = rect;
        this.halfSide = halfSide;
        this.maxHeight = maxHeight;
        this.speed = speed;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getHalfSide() {
        return halfSide;
    }

    public void setHalfSide(int halfSide) {
        this.halfSide = halfSide;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
