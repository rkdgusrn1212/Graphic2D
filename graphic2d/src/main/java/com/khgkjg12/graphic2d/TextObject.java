package com.khgkjg12.graphic2d;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class TextObject extends Object {

    private Paint mPaint;
    private String mText;
    private int mTextSize;
    private Paint.FontMetrics mFontMetrics;
    private Rect mBound;

    public TextObject(@NonNull String text, Typeface typeface, int color, int textSize, float z, int x, int y, @Nullable String id){
        this(text, typeface, color, textSize, z, x, y, true, false, id);
    }

    public TextObject(Typeface typeface, int color, int textSize, float z, int x, int y, @Nullable String id){
        this(typeface, color, textSize, z, x, y, true, false, id);
    }

    public TextObject(Typeface typeface, int color, int textSize, float z, int x, int y, boolean visibility, boolean clickable, @Nullable String id) {
        this("", typeface, color, textSize, z, x, y, visibility, clickable, id);
    }

    public TextObject(@NonNull String text, Typeface typeface, int color, int textSize, float z, int x, int y, boolean visibility, boolean clickable, @Nullable String id) {
        super(z, x, y, visibility, clickable, id);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(typeface);
        mTextSize = textSize;
        mText = text;
        mBound = new Rect();
        mFontMetrics = new Paint.FontMetrics();
    }

    public void setText(@NonNull String text){
        mText = text;
    }

    public void setColor(int color){
        mPaint.setColor(color);
    }

    public void setTextSize(float textSize){
        mPaint.setTextSize(textSize);
    }


    @Override
    boolean checkBoundary(float scale, float renderX, float renderY, int x, int y) {
        float textSize = mPaint.getTextSize();
        mPaint.getFontMetrics(mFontMetrics);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        float halfWidth = mPaint.measureText(mText)/2;
        int left = (int)(renderX+mBound.left-halfWidth);
        int top = (int)(renderY+mBound.top-mFontMetrics.bottom+textSize/2);
        int right = (int)(renderX+mBound.right-halfWidth);
        int bottom = (int)(renderY+mBound.bottom-mFontMetrics.bottom+textSize/2);
        return x < right && x >= left && y < bottom && y >= top;
    }

    @Override
    void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY) {
        float scaledSize = mTextSize * scale;
        mPaint.setTextSize(scaledSize);
        drawer.drawText(mText, renderX, renderY+scaledSize/2-mFontMetrics.bottom*scale, mPaint);
    }
}