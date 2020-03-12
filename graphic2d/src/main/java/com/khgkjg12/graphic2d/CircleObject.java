package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class CircleObject extends PaintableObject {
    float mRadius;
    float mRenderRadius;

    @WorkerThread
    public CircleObject(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float radius){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mRadius = radius;
    }

    @WorkerThread
    public void changeRadius(@FloatRange(from = 0, fromInclusive = false) float radius){
        mRadius = radius;
        calculateBoundary();
    }

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
    void draw(Graphic2dDrawer drawer) {
        drawer.drawCircle(mRenderX, mRenderY, mRenderRadius, mPaint);
    }
}
