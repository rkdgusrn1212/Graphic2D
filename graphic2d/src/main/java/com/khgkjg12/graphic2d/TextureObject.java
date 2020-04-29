package com.khgkjg12.graphic2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class TextureObject extends Object {
    private Bitmap mBitmap;
    float mWidth, mHeight;
    RectF mRectF = new RectF();
    int mHorizontalDegree;
    int mVerticalDegree;
    float mRenderWidth;
    float mRenderHeight;
    Paint mPaint;

    public TextureObject(float z, float x, float y, boolean visibility, boolean clickable, float width, float height, int degreeH, int degreeV, @NonNull Bitmap bitmap){
        super(z, x, y, visibility, clickable);
        mBitmap = bitmap;
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    /**
     * World 콜백에서만 호출.
     * @param bitmap
     */
    @WorkerThread
    public void setBItmap(@NonNull Bitmap bitmap){
        mBitmap = bitmap;
    }

    public Paint getPaint(){
        if(mPaint == null)
            mPaint = new Paint();
        return mPaint;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRectF.right && x > mRectF.left && y < mRectF.bottom && y > mRectF.top;
    }

    @WorkerThread
    public void changeHorizontalDegree(int degree){
        mVerticalDegree = 0;
        mHorizontalDegree = degree%360;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public void changeVerticalDegree(int degree){
        mHorizontalDegree = 0;
        mVerticalDegree = degree%360;
        calculateAndCheckBoundary();
    }
    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180))*mScale;
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180))*mScale;
        mRectF.left =  mRenderX - mRenderWidth/2;
        mRectF.top = mRenderY - mRenderHeight/2;
        mRectF.right = mRectF.left + mRenderWidth;
        mRectF.bottom = mRectF.top + mRenderHeight;
    }


    @Override
    protected void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, null, mRectF, null);
    }
}
