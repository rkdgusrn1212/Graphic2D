package com.khgkjg12.graphic2d;


import android.graphics.Paint;
import android.support.annotation.WorkerThread;

public class RectObject extends Object {
    Paint mPaint;
    int mWidth, mHeight;
    int mHorizontalDegree;
    int mVerticalDegree;
    float mRenderWidth, mRenderHeight;
    float mRenderLeft, mRenderRight, mRenderTop, mRenderBottom;

    public RectObject(float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener, int width, int height, int degreeH, int degreeV, int color){
        super(z, x, y, visibility, clickable, onClickListener);
        mPaint = new Paint();
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }

    @WorkerThread
    public void setHorizontalFlip(World world, int degree){
        mVerticalDegree = 0;
        mHorizontalDegree = degree%360;
        calculateRenderXY(world);
    }

    @WorkerThread
    public void setVerticalFlip(World world, int degree){
        mHorizontalDegree = 0;
        mVerticalDegree = degree%360;
        calculateRenderXY(world);
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
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180))*mScale;
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180))*mScale;
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
