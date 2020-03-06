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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private OnTouchListener mOnTouchListener;
    boolean mIsPressed;
    boolean mConsumeTouchEvent;
    boolean mConsumeDragEvent;
    ChildListener mChildListener;
    boolean mClickableGroupMask;
    World mAttachedWorld;
    GroupObject mGroup;

    @WorkerThread
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
        mClickableGroupMask = true;
        mAttachedWorld = null;
        mGroup = null;
    }

    @WorkerThread
    void attached(World world){
        mAttachedWorld = world;
    }

    @WorkerThread
    void detached() {
        mAttachedWorld = null;
    }

    @WorkerThread
    void setClickableGroupMask(boolean clickableGroupMask){
        mClickableGroupMask = clickableGroupMask;
    }

    @WorkerThread
    void joinGroup(GroupObject group){
        mChildListener = group.mInnerItemListener;
        mClickableGroupMask = group.mClickableGroupMask;
        mGroup = group;
    }

    @WorkerThread
    void leaveGroup(){
        mChildListener = null;
        mClickableGroupMask = true;
        mGroup = null;
    }

    @WorkerThread
    public void setConsumeDragEvent(boolean consumeDragEvent){
        mConsumeDragEvent = consumeDragEvent;
    }

    @WorkerThread
    public boolean getConsumeDragEvent(){
        return mConsumeDragEvent;
    }

    @WorkerThread
    public void setConsumeTouchEvent(boolean consumeTouchEvent){
        mConsumeTouchEvent = consumeTouchEvent;
    }

    @WorkerThread
    public boolean getConsumeTouchEvent(){
        return mConsumeTouchEvent;
    }

    @WorkerThread
    public float getZ(){
        return mZ;
    }

    @WorkerThread
    public int getX(){
        return mX;
    }

    @WorkerThread
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
    public void onClick(){
        if(mOnClickListener!=null) mOnClickListener.onClick(mAttachedWorld, this);
        if(mChildListener!=null) mChildListener.onClick(mAttachedWorld, this);
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
        void onClick(@Nullable World attachedWorld, Object object);
    }

    /**
     * @exception IndexOutOfBoundsException 해당 오브젝트 없음.
     * @param z 새 z 좌표.
     */
    @WorkerThread
    public void moveZ(float z){
        if(mAttachedWorld!=null) {
            if (z >= mZ) {
                int i = 0;
                while (mAttachedWorld.mObjects[i].mZ > z) {
                    i++;
                }
                Object tempObj;
                int j = i;
                while (mAttachedWorld.mObjects[j] != this) {
                    j++;
                }
                tempObj = mAttachedWorld.mObjects[j];
                while (i != j) {
                    mAttachedWorld.mObjects[j] = mAttachedWorld.mObjects[j - 1];
                    j--;
                }
                mAttachedWorld.mObjects[j] = tempObj;
            } else {
                int i = mAttachedWorld.mObjectCount - 1;
                while (mAttachedWorld.mObjects[i].mZ <= z) {
                    i--;
                }
                Object tempObj;
                int j = i;
                while (mAttachedWorld.mObjects[j] != this) {
                    j--;
                }
                tempObj = mAttachedWorld.mObjects[j];
                while (i != j) {
                    mAttachedWorld.mObjects[j] = mAttachedWorld.mObjects[j + 1];
                    j++;
                }
                mAttachedWorld.mObjects[j] = tempObj;
            }
        }
        mZ = z;
        if(mAttachedWorld!=null) calculateScale(mAttachedWorld);
    }

    @WorkerThread
    public void moveXY(int x, int y){
        mX = x;
        mY = y;
        if(mAttachedWorld!=null) calculateRenderXY(mAttachedWorld);
    }

    @WorkerThread
    void calculateScale(@NonNull World world){
        if(mZ<world.mCameraZ) {
            mIsInCameraRange = true;
            mScale = world.mFocusedZ / (world.mCameraZ - mZ);
            calculateRenderXY(world);
        }else{
            mIsInCameraRange = false;
        }
    }

    @WorkerThread
    void calculateRenderXY(@NonNull World world){
        mRenderX = (world.mViewportWidth / 2f) - (world.mViewportX - mX) * mScale;
        mRenderY = (world.mViewportHeight / 2f) - (world.mViewportY - mY) * mScale;
        calculateBoundary();
    }

    public interface OnTouchListener{
        @WorkerThread
        void onTouchDown(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
        @WorkerThread
        void onTouchUp(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
        @WorkerThread
        void onTouchCancel(@Nullable World attachedWorld, @NonNull Object object);
        @WorkerThread
        void onTouchDrag(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
    }

    @WorkerThread
    boolean checkTouchDown(int x, int y){
        if(mClickable && mClickableGroupMask&&mIsInCameraRange&&checkBoundary(x, y)){
            mIsPressed = true;
            onTouchDown(x, y);
            return mConsumeTouchEvent;
        }else{
            return false;
        }
    }

    @WorkerThread
    void checkTouchCancel(){
        if(mIsPressed){
            mIsPressed = false;
            onTouchCancel();
        }
    }

    @WorkerThread
    void checkTouchUp(int x, int y){
        if(mIsPressed){
            mIsPressed = false;
            if(mIsInCameraRange&&checkBoundary(x, y)){
                onTouchUp(x, y);
                onClick();
            }else{
                onTouchCancel();
            }
        }
    }

    @WorkerThread
    boolean checkDrag(int x, int y){
        if(mIsPressed){
            if(mIsInCameraRange&&checkBoundary(x, y)){
                onTouchDrag(x, y);
                return mConsumeDragEvent;
            }else{
                mIsPressed = false;
                onTouchCancel();
            }
        }
        return false;
    }

    @WorkerThread
    public void onTouchDown(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchDown(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchDown(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchDrag(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchDrag(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchDrag(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchUp(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchUp(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchUp(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchCancel(){
        if (mOnTouchListener != null) mOnTouchListener.onTouchCancel(mAttachedWorld, this);
        if (mChildListener != null) mChildListener.onTouchCancel(mAttachedWorld, this);
    }


    interface ChildListener{
        @WorkerThread
        void onClick(@Nullable World attachedWorld, @NonNull Object object);
        @WorkerThread
        void onTouchDown(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
        @WorkerThread
        void onTouchUp(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
        @WorkerThread
        void onTouchCancel(@Nullable World attachedWorld, @NonNull Object object);
        @WorkerThread
        void onTouchDrag(@Nullable World attachedWorld, @NonNull Object object, int x, int y);
    }

}