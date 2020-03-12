package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class OvalObject extends PaintableObject {
    float mWidth, mHeight;
    float mRenderWidth, mRenderHeight;
    RectF mRenderRect;
    @WorkerThread
    public OvalObject(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mWidth = width;
        mHeight = height;
        mRenderRect = new RectF();
    }

    @WorkerThread
    public void changeWidth(@FloatRange(from = 0, fromInclusive = false) float width){
        mWidth = width;
        calculateBoundary();
    }

    @WorkerThread
    public void changeHeight(@FloatRange(from = 0, fromInclusive = false)float height){
        mHeight = height;
        calculateBoundary();
    }
    @WorkerThread
    public void changeSize(@FloatRange(from = 0, fromInclusive = false)float width,@FloatRange(from = 0, fromInclusive = false) float height){
        mWidth = width;
        mHeight = height;
        calculateBoundary();
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        float deltaX = x - mRenderX;
        float deltaY = y - mRenderY;
        float xRadius = mRenderWidth/2;
        float yRadius = mRenderHeight/2;
        return (deltaX*deltaX)/(xRadius*xRadius)+(deltaY*deltaY)/(yRadius*yRadius)<=1;
    }

    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth *mScale;
        mRenderHeight = mHeight *mScale;
        mRenderRect.left = mRenderX - mRenderWidth/2;
        mRenderRect.top = mRenderY - mRenderHeight/2;
        mRenderRect.right = mRenderRect.left + mRenderWidth;
        mRenderRect.bottom = mRenderRect.top + mRenderHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawOval(mRenderRect, mPaint);
    }
}
