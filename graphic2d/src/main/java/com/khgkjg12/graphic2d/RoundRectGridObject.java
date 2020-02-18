package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.support.annotation.Nullable;

public class RoundRectGridObject extends RectGridObject {

    private float mRx;
    private float mRy;

    public RoundRectGridObject(float rX, float rY, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, @Nullable OnClickItemListener onClickItemListener){
        this(Color.TRANSPARENT, rX, rY, width, height, row, column, z, x, y, degreeH, degreeV, onClickItemListener);
    }

    public RoundRectGridObject(float rX, float rY, int width, int height, int row, int column, int z, int x, int y, @Nullable OnClickItemListener onClickItemListener){
        this(Color.TRANSPARENT, rX, rY, width, height, row, column, z, x, y, 0, 0, onClickItemListener);
    }

    public RoundRectGridObject(int color, float rX, float rY, int width, int height, int row, int column, int z, int x, int y, @Nullable OnClickItemListener onClickItemListener){
        this(color, rX, rY, width, height, row, column, z, x, y, 0, 0, onClickItemListener);
    }

    /**
     * @exception ArrayIndexOutOfBoundsException parameter objects's size does not match parameter row x parameter column.
     * @param color background color.
     * @param width total grid width.
     * @param height total grid height.
     * @param row the number of row in grid.
     * @param column the number of column in grid.
     * @param z z coordinate of center of grid.
     * @param x x coordinate of center of grid.
     * @param y y coordinate of center of grid.
     * @param degreeH horizontal degree of grid.
     * @param degreeV vertical degree of grid.
     * @param onClickItemListener Item click event callback.
     */
    public RoundRectGridObject(int color, float rX, float rY, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, @Nullable OnClickItemListener onClickItemListener) {
        super(color, width, height, row, column, z, x, y, degreeH, degreeV, onClickItemListener);
        mRx = rX;
        mRy = rY;
    }

    @Override
    void render(Graphic2dDrawer drawer) {
        drawer.drawRoundRect(mRenderLeft, mRenderTop, mRenderRight, mRenderBottom, mRx, mRy, super.mPaint);
    }
}
