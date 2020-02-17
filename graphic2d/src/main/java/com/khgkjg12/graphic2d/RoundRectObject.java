package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, OnClickListener onClickListener) {
        this(color, rX, rY, width, height, z, x, y, 0, 0, true, true, onClickListener);
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV, OnClickListener onClickListener) {
        this(color, rX, rY, width, height, z, x, y, degreeH, degreeV, true, true, onClickListener);
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable, OnClickListener onClickListener) {
        super(color, width, height, z, x, y, degreeH, degreeV, visibility, clickable, onClickListener);
        mRX = rX;
        mRY = rY;
    }

    @Override
    @WorkerThread
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRX, mRY, super.mPaint);
    }
}