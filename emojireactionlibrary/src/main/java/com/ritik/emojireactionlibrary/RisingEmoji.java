package com.ritik.emojireactionlibrary;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * This class deals with properties of risingEmoji Rect
 */

class RisingEmoji {
    private Rect rect;
    private int speed;
    private Paint paint;

    RisingEmoji(Rect rect, int speed) {
        this.rect = rect;
        this.speed = speed;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
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
