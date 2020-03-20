package com.khgkjg12.graphic2d;

import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class CircleObject extends PaintableObject {
    protected float mRadius;
    protected float mRenderRadius;

    @WorkerThread
    public CircleObject(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float radius){
        super(z, x, y, visibility, clickable, color, autoShadow);
        mRadius = radius;
    }

    @WorkerThread
    public void changeRadius(@FloatRange(from = 0, fromInclusive = false) float radius){
        mRadius = radius;
        calculateAndCheckBoundary();
    }
    @WorkerThread
    public float getRadius(){return mRadius;}
    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        float deltaX = x - mRenderX;
        float deltaY = y - mRenderY;
        return deltaX*deltaX+deltaY*deltaY<=mRenderRadius*mRenderRadius;
    }

    @Override
    void calculateBoundary(){
        mRenderRadius = mRadius *mScale;
    }

    @Override
    protected void draw(Graphic2dDrawer drawer) {
        drawer.drawCircle(mRenderX, mRenderY, mRenderRadius, mPaint);
    }
}
