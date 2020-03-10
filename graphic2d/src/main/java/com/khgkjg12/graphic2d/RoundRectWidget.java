package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectWidget extends RectWidget {

    private float mRY, mRX;

    public RoundRectWidget(float z, float x, float y, boolean visibility, boolean clickable, boolean autoShadow, OnClickListener onClickListener, int width, int height, int color, float rX, float rY) {
        super(z, x, y, visibility, clickable, autoShadow, onClickListener, width, height, color);
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

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mLeft, mTop, mRight, mBottom, mRX, mRY, mPaint);
    }
}