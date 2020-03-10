package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

public class RectWidget extends PaintableWidget {

    float mWidth, mHeight;
    float mLeft, mRight, mTop, mBottom;

    @WorkerThread
    public RectWidget(float z, float x, float y, boolean visibility, boolean clickable, boolean autoShadow, Widget.OnClickListener onClickListener, float width, float height, int color){
        super(z, x, y, visibility, clickable, autoShadow, onClickListener);
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
        calculateBoundary();
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

    /**
     * world 콜백에서만 호출.
     * */
    @WorkerThread
    public void changeColor(int color){
        mPaint.setColor(color);
    }

    @Override
    void calculateBoundary(){
        mLeft = mX - mWidth/2;
        mTop = mY - mHeight/2;
        mRight = mLeft + mWidth;
        mBottom = mRight + mRight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    }
}
