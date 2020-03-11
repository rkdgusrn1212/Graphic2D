package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.WorkerThread;

public class OvalObject extends PaintableObject {
    float mWidth, mHeight;
    RectF mRenderRect;
    @WorkerThread
    public OvalObject(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, float width, float height){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mWidth = width;
        mHeight = height;
        mRenderRect = new RectF();
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
        float deltaX = x - mRenderX;
        float deltaY = y - mRenderY;
        float xRadius = mRenderRect.width()/2;
        float yRadius = mRenderRect.height()/2;
        float squareXR = xRadius*xRadius;
        float squareYR = yRadius*yRadius;
        return deltaX*deltaX*squareYR+deltaY*deltaY*squareXR==squareXR*squareYR;
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
        drawer.drawRect(mRenderRect.left, mRenderRect.top, mRenderRect.right, mRenderRect.bottom, mPaint);
    }
}
