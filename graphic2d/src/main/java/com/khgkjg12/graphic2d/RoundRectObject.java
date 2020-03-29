package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.WorkerThread;

public class RoundRectObject extends RectObject {

    protected float mRY, mRX;
    protected float mRenderRY, mRenderRX;
    protected RectF mBoundaryF = new RectF();

    public RoundRectObject(float z, float x, float y,  boolean visibility, boolean clickable, int color, boolean autoShadow, float width, float height, float rX, float rY) {
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
        mBoundaryF.left = mRenderLeft;
        mBoundaryF.right = mRenderRight;
        mBoundaryF.top = mRenderTop;
        mBoundaryF.bottom = mRenderBottom;
    }

    @Override
    @WorkerThread
    protected void draw(Canvas canvas) {
        canvas.drawRoundRect(mBoundaryF, mRX, mRY, mPaint);
    }
}