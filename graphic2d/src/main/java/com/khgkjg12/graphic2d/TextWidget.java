package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class TextWidget extends PaintableWidget {

    protected String mText;
    protected Rect mBound = new Rect();
    protected float mLeft;
    protected float mRight;
    protected float mTop;
    protected float mBottom;
    protected float mTextWidth;
    protected float mTextHeight;

    public TextWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @NonNull String text, float textSize, Paint.Align textAlignment, @NonNull Typeface typeface) {
        super(z, x, y, visibility, clickable, color, autoShadow);
        mPaint.setTextAlign(textAlignment);
        mPaint.setTypeface(typeface);
        mPaint.setTextSize(textSize);
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
        return mPaint.getTextSize();
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
        mPaint.setTextSize(textSize);
        calculateAndCheckBoundary();
    }

    @Override
    @WorkerThread
    void calculateBoundary() {
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
    protected void draw(Canvas canvas) {
        canvas.drawText(mText, mRenderX, mBottom - mBound.bottom, mPaint);
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

    @Override
    void calculateOuterBound() {
        float left = mLeft-mShadow+mShadowDx;
        float top = mTop-mShadow+mShadowDy;
        float right = mRight+mShadow+mShadowDx;
        float bottom = mBottom+mShadow+mShadowDy;
        if (mPaint.getStyle()!=Paint.Style.FILL){
            float halfStrokeWidth = mPaint.getStrokeWidth()/2;
            left -= halfStrokeWidth;
            top -= halfStrokeWidth;
            right += halfStrokeWidth;
            bottom += halfStrokeWidth;
        }
        mOuterBoundary.set(left,top,right, bottom);
    }

    //줄여지면 true.
    public boolean textEllipsis(boolean fromStart, float length){
        int shortenLen = mPaint.breakText(mText, fromStart, length, null);
        if(shortenLen != mText.length()){
            String shortenText = mText.substring(0, shortenLen-1);
            if(fromStart){
                shortenText += '\u2026';
            }else{
                shortenText = '\u2026'+shortenText;
            }
            mText = shortenText;
            textEllipsis(fromStart, length);
            return true;
            //...을 한단어로하고 재귀호출 또돌려서 검사하기. 생성자에서 길이제한을 두던가 아님 이런식으로 public 메소드로 길이 조절된 텍스트를 반환받아 넣는 방식도 괜찮음.
        }else {
            return false;
        }
    }
}