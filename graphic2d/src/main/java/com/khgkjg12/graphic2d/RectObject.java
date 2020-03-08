package com.khgkjg12.graphic2d;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.PriorityQueue;

public class RectObject extends Object {
    Paint mPaint;
    int mWidth, mHeight;
    int mHorizontalDegree;
    int mVerticalDegree;
    float mLeft;
    float mRight;
    float mTop;
    float mBottom;
    float mRenderWidth, mRenderHeight;
    float mRenderLeft, mRenderRight, mRenderTop, mRenderBottom;
    private boolean mAutoShadow;
    private float mShadow;

    @WorkerThread
    public RectObject(float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener, int width, int height, int degreeH, int degreeV, int color, boolean autoShadow){
        super(z, x, y, visibility, clickable, onClickListener);
        mPaint = new Paint();
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
        mShadow = 0;
        mAutoShadow = autoShadow;
        calculateShadow();
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

    @Override
    public void moveZ(float z) {
        super.moveZ(z);
        calculateShadow();
    }

    void calculateShadow(){
        float shadow;
        if(!mAutoShadow||mZ<0){
            shadow = 0;
        }else if(mZ<10){
            shadow = mZ;
        }else{
            shadow = 10;
        }
        if(shadow!=mShadow){
            mPaint.setShadowLayer(mZ, mZ / 5, mZ / 2, Color.BLACK);
            mShadow = shadow;
        }
    }

    @WorkerThread
    public Paint editPaint(){
        return mPaint;
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
        mLeft = mX-mWidth/2f;
        mRight = mLeft + mWidth;
        mTop = mY-mHeight/2f;
        mBottom = mTop + mHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mPaint);
    }

    @Override
    void drawViewport(Graphic2dDrawer drawer) {
        drawer.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    }
}
