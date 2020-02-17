package com.khgkjg12.graphic2d;

import android.graphics.RectF;
import android.support.annotation.NonNull;

public class TextureObject extends Object {
    private Texture mTexture;
    int mWidth, mHeight;
    RectF mRectF;
    int mHorizontalDegree;
    int mVerticalDegree;
    float mRenderWidth;
    float mRenderHeight;

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y) {
        this(texture, width, height, z, x, y, 0, 0);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y, int degreeH, int degreeV) {
        this(texture, width, height, z, x, y, degreeH, degreeV, true, true);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable){
        super(z, x, y, visibility, clickable);
        mTexture = texture;
        mRectF = new RectF();
        mWidth = width;
        mHeight = height;
        mHorizontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    public void setTexture(@NonNull Texture texture){
        mTexture = texture;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRectF.right && x > mRectF.left && y < mRectF.bottom && y > mRectF.top;
    }

    @Override
    void calculateBoundary(){
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180))*mScale;
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180))*mScale;
        mRectF.left =  mRenderX - mRenderWidth/2;
        mRectF.top = mRenderY - mRenderHeight/2;
        mRectF.right = mRectF.left + mRenderWidth;
        mRectF.bottom = mRectF.top + mRenderHeight;
    }


    @Override
    void draw(Graphic2dDrawer drawer) {
        drawer.drawObject(mTexture, mRectF);
    }
}
