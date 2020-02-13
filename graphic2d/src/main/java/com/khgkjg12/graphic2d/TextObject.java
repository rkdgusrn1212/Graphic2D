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
    private float mLeft;
    private float mRight;
    private float mTop;
    private float mBottom;
    private float mRenderX;
    private float mRenderY;
    private float mScaledSize;

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
    void calculateBoundary(float scale, float renderX, float renderY) {
        mRenderX = renderX;
        mRenderY = renderY;
        mScaledSize = mTextSize * scale;
        mPaint.setTextSize(mScaledSize);
        mPaint.getFontMetrics(mFontMetrics);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        float halfWidth = mPaint.measureText(mText)/2;
        mLeft = renderX+mBound.left-halfWidth;
        mTop = renderY+mBound.top-mFontMetrics.bottom+mScaledSize/2;
        mRight = renderX+mBound.right-halfWidth;
        mBottom = renderY+mBound.bottom-mFontMetrics.bottom+mScaledSize/2;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }

    @Override
    void render(Graphic2dDrawer drawer) {
        drawer.drawText(mText, mRenderX, mRenderY+mScaledSize/2-mFontMetrics.bottom, mPaint);
    }
}