package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;

    public RoundRectObject(float z, int x, int y,  boolean visibility, boolean clickable, OnClickListener onClickListener, int width, int height, int degreeH, int degreeV, int color, float rX, float rY) {
        super(z, x, y, visibility, clickable, onClickListener, width, height, degreeH, degreeV, color);
        mRX = rX;
        mRY = rY;
    }

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRX, mRY, super.mPaint);
    }
}