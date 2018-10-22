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
    private float mZ;
    private int mDegree;

    public Object(Texture texture, int width, int height, String id) {
        this(texture, width, height, 0, 0, id);
    }

    public Object(int color, int width, int height, String id) {
        this(color, width, height, 0, 0, id);
    }

    public Object(Texture texture, int width, int height, int z, int degree, String id){
        mTexture = texture;
        mColor = 0;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mState = NO_WHERE;
        mId = id;
        mZ = z;
        mDegree = degree%360;
    }

    public Object(int color, int width, int height, int z, int degree, String id){
        mTexture = null;
        mColor = color;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mState = NO_WHERE;
        mId = id;
        mZ = z;
        mDegree = degree%360;
    }

    //객체의 좌표를 설정.
    public void setPosition(int x, int y){
        mBoundary.set(x-mWidth/2, y-mWidth/2, x+mWidth/2, y+mWidth/2);
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

    void render(Graphic2dDrawer drawer, int viewportWidth, int viewportHeight, float cameraZ, float focusedZ, int viewportX, int viewportY){
        if(mZ>=cameraZ){
            return;
        }
        float scale = focusedZ/(cameraZ-mZ);
        float invScale = 1/scale;
        int x = viewportWidth/2-(int)((viewportX-mBoundary.centerX()+mBoundary.width()/2)*scale);
        int y = viewportHeight/2-(int)((viewportY-mBoundary.centerY()+mBoundary.height()/2)*scale);
        int left = Math.max(x,0);
        int top = Math.max(y,0);
        int right = Math.min((int)(x + mBoundary.width()*scale), viewportWidth)-1;
        int bottom = Math.min((int)(y + mBoundary.height()*scale), viewportHeight)-1;

        if(mTexture!=null) {
            int srcLeft = (int)((left-x)*invScale);
            int srcTop = (int)((top-y)*invScale);
            int srcRight = (int)(srcLeft+(right-left)*invScale);
            int srcBottom = (int)(srcTop+(bottom-top)*invScale);

            drawer.drawObject(mTexture, left, top, right, bottom, srcLeft, srcTop, srcRight, srcBottom);
        }else{
            drawer.drawRect( left, top, right, bottom, mColor);
        }
    }

    public interface OnClickListener{
        public void onClick(Object object);
    }

    public void setHorizontalFlip(int degree){
        int halfWidth = (int)(mBoundary.width()*Math.abs(Math.cos(degree*Math.PI/180))*0.5/Math.abs(Math.cos(mDegree*Math.PI/180)));
        mDegree = degree%360;
        mBoundary.set(mBoundary.centerX()-halfWidth, mBoundary.top, mBoundary.centerX()+halfWidth, mBoundary.bottom);
    }

    public void setVerticalFlip(int degree){
        int halfHeight = (int)(mBoundary.height()*Math.abs(Math.cos(degree*Math.PI/180))*0.5/Math.abs(Math.cos(mDegree*Math.PI/180)));
        mDegree = degree%360;
        mBoundary.set(mBoundary.left, mBoundary.centerY()-halfHeight, mBoundary.right, mBoundary.centerY()+halfHeight);
    }

    public void setZ(float z){
        mZ = z;
    }
}