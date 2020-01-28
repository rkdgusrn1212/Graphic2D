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
    void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY, float verticalDegree, float horizontalDegree) {
        float width = mWidth * Math.abs((float) Math.cos(mHoriaontalDegree * Math.PI / 180));
        float height = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180));
        float left = renderX - width/2;
        float top = renderY - height/2;
        float right = left + width * scale;
        float bottom = top + height * scale;
        drawer.drawRoundRect(left, top, right, bottom, mRX, mRY, super.mPaint);
    }
}