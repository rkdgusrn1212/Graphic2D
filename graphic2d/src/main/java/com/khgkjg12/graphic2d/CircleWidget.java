package com.khgkjg12.graphic2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class CircleWidget extends PaintableWidget {
    protected float mRadius;

    @WorkerThread
    public CircleWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float radius){
        super(z, x, y, visibility, clickable, color, autoShadow);
        mRadius = radius;
    }

    @WorkerThread
    public void changeRadius(@FloatRange(from = 0, fromInclusive = false) float radius){
        mRadius = radius;
    }

    @WorkerThread
    public float getRadius(){return mRadius;}

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        float deltaX = x - mRenderX;
        float deltaY = y - mRenderY;
        return deltaX*deltaX+deltaY*deltaY<=mRadius*mRadius;
    }

    @Override
    void calculateBoundary(){ }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawCircle(mRenderX, mRenderY, mRadius, mPaint);
    }

    @Override
    void calculateOuterBound() {
        float left = mRenderX- mRadius-mShadow+mShadowDx;
        float top = mRenderY - mRadius-mShadow+mShadowDy;
        float right = mRenderX+mRadius+mShadow+mShadowDx;
        float bottom = mRenderY+mRadius+mShadow+mShadowDy;
        if (mPaint.getStyle()!=Paint.Style.FILL){
            float halfStrokeWidth = mPaint.getStrokeWidth()/2;
            left -= halfStrokeWidth;
            top -= halfStrokeWidth;
            right += halfStrokeWidth;
            bottom += halfStrokeWidth;
        }
        mOuterBoundary.set(left,top,right, bottom);
    }
}
