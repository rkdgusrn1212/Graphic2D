package com.khgkjg12.graphic2d;


import android.graphics.Paint;

public class RectObject extends Object {
    Paint mPaint;
    int mWidth, mHeight;

    public RectObject(int color, int width, int height, float z, int x, int y, String id){
        this(color, width, height, z, x, y, 0, 0, true, true, id);
    }

    public RectObject(int color, int width, int height, float z, int x, int y, int degreeH, int degreeV, String id){
        this(color, width, height, z, x, y, degreeH, degreeV, true, true, id);
    }

    public RectObject(int color, int width, int height, float z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean clickable, String id){
        super(z, x, y, degreeH, degreeV, visibility, clickable, id);
        mPaint = new Paint();
        mPaint.setColor(color);
        mWidth = width;
        mHeight = height;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        int left = mX-mWidth/2;
        int top = mY-mHeight/2;
        int right = left + mWidth;
        int bottom = top + mHeight;
        return x < right && x >= left && y < bottom && y >= top;
    }

    public void changeColor(int color){
        mPaint.setColor(color);
    }

    @Override
    void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY, float verticalDegree, float horizontalDegree) {
        float width = mWidth * Math.abs((float) Math.cos(mHoriaontalDegree * Math.PI / 180));
        float height = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180));
        float left = renderX - width/2;
        float top = renderY - height/2;
        float right = left + width * scale;
        float bottom = top + height * scale;
        drawer.drawRect(left, top, right, bottom, mPaint);
    }
}