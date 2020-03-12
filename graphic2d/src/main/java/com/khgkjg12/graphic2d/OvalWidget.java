package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class OvalWidget extends PaintableWidget {
    float mWidth, mHeight;
    RectF mRenderRect;
    @WorkerThread
    public OvalWidget(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float width, @FloatRange(from = 0, fromInclusive = false) float height){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mWidth = width;
        mHeight = height;
        mRenderRect = new RectF();
        mRenderRect.left = mX - mWidth/2;
        mRenderRect.top = mY - mHeight/2;
        mRenderRect.right = mRenderRect.left + mWidth;
        mRenderRect.bottom = mRenderRect.top + mHeight;
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
        float deltaX = x - mX;
        float deltaY = y - mY;
        float xRadius = mWidth/2;
        float yRadius = mHeight/2;
        return (deltaX*deltaX)/(xRadius*xRadius)+(deltaY*deltaY)/(yRadius*yRadius)<=1;
    }

    @Override
    void calculateBoundary(){
        mRenderRect.left = mX - mWidth/2;
        mRenderRect.top = mY - mHeight/2;
        mRenderRect.right = mRenderRect.left + mWidth;
        mRenderRect.bottom = mRenderRect.top + mHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawOval(mRenderRect, mPaint);
    }
}
