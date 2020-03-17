package com.khgkjg12.graphic2d;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class TextObject extends PaintableObject {

    protected String mText;
    protected float mTextSize;
    protected Rect mBound = new Rect();
    protected float mLeft;
    protected float mRight;
    protected float mTop;
    protected float mBottom;
    protected float mScaledSize;
    protected float mTextWidth;
    protected float mTextHeight;

    public TextObject(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @NonNull String text, float textSize, Paint.Align textAlignment, @NonNull Typeface typeface) {
        super(z, x, y, visibility, clickable, color, autoShadow);
        mPaint.setTextAlign(textAlignment);
        mPaint.setTypeface(typeface);
        mTextSize = textSize;
        mText = text;
    }

    @WorkerThread
    public void changeTypeface(@NonNull Typeface Typeface){
        mPaint.setTypeface(Typeface);
        calculateAndCheckBoundary();
    }

    public Typeface getTypeface(){
        return mPaint.getTypeface();
    }

    public float getTextSize(){
        return mTextSize;
    }
    /**
     * world 콜백에서 호출.
     * @param text
     */
    @WorkerThread
    public void changeText(@NonNull String text){
        mText = text;
        calculateAndCheckBoundary();
    }

    public String getText(){
        return mText;
    }

    @WorkerThread
    public void changeTextSize(float textSize){
        mTextSize = textSize;
        calculateAndCheckBoundary();
    }

    @Override
    @WorkerThread
    void calculateBoundary() {
        mScaledSize = mTextSize * mScale;
        mPaint.setTextSize(mScaledSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        mTextHeight = mBound.height();
        mTextWidth = mBound.width();
        mTop = mRenderY - mTextHeight/2;
        mBottom = mTop + mTextHeight;
        Paint.Align align = mPaint.getTextAlign();
        if(align != Paint.Align.RIGHT){
            if(align == Paint.Align.CENTER) {
                mLeft = mRenderX-mTextWidth/2;
            }else {
                mLeft = mRenderX;
            }
            mRight = mLeft + mTextWidth;
        }else{
            mRight = mRenderX;
            mLeft = mRight - mTextWidth;
        }
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawText(mText, mRenderX, mBottom - mBound.bottom, mPaint);
    }

    @WorkerThread
    public float getTextWidth(){
        return mTextWidth;
    }

    @WorkerThread
    public float getTextHeight(){
        return mTextHeight;
    }

    @WorkerThread
    public void changeTextAlign(Paint.Align align){
        mPaint.setTextAlign(align);
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public Paint.Align getTextAlign(){
        return mPaint.getTextAlign();
    }
}