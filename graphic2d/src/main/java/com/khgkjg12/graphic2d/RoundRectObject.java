package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;
    private float mRenderRY, mRenderRX;

    public RoundRectObject(float z, float x, float y,  boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, int width, int height, float rX, float rY) {
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow, width, height);
        mRX = rX;
        mRY = rY;
    }

    @WorkerThread
    public void changeRX(float rX){
        mRX = rX;
        calculateBoundary();
    }

    @WorkerThread
    public void changeRY(float rY){
        mRY = rY;
        calculateBoundary();
    }

    @WorkerThread
    public void changeRXY(float rX, float rY){
        mRX = rX;
        mRY = rY;
        calculateBoundary();
    }

    @Override
    void calculateBoundary() {
        super.calculateBoundary();
        mRenderRY = mRY*mScale;
        mRenderRX = mRX*mScale;
    }

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRenderRX, mRenderRY, mPaint);
    }
}