package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectWidget extends RectWidget {

    protected float mRY, mRX;

    public RoundRectWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, int width, int height, float rX, float rY) {
        super(z, x, y, visibility, clickable, color, autoShadow, width, height);
        mRX = rX;
        mRY = rY;
    }

    @WorkerThread
    public void changeRX(float rX){
        mRX = rX;
    }

    @WorkerThread
    public void changeRY(float rY){
        mRY = rY;
    }

    @WorkerThread
    public void changeRXY(float rX, float rY){
        mRX = rX;
        mRY = rY;
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
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mLeft, mTop, mRight, mBottom, mRX, mRY, mPaint);
    }
}