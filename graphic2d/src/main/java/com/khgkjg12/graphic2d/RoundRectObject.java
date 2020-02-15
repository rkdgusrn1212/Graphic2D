package com.khgkjg12.graphic2d;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y) {
        super(color, width, height, z, x, y);
        mRX = rX;
        mRY = rY;
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV) {
        super(color, width, height, z, x, y, degreeH, degreeV);
        mRX = rX;
        mRY = rY;
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable) {
        super(color, width, height, z, x, y, degreeH, degreeV, visibility, clickable);
        mRX = rX;
        mRY = rY;
    }

    @Override
    void render(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRX, mRY, super.mPaint);
    }
}