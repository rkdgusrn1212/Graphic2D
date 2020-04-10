package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.WorkerThread;

public abstract class PaintableWidget extends Widget {

    protected float mShadow = 0;
    protected boolean mAutoShadow;
    protected Paint mPaint = new Paint();
    protected float mShadowDx, mShadowDy;

    public PaintableWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow) {
        super(z, x, y, visibility, clickable);
        mPaint.setColor(color);
        if(autoShadow){
            enableAutoShadow();
        }
    }

    private void calculateShadow(){
        if(mZ<0){
            mShadow = 0;
        }else{
            mShadow = mZ;
        }
        mShadowDx = mShadow/8;
        mShadowDy = mShadow/4;
        mPaint.setShadowLayer(mShadow, mShadowDx, mShadowDy, Color.BLACK);
    }

    @WorkerThread
    public boolean getAutoShadow(){
        return mAutoShadow;
    }

    @WorkerThread
    public void enableAutoShadow(){
        mAutoShadow = true;
        calculateShadow();
    }

    /**
     * autoShadow를 꺼도 그림자는 그대로다.
     */
    @WorkerThread
    public void disableAutoShadow(){
        mAutoShadow = false;
    }

    /**
     * autoShadow가 true일때는 쓰지 안아야함. 물론 써도 애러는 안나지만 금방 z 축 이동시 다시 바뀜.
     * @param radius
     * @param dx
     * @param dy
     * @param shadowColor
     */
    @WorkerThread
    public void setShadowLayer(float radius, float dx, float dy, int shadowColor){
        if(!mAutoShadow){
            mShadow = radius;
            mShadowDx = dx;
            mShadowDy = dy;
            mPaint.setShadowLayer(mShadow, mShadowDx, mShadowDy, shadowColor);
        }else{
            throw new RuntimeException("try to change shadow while auto shadow on");
        }
    }

    @WorkerThread
    public void clearShadowLayer(){
        mPaint.clearShadowLayer();
    }

    @Override
    public void moveZ(float z) {
        super.moveZ(z);
        if(mAutoShadow)calculateShadow();
    }

    //32-bit
    @WorkerThread
    public void changeColor(int color){
        mPaint.setColor(color);
    }
    //32-bit
    @WorkerThread
    public int getColor(){
        return mPaint.getColor();
    }

    @WorkerThread
    public void changeAlpha(int alpha){
        mPaint.setAlpha(alpha);
    }

    @WorkerThread
    public int getAlpha(){
        return mPaint.getAlpha();
    }

    public void setStyle(Paint.Style style){
        mPaint.setStyle(style);
    }

    public void setStrokeWidth(float width){
        mPaint.setStrokeWidth(width);
    }

    public void setAntialias(boolean antialias){
        mPaint.setAntiAlias(antialias);
    }
}
