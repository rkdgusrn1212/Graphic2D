package com.khgkjg12.graphic2d;

public class RoundRectObject extends RectObject {

    private float mRY, mRX;

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, String id) {
        super(color, width, height, z, x, y, id);
        mRX = rX;
        mRY = rY;
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV, String id) {
        super(color, width, height, z, x, y, degreeH, degreeV, id);
        mRX = rX;
        mRY = rY;
    }

    public RoundRectObject(int color, float rX, float rY, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable, String id) {
        super(color, width, height, z, x, y, degreeH, degreeV, visibility, clickable, id);
        mRX = rX;
        mRY = rY;
    }

    @Override
    void render(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRX, mRY, super.mPaint);
    }
}