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

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public abstract class Object {

    private boolean mVisibility;
    boolean mClickable;
    OnClickListener mOnClickListener;
    float mZ;
    int mX, mY;
    boolean mIsInCameraRange;
    float mScale;
    float mRenderX;
    float mRenderY;
    private OnTouchListener mOnTouchListener;
    private boolean mIsPressed;
    private boolean mConsumeTouchEvent;
    private boolean mConsumeDragEvent;
    private ChildListener mChildListener;

    public Object(float z, int x, int y, boolean visibility, boolean clickable, OnClickListener onClickListener){
        mVisibility = visibility;
        mClickable = clickable;
        mZ = z;
        mX = x;
        mY = y;
        mOnClickListener = onClickListener;
        mIsPressed = false;
        mConsumeTouchEvent = true;
        mConsumeDragEvent = false;
    }

    @WorkerThread
    void setChildListener(ChildListener childListener){
        mChildListener = childListener;
    }

    @WorkerThread
    public void setConsumeDragEvent(boolean consumeDragEvent){
        mConsumeDragEvent = consumeDragEvent;
    }

    public boolean getConsumeDragEvent(){
        return mConsumeDragEvent;
    }

    @WorkerThread
    public void setConsumeTouchEvent(boolean consumeTouchEvent){
        mConsumeTouchEvent = consumeTouchEvent;
    }

    public boolean getConsumeTouchEvent(){
        return mConsumeTouchEvent;
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
    public void setClickable(World world, boolean clickable){
        mClickable = clickable;
        checkTouchCancel(world);
    }

    @WorkerThread
    public void onClick(World world){
        if(mOnClickListener!=null) mOnClickListener.onClick(world, this);
        if(mChildListener!=null) mChildListener.onClick(world, this);
    }

    /**
     * 경계선 채크 메소드.
     * @return x, y 가 경계선 안에 있으면 참.
     */
    @WorkerThread
    abstract boolean checkBoundary(int x, int y);

    @WorkerThread
    public void setOnClickListener(@Nullable OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    @WorkerThread
    public void setOnTouchListener(@Nullable OnTouchListener onTouchListener){
        mOnTouchListener = onTouchListener;
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
        void onClick(World world, Object object);
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

    public interface OnTouchListener{
        @WorkerThread
        void onTouchDown(World world, Object object, int x, int y);
        @WorkerThread
        void onTouchUp(World world, Object object, int x, int y);
        @WorkerThread
        void onTouchCancel(World world, Object object);
        @WorkerThread
        void onTouchDrag(World world, Object object, int x, int y);
    }

    @WorkerThread
    boolean checkTouchDown(World world, int x, int y){
        if(mIsInCameraRange&&checkBoundary(x, y)){
            if(mClickable) {
                mIsPressed = true;
                onTouchDown(world, x, y);
                return mConsumeTouchEvent;
            }
        }
        return false;
    }

    @WorkerThread
   public void onTouchDown(World world, int x, int y){
        if(mOnTouchListener!=null){
            mOnTouchListener.onTouchDown(world, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchDown(world, this, x, y);
    }

    @WorkerThread
    void checkTouchCancel(World world){
        if(mIsPressed){
            mIsPressed = false;
            onTouchCancel(world);
        }
    }

    @WorkerThread
    void checkTouchUp(World world, int x, int y){
        if(mIsPressed){
            mIsPressed = false;
            if(mIsInCameraRange&&checkBoundary(x, y)){
                onTouchUp(world, x, y);
                onClick(world);
            }else{
                onTouchCancel(world);
            }
        }
    }

    @WorkerThread
    boolean checkDrag(World world, int x, int y){
        if(mIsPressed){
            if(mIsInCameraRange&&checkBoundary(x, y)){
                onTouchDrag(world, x, y);
                return mConsumeDragEvent;
            }else{
                mIsPressed = false;
                onTouchCancel(world);
            }
        }
        return false;
    }

    @WorkerThread
    public void onTouchDrag(World world, int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchDrag(world, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchDrag(world, this, x, y);
    }

    @WorkerThread
    public void onTouchUp(World world, int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchUp(world, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchUp(world, this, x, y);
    }

    @WorkerThread
    public void onTouchCancel(World world){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchCancel(world, this);
        if(mChildListener!=null) mChildListener.onTouchCancel(world, this);
    }


    interface ChildListener{
        @WorkerThread
        void onClick(World world, Object object);
        @WorkerThread
        void onTouchDown(World world, Object object, int x, int y);
        @WorkerThread
        void onTouchUp(World world, Object object, int x, int y);
        @WorkerThread
        void onTouchCancel(World world, Object object);
        @WorkerThread
        void onTouchDrag(World world, Object object, int x, int y);
    }

}