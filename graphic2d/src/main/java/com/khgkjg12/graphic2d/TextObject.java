package com.khgkjg12.graphic2d;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class TextObject extends PaintableObject {

    private String mText;
    private float mTextSize;
    private Rect mBound;
    private float mLeft;
    private float mRight;
    private float mTop;
    private float mBottom;
    private float mScaledSize;
    private float mTextWidth;
    private float mTextHeight;

    public TextObject(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, @NonNull String text, float textSize, Paint.Align textAlignment, @NonNull Typeface typeface) {
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mPaint.setTextAlign(textAlignment);
        mPaint.setTypeface(typeface);
        mTextSize = textSize;
        mText = text;
        mBound = new Rect();
    }

    @WorkerThread
    public void changeTypeface(@NonNull Typeface Typeface){
        mPaint.setTypeface(Typeface);
        calculateBoundary();
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
        calculateBoundary();
    }

    public String getText(){
        return mText;
    }

    @WorkerThread
    public void changeTextSize(float textSize){
        mTextSize = textSize;
        calculateBoundary();
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
        calculateBoundary();
    }

    @WorkerThread
    public Paint.Align getTextAlign(){
        return mPaint.getTextAlign();
    }
}