package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class OvalWidget extends PaintableWidget {
    protected float mWidth, mHeight;
    protected RectF mRenderRect = new RectF();
    @WorkerThread
    public OvalWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
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
    public void changeHeight(@FloatRange(from = 0, fromInclusive = false)float height){
        mHeight = height;
        calculateAndCheckBoundary();
    }
    @WorkerThread
    public void changeSize(@FloatRange(from = 0, fromInclusive = false)float width,@FloatRange(from = 0, fromInclusive = false) float height){
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
        float deltaX = x - mRenderX;
        float deltaY = y - mRenderY;
        float xRadius = mWidth/2;
        float yRadius = mHeight/2;
        return (deltaX*deltaX)/(xRadius*xRadius)+(deltaY*deltaY)/(yRadius*yRadius)<=1;
    }

    @Override
    void calculateBoundary(){
        mRenderRect.left = mRenderX - mWidth/2;
        mRenderRect.top = mRenderY - mHeight/2;
        mRenderRect.right = mRenderRect.left + mWidth;
        mRenderRect.bottom = mRenderRect.top + mHeight;
    }

    @Override
    void calculateOuterBound() {
        mOuterBoundary.set( mRenderRect.left-mShadow+mShadowDx,mRenderRect.top-mShadow+mShadowDy,mRenderRect.right+mShadow+mShadowDx, mRenderRect.bottom+mShadow+mShadowDy);
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawOval(mRenderRect, mPaint);
    }
}
