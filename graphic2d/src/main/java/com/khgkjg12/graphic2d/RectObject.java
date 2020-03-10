package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RectObject extends PaintableObject {
    float mWidth, mHeight;
    float mRenderWidth, mRenderHeight;
    float mRenderLeft, mRenderRight, mRenderTop, mRenderBottom;
    @WorkerThread
    public RectObject(float z, float x, float y, boolean visibility, boolean clickable, boolean autoShadow, OnClickListener onClickListener, float width, float height, int color){
        super(z, x, y, visibility, clickable, autoShadow, onClickListener);
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
    }

    @WorkerThread
    public void changeWidth(float width){
        mWidth = width;
        calculateBoundary();
    }

    @WorkerThread
    public void changeHeight(float height){
        mHeight = height;
        calculateBoundary();
    }
    @WorkerThread
    public void changeSize(float width, float height){
        mWidth = width;
        mHeight = height;
        calculateBoundary();
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }

    /**
     * world 콜백에서만 호출.
    * */
    @WorkerThread
    public void changeColor(int color){
        mPaint.setColor(color);
    }

    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth *mScale;
        mRenderHeight = mHeight *mScale;
        mRenderLeft = mRenderX - mRenderWidth/2;
        mRenderTop = mRenderY - mRenderHeight/2;
        mRenderRight = mRenderLeft + mRenderWidth;
        mRenderBottom = mRenderTop + mRenderHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mPaint);
    }
}
