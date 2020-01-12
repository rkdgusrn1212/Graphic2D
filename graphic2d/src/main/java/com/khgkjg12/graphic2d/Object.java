/**
 * Copyright 2018 Hyungu Kang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.khgkjg12.graphic2d;

import android.graphics.Rect;

public class Object {

    private Texture mTexture;
    private int mColor;
    Rect mBoundary;
    private int mWidth, mHeight;
    private boolean mVisibility;
    String mId;
    private OnClickListener mOnClickListener;
    private float mZ;
    private int mHoriaontalDegree;
    private int mVerticalDegree;


    public Object(Texture texture, int width, int height, String id) {
        this(texture, width, height, 0, 0, 0, id);
    }

    public Object(int color, int width, int height, String id) {
        this(color, width, height, 0, 0, 0, id);
    }

    public Object(Texture texture, int width, int height, int z, int degreeH, int degreeV, String id){
        mTexture = texture;
        mColor = 0;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mVisibility = true;
        mId = id;
        mZ = z;
        mHoriaontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    public Object(int color, int width, int height, int z, int degreeH, int degreeV, String id){
        mTexture = null;
        mColor = color;
        mWidth = width;
        mHeight = height;
        mBoundary = new Rect();
        mVisibility = true;
        mId = id;
        mZ = z;
        mHoriaontalDegree = degreeH%360;
        mVerticalDegree = degreeV%360;
    }

    public void setTexture(Texture texture){
        mTexture = texture;
    }

    //객체의 좌표를 설정.
    public void setPosition(int x, int y){
        mBoundary.set(x-mWidth/2, y-mHeight/2, x+mWidth/2, y+mHeight/2);
    }

    //객체를 물리적 상태를 변화.
    public void setVisibility(boolean visible){
        mVisibility = visible;
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
        if(!mVisibility||mZ>=cameraZ){
            return;
        }
        float scale = focusedZ/(cameraZ-mZ);
        float invScale = 1/scale;


        int width = (int)(mBoundary.width()*Math.abs(Math.cos(mHoriaontalDegree*Math.PI/180)));
        int height = (int)(mBoundary.height()*Math.abs(Math.cos(mVerticalDegree*Math.PI/180)));

        int x = viewportWidth/2-(int)((viewportX-mBoundary.centerX()+width*0.5)*scale);
        int y = viewportHeight/2-(int)((viewportY-mBoundary.centerY()+height*0.5)*scale);
        int left = Math.max(x,0);
        int top = Math.max(y,0);
        int right = Math.min((int)(x+width*scale), viewportWidth);
        int bottom = Math.min((int)(y+height*scale), viewportHeight);

        if(mTexture!=null) {
            float srcLeftOffset = (left-x)*invScale/width;
            float srcTopOffset = (top-y)*invScale/height;
            float srcWidth = (right-left)*invScale/width;
            float srcHeight = (bottom-top)*invScale/height;

            drawer.drawObject(mTexture, left, top, right, bottom, srcLeftOffset, srcTopOffset, srcWidth, srcHeight);
        }else{
            drawer.drawRect( left, top, right, bottom, mColor);
        }
    }

    public interface OnClickListener{
        public void onClick(Object object);
    }

    public void setHorizontalFlip(int degree){
        mVerticalDegree = 0;
        mHoriaontalDegree = degree%360;
    }

    public void setVerticalFlip(int degree){
        mHoriaontalDegree = 0;
        mVerticalDegree = degree%360;
    }

    public void setZ(float z){
        mZ = z;
    }

    public float getZ(){
        return mZ;
    }
}