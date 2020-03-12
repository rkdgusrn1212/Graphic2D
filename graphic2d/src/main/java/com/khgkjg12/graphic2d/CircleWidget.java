package com.khgkjg12.graphic2d;

import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public class CircleWidget extends PaintableWidget {
    float mRadius;

    @WorkerThread
    public CircleWidget(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, int color, boolean autoShadow, @FloatRange(from = 0, fromInclusive = false) float radius){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mRadius = radius;
    }

    @WorkerThread
    public void changeRadius(@FloatRange(from = 0, fromInclusive = false) float radius){
        mRadius = radius;
    }

    @Override
    @WorkerThread
    boolean checkBoundary(int x, int y) {
        float deltaX = x - mX;
        float deltaY = y - mY;
        return deltaX*deltaX+deltaY*deltaY<=mRadius*mRadius;
    }

    @Override
    void calculateBoundary(){ }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawCircle(mX, mY, mRadius, mPaint);
    }
}
