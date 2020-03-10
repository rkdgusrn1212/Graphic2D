package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class TextureWidget extends Widget {
    private Texture mTexture;
    float mWidth, mHeight;
    RectF mRectF;
    int mHorizontalDegree;
    int mVerticalDegree;
    float mRenderWidth;
    float mRenderHeight;

    public TextureWidget(float z, float x, float y, boolean visibility, boolean clickable, OnClickListener onClickListener, float width, float height, int degreeH, int degreeV, @NonNull Texture texture){
        super(z, x, y, visibility, clickable, onClickListener);
        mTexture = texture;
        mRectF = new RectF();
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180));
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180));
        mRectF.left =  mX - mRenderWidth/2;
        mRectF.top = mY - mRenderHeight/2;
        mRectF.right = mRectF.left + mRenderWidth;
        mRectF.bottom = mRectF.top + mRenderHeight;

    }

    /**
     * World 콜백에서만 호출.
     * @param texture
     */
    @WorkerThread
    public void setTexture(@NonNull Texture texture){
        mTexture = texture;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRectF.right && x > mRectF.left && y < mRectF.bottom && y > mRectF.top;
    }

    @WorkerThread
    public void changeHorizontalDegree(int degree){
        mVerticalDegree = 0;
        mHorizontalDegree = degree%360;
        calculateBoundary();
    }

    @WorkerThread
    public void changeVerticalDegree(int degree){
        mHorizontalDegree = 0;
        mVerticalDegree = degree%360;
        calculateBoundary();
    }

    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180));
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180));
        mRectF.left =  mX - mRenderWidth/2;
        mRectF.top = mY - mRenderHeight/2;
        mRectF.right = mRectF.left + mRenderWidth;
        mRectF.bottom = mRectF.top + mRenderHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawObject(mTexture, mRectF);
    }
}
