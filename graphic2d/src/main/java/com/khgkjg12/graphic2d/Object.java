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

import android.support.annotation.WorkerThread;

public abstract class Object {

    private boolean mVisibility;
    boolean mClickable;
    private OnClickListener mOnClickListener;
    float mZ;
    int mX, mY;
    boolean mIsInCameraRange;
    float mScale;
    float mRenderX;
    float mRenderY;

    public Object(float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener){
        mVisibility = visibility;
        mClickable = clickable;
        mZ = z;
        mX = x;
        mY = y;
        mOnClickListener = onClickListener;
    }

    public float getZ(){
        return mZ;
    }

    public int getX(){
        return mX;
    }

    public int getY(){
        return mY;
    }

    /**
     * World의 콜백 메서드에서만 사용.
     * @param visible
     */
    @WorkerThread
    public void setVisibility(boolean visible){
        mVisibility = visible;
    }

    /**
     * World의 콜백 메서드에서만 사용.
     * @param clickable
     */
    @WorkerThread
    public void setClickable(boolean clickable){
        mClickable = clickable;
    }

    @WorkerThread
    boolean onTouch(World world, int x, int y){
        if(mIsInCameraRange&&checkBoundary(x, y)){
            if(mClickable&&mOnClickListener!=null) {
                return mOnClickListener.onClick(world, this);
            }
            return mClickable;
        }
        return false;
    }

    /**
     * 경계선 채크 메소드.
     * @return x, y 가 경계선 안에 있으면 참.
     */
    @WorkerThread
    abstract boolean checkBoundary(int x, int y);

    @WorkerThread
    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    @WorkerThread
    void render(Graphic2dDrawer drawer){
        if(mIsInCameraRange&&mVisibility){
            draw(drawer);
        }
    }

    /**
     * 오브젝트의 경계를 계산.
     */
    @WorkerThread
    abstract void calculateBoundary();

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     */
    @WorkerThread
    abstract void draw(Graphic2dDrawer drawer);

    public interface OnClickListener{
        @WorkerThread
        public boolean onClick(World world, Object object);
    }

    /**
     * @exception IndexOutOfBoundsException 해당 오브젝트 없음.
     * @param world
     * @param z 새 z 좌표.
     */
    @WorkerThread
    public void moveZ(World world, float z){
        if(z>=mZ){
            int i=0;
            while(world.mObjects[i].mZ>z){
                i++;
            }
            Object tempObj;
            int j = i;
            while(world.mObjects[j]!=this){
                j++;
            }
            tempObj = world.mObjects[j];
            while(i!=j) {
                world.mObjects[j] = world.mObjects[j-1];
                j--;
            }
            world.mObjects[j] = tempObj;
        }else{
            int i = world.mObjectCount-1;
            while(world.mObjects[i].mZ<=z){
                i--;
            }
            Object tempObj;
            int j = i;
            while(world.mObjects[j]!=this){
                j--;
            }
            tempObj = world.mObjects[j];
            while(i!=j) {
                world.mObjects[j] = world.mObjects[j+1];
                j++;
            }
            world.mObjects[j] = tempObj;
        }
        mZ = z;
        calculateScale(world);
    }

    @WorkerThread
    public void moveXY(World world, int x, int y){
        mX = x;
        mY = y;
        calculateRenderXY(world);
    }

    @WorkerThread
    void calculateScale(World world){
        if(mZ<world.mCameraZ) {
            mIsInCameraRange = true;
            mScale = world.mFocusedZ / (world.mCameraZ - mZ);
            calculateRenderXY(world);
        }else{
            mIsInCameraRange = false;
        }
    }

    @WorkerThread
    void calculateRenderXY(World world){
        mRenderX = (world.mViewportWidth / 2f) - (world.mViewportX - mX) * mScale;
        mRenderY = (world.mViewportHeight / 2f) - (world.mViewportY - mY) * mScale;
        calculateBoundary();
    }

}