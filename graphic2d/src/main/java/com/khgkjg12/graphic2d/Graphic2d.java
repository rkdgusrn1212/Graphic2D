package com.khgkjg12.graphic2d;

import android.graphics.Bitmap;

public class Graphic2d {

    public static enum Format {
        ARGB8888, ARGB4444, RGB565
    }

    Bitmap bitmap;
    Format format;

    public Graphic2d(Bitmap bitmap, Format format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public Format getFormat() {
        return format;
    }

    public void dispose() {
        bitmap.recycle();
    }
}
