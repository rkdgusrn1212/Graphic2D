package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class RectObject extends PaintableObject {

    protected float mWidth, mHeight;
    protected float mRenderWidth, mRenderHeight;
    protected float mRenderLeft, mRenderRight, mRenderTop, mRenderBottom;

    @WorkerThread
    public RectObject(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
        super(z, x, y, visibility, clickable, color, autoShadow);
        mWidth = width;
        mHeight = height;
    }

    @WorkerThread
    public void changeWidth(@FloatRange(from = 0, fromInclusive = false) float width){
        mWidth = width;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public void changeHeight(@FloatRange(from = 0, fromInclusive = false) float height){
        mHeight = height;
        calculateAndCheckBoundary();
    }
    @WorkerThread
    public void changeSize(@FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
        mWidth = width;
        mHeight = height;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public float getWidth(){
        return mWidth;
    }
    @WorkerThread
    public float getHeight(){
        return mHeight;
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }

    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth *mScale;
        mRenderHeight = mHeight *mScale;
        mRenderLeft = mRenderX - mRenderWidth/2;
        mRenderTop = mRenderY - mRenderHeight/2;
        mRenderRight = mRenderLeft + mRenderWidth;
        mRenderBottom = mRenderTop + mRenderHeight;
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mPaint);
    }
}
