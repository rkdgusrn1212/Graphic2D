package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RectWidget extends PaintableWidget {

    float mWidth, mHeight;
    float mLeft, mRight, mTop, mBottom;

    @WorkerThread
    public RectWidget(float z, float x, float y, boolean visibility, boolean clickable, Widget.OnClickListener onClickListener, int color, boolean autoShadow, float width, float height){
        super(z, x, y, visibility, clickable, onClickListener, color, autoShadow);
        mWidth = width;
        mHeight = height;
        mLeft = mX - width/2;
        mTop = mY - height/2;
        mRight = mLeft + width;
        mBottom = mTop + height;
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
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }

    @Override
    void calculateBoundary(){
        mLeft = mX - mWidth/2;
        mTop = mY - mHeight/2;
        mRight = mLeft + mWidth;
        mBottom = mTop + mHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    }
}
