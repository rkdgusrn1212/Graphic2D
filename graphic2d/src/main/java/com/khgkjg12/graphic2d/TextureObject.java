package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.NonNull;

public class TextureObject extends RectObject {
    private Texture mTexture;
    private RectF mRectF;

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y, String id) {
        this(texture, width, height, z, x, y, 0, 0, id);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y, int degreeH, int degreeV, String id) {
        this(texture, width, height, z, x, y, degreeH, degreeV, true, true, id);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable, String id){
        super(Color.TRANSPARENT, width, height, z, x, y, degreeH, degreeV, visibility, clickable, id);
        mTexture = texture;
        mRectF = new RectF();
    }

    public void setTexture(@NonNull Texture texture){
        mTexture = texture;
    }

    @Override
    void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY) {
        mRenderWidth = mWidth * Math.abs((float) Math.cos(mHorizontalDegree * Math.PI / 180))*scale;
        mRenderHeight = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180))*scale;
        mRectF.left = mRenderLeft = renderX - mRenderWidth/2;
        mRectF.top = mRenderTop = renderY - mRenderHeight/2;
        mRectF.right = mRenderRight = mRenderLeft + mRenderWidth;
        mRectF.bottom = mRenderBottom = mRenderTop + mRenderHeight;
        drawer.drawObject(mTexture, mRectF);
    }
}
