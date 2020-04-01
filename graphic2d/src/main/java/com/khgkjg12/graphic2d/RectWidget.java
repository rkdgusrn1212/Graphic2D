package com.khgkjg12.graphic2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class RectWidget extends PaintableWidget {

    protected float mWidth, mHeight;
    protected float mLeft, mRight, mTop, mBottom;

    @WorkerThread
    public RectWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
        super(z, x, y, visibility, clickable, color, autoShadow);
        mWidth = width;
        mHeight = height;
    }

    @WorkerThread
    public void changeWidth(@FloatRange(from = 0, fromInclusive = false) float width){
        mWidth = width;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public void changeHeight(@FloatRange(from = 0, fromInclusive = false) float height){
        mHeight = height;
        calculateAndCheckBoundary();
    }
    @WorkerThread
    public void changeSize(@FloatRange(from = 0, fromInclusive = false) float width,@FloatRange(from = 0, fromInclusive = false) float height){
        mWidth = width;
        mHeight = height;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public float getWidth(){
        return mWidth;
    }
    @WorkerThread
    public float getHeight(){
        return mHeight;
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }

    @Override
    void calculateBoundary(){
        mLeft = mRenderX - mWidth/2;
        mTop = mRenderY - mHeight/2;
        mRight = mLeft + mWidth;
        mBottom = mTop + mHeight;
    }

    @Override
    void calculateOuterBound() {
        mOuterBoundary.set(mLeft-mShadow+mShadowDx,mTop-mShadow+mShadowDy,mRight+mShadow+mShadowDx, mBottom+mShadow+mShadowDy);
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    }
}
