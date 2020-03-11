package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class OvalObject extends PaintableObject {
    float mWidth, mHeight;
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
        float xRadius = mRenderRect.width()/2;
        float yRadius = mRenderRect.height()/2;
        return (deltaX*deltaX)/(xRadius*xRadius)+(deltaY*deltaY)/(yRadius*yRadius)<=1;
    }

    @Override
    void calculateBoundary(){
        float renderWidth = mWidth *mScale;
        float renderHeight = mHeight *mScale;
        mRenderRect.left = mRenderX - renderWidth/2;
        mRenderRect.top = mRenderY - renderHeight/2;
        mRenderRect.right = mRenderRect.left + renderWidth;
        mRenderRect.bottom = mRenderRect.top + renderHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawOval(mRenderRect, mPaint);
    }
}
