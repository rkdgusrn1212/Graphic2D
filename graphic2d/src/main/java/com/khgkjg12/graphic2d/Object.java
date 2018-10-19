package com.khgkjg12.graphic2d;

import android.graphics.Rect;
import android.util.Log;

public class Object {

    private Texture mTexture;
    private int mColor;
    Rect mBoundary;
    private int mWidth, mHeight;
    private int mState;
    public static final int NO_WHERE = 0;//데이터로써만 존제.
    public static final int NORMAL = 1;//터치랑 충돌 모두 가능.
    public static final int NO_COLLISION = 2;//충돌 불가.
    public static final int NO_TOUCH = 3;//터치 불가.
    String mId;
    private OnClickListener mOnClickListener;

    public Object(Texture texture, int width, int height, String id) {
        mTexture = texture;
        mColor = 0;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mState = NO_WHERE;
        mId = id;
    }

    public Object(int color, int width, int height, String id) {
        mTexture = null;
        mColor = color;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mState = NO_WHERE;
        mId = id;
    }

    //객체의 좌표를 설정.
    public void setPosition(int x, int y){
        mBoundary.set(x-mWidth/2, y-mHeight/2, x+mWidth/2, y+mHeight/2);
    }

    //객체를 물리적 상태를 변화.
    public void setState(int state){
        mState = state;
    }

    boolean onTouch(int x, int y){
        if(mBoundary.contains(x, y)){
            if(mOnClickListener!=null) {
                mOnClickListener.onClick(this);
            }
            return true;
        }else{
            return false;
        }
    }

    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    void render(Graphic2dDrawer drawer, int bufferWidth, int bufferHeight, int worldWidth, int worldHeight, float scale, int viewportX, int viewportY){
        int deltaX = (int)((mBoundary.left-viewportX)*scale);
        int deltaY = (int)((mBoundary.top-viewportY)*scale);

        if(mTexture!=null) {
            drawer.drawObject(mTexture, bufferWidth/2+deltaX, bufferHeight/2+deltaY, (int) (mBoundary.width() * scale), (int) (mBoundary.height() * scale));
        }else{
            drawer.drawRect(bufferWidth/2+deltaX, bufferHeight/2+deltaY, (int) (mBoundary.width() * scale), (int) (mBoundary.height() * scale), mColor);
        }
    }

    void dispose(){
        if(mTexture!=null){
            mTexture.dispose();
        }
    }

    public interface OnClickListener{
        public void onClick(Object object);
    }
}