package com.khgkjg12.graphic2d;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

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
    private float mScaledSize;

    public TextObject(@NonNull String text, Typeface typeface, int color, int textSize, float z, int x, int y, OnClickListener onClickListener){
        this(text, typeface, color, textSize, z, x, y, true, false, onClickListener);
    }

    public TextObject(Typeface typeface, int color, int textSize, float z, int x, int y, OnClickListener onClickListener){
        this(typeface, color, textSize, z, x, y, true, false, onClickListener);
    }

    public TextObject(Typeface typeface, int color, int textSize, float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener) {
        this("", typeface, color, textSize, z, x, y, visibility, clickable, onClickListener);
    }

    public TextObject(@NonNull String text, Typeface typeface, int color, int textSize, float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener) {
        super(z, x, y, visibility, clickable, onClickListener);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(typeface);
        mTextSize = textSize;
        mText = text;
        mBound = new Rect();
        mFontMetrics = new Paint.FontMetrics();
    }

    /**
     * world 콜백에서 호출.
     * @param text
     */
    @WorkerThread
    public void setText(@NonNull String text){
        mText = text;
        calculateBoundary();
    }

    /**
     * world 콜백에서 호출.
     * @param color
     */
    @WorkerThread
    public void setColor(int color){
        mPaint.setColor(color);
    }

    @WorkerThread
    public void setTextSize(float textSize){
        mPaint.setTextSize(textSize);
        calculateBoundary();
    }

    @Override
    @WorkerThread
    void calculateBoundary() {
        mScaledSize = mTextSize * mScale;
        mPaint.setTextSize(mScaledSize);
        mPaint.getFontMetrics(mFontMetrics);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        float halfWidth = mPaint.measureText(mText)/2;
        mLeft = mRenderX+mBound.left-halfWidth;
        mTop = mRenderY+mBound.top-mFontMetrics.bottom+mScaledSize/2;
        mRight = mRenderX+mBound.right-halfWidth;
        mBottom = mRenderY+mBound.bottom-mFontMetrics.bottom+mScaledSize/2;
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawText(mText, mRenderX, mRenderY+mScaledSize/2-mFontMetrics.bottom, mPaint);
    }
}