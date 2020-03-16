package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.WorkerThread;

public abstract class PaintableWidget extends Widget {

    protected float mShadow = -1;
    protected boolean mAutoShadow;
    protected Paint mPaint = new Paint();

    public PaintableWidget(float z, float x, float y, boolean visibility, boolean clickable, int color, boolean autoShadow) {
        super(z, x, y, visibility, clickable);
        mPaint.setColor(color);
        if(autoShadow){
            enableAutoShadow();
        }
    }

    private void calculateShadow(){
        float shadow;
        if(mZ<0){
            shadow = 0;
        }else{
            shadow = mZ;
        }
        if(shadow!=mShadow){
            mPaint.setShadowLayer(mZ, mZ/8, mZ/4, Color.BLACK);
            mShadow = shadow;
        }
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
        mShadow = -1;
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
        mPaint.setShadowLayer(radius, dx, dy, shadowColor);
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
}
