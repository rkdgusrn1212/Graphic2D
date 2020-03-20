package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    protected float mRY, mRX;
    protected float mRenderRY, mRenderRX;

    public RoundRectObject(float z, float x, float y,  boolean visibility, boolean clickable, int color, boolean autoShadow, int width, int height, float rX, float rY) {
        super(z, x, y, visibility, clickable, color, autoShadow, width, height);
        mRX = rX;
        mRY = rY;
    }

    @WorkerThread
    public void changeRX(float rX){
        mRX = rX;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public void changeRY(float rY){
        mRY = rY;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public void changeRXY(float rX, float rY){
        mRX = rX;
        mRY = rY;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public float getRX(){
        return mRX;
    }

    @WorkerThread
    public float getRY(){
        return mRY;
    }

    @Override
    void calculateBoundary() {
        super.calculateBoundary();
        mRenderRY = mRY*mScale;
        mRenderRX = mRX*mScale;
    }

    @Override
    @WorkerThread
    protected void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRenderRX, mRenderRY, mPaint);
    }
}