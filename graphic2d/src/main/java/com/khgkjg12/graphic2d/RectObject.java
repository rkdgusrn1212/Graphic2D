package com.khgkjg12.graphic2d;


import android.graphics.Paint;

public class RectObject extends Object {
    Paint mPaint;
    int mWidth, mHeight;
    int mHorizontalDegree;
    int mVerticalDegree;
    float mRenderWidth, mRenderHeight;
    float mRenderLeft, mRenderRight, mRenderTop, mRenderBottom;

    public RectObject(int color, int width, int height, float z, int x, int y){
        this(color, width, height, z, x, y, 0, 0, true, true);
    }

    public RectObject(int color, int width, int height, float z, int x, int y, int degreeH, int degreeV){
        this(color, width, height, z, x, y, degreeH, degreeV, true, true);
    }

    public RectObject(int color, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable){
        super(z, x, y, visibility, clickable);
        mPaint = new Paint();
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }

    public void setHorizontalFlip(int degree){
        mVerticalDegree = 0;
        mHorizontalDegree = degree%360;
    }

    public void setVerticalFlip(int degree){
        mHorizontalDegree = 0;
        mVerticalDegree = degree%360;
    }

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
