package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.WorkerThread;

public class RoundRectWidget extends RectWidget {

    protected float mRY, mRX;
    protected RectF mBoundaryF = new RectF();

    public RoundRectWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, float width, float height, float rX, float rY) {
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
    void calculateBoundary() {
        super.calculateBoundary();
        mBoundaryF.left = mLeft;
        mBoundaryF.right = mRight;
        mBoundaryF.top = mTop;
        mBoundaryF.bottom = mBottom;
    }

    @Override
    @WorkerThread
    protected void draw(Canvas canvas) {
        canvas.drawRoundRect(mBoundaryF, mRX, mRY, mPaint);
    }
}