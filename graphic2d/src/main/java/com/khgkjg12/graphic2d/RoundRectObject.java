package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;
    private float mRenderRY, mRenderRX;

    public RoundRectObject(float z, float x, float y,  boolean visibility, boolean clickable, boolean autoShadow, OnClickListener onClickListener, int width, int height, int color, float rX, float rY) {
        super(z, x, y, visibility, clickable, autoShadow, onClickListener, width, height, color);
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
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRenderRX, mRenderRY, mPaint);
    }
}