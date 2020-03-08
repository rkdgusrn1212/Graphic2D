package com.khgkjg12.graphic2d;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;
    private float mRenderRY, mRenderRX;

    public RoundRectObject(float z, int x, int y,  boolean visibility, boolean clickable, OnClickListener onClickListener, int width, int height, int degreeH, int degreeV, int color, float rX, float rY, boolean shadow) {
        super(z, x, y, visibility, clickable, onClickListener, width, height, degreeH, degreeV, color, shadow);
        mRX = rX;
        mRY = rY;
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
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRenderRX, mRenderRY, super.mPaint);
    }
}