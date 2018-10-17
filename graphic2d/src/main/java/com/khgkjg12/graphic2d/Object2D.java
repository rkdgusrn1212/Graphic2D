package com.khgkjg12.graphic2d;

import android.graphics.Rect;

public class Object2D {

    private Texture mTexture;
    private Rect mBoundary;
    private int x, y;
    private int mWidth, mHeight;
    private int mState;
    public static final int NO_WHERE = 0;//데이터로써만 존제.
    public static final int NORMAL = 1;//터치랑 충돌 모두 가능.
    public static final int NO_COLLISION = 2;//충돌 불가.
    public static final int NO_TOUCH = 3;//터치 불가.

    public Object2D(Texture texture, int width, int height) {
        mTexture = texture;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mState = NO_WHERE;
    }

    //객체의 좌표를 설정.
    public void setPosition(int x, int y){
        mBoundary.set(x-mWidth/2, y-mHeight/2, x+mWidth/2, y+mHeight/2);
    }

    //객체를 물리적 상태를 변화.
    public void setState(int state){
        mState = state;
    }

    public void dispose(){
        mTexture.dispose();
    }
}