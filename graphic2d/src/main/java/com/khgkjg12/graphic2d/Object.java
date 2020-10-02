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

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.widget.ProgressBar;

import java.util.ArrayList;

public abstract class Object {

    protected boolean mVisibility;
    protected boolean mClickable;
    protected ArrayList<OnClickListener> mOnClickListeners = null;
    protected float mZ;
    protected float mX, mY;
    protected boolean mIsInCameraRange;
    protected float mScale;
    protected float mRenderX;
    protected float mRenderY;
    protected ArrayList<OnTouchListener> mOnTouchListeners = null;
    protected boolean mIsPressed = false;
    protected boolean mConsumeTouchEvent = true;
    protected ChildListener mChildListener = null;
    protected World mAttachedWorld = null;
    protected GroupObject mGroup = null;
    protected int mPressedX, mPressedY;
    protected Object mLayerHost = null;
    protected Object[] mForegroundObjects = null;
    protected Object[] mBackgroundObjects = null;

    @WorkerThread
    public Object(float z, float x, float y, boolean visibility, boolean clickable) {
        mVisibility = visibility;
        mClickable = clickable;
        mZ = z;
        mX = x;
        mY = y;
    }

    @WorkerThread
    void attachedHost(Object object){
        mLayerHost = object;
    }

    @WorkerThread
    void detachedHost(){
        mLayerHost = null;
    }

    @WorkerThread
    void attached(World world){
        mAttachedWorld = world;
        calculateScale();
        if(mForegroundObjects!=null)
            for(Object obj : mForegroundObjects)
                if(obj!=null)
                    obj.attached(world);
        if(mBackgroundObjects!=null)
            for(Object obj : mBackgroundObjects)
                if(obj!=null)
                    obj.attached(world);
    }

    @WorkerThread
    void detached() {
        if(mForegroundObjects!=null)
            for(Object obj : mForegroundObjects)
                if(obj!=null)
                    obj.detached();
        if(mBackgroundObjects!=null)
            for(Object obj : mBackgroundObjects)
                if(obj!=null)
                    obj.detached();
        mAttachedWorld = null;
    }

    @WorkerThread
    void joinGroup(GroupObject group){
        mChildListener = group.mInnerItemListener;
        mGroup = group;
    }

    @WorkerThread
    void leaveGroup(){
        mChildListener = null;
        mGroup = null;
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
    public float getX(){
        return mX;
    }

    @WorkerThread
    public float getY(){
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

    @WorkerThread
    public boolean getVisibility(){
        return mVisibility;
    }

    @WorkerThread
    public boolean isVisible(){
        return mVisibility&&(mGroup==null||mGroup.isVisible());
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
    public boolean getClickable(){
        return mClickable;
    }

    @WorkerThread
    public void onClick(){
        if(mOnClickListeners!=null) {
            int size = mOnClickListeners.size();
            for(int i=0;i<size;i++) mOnClickListeners.get(i).onClick(mAttachedWorld, this);
        }
        if(mChildListener!=null) mChildListener.onClick(mAttachedWorld, this);
    }

    /**
     * 경계선 채크 메소드.
     * @return x, y 가 경계선 안에 있으면 참.
     */
    @WorkerThread
    abstract boolean checkBoundary(int x, int y);

    @WorkerThread
    public void addOnClickListener(@NonNull OnClickListener onClickListener){
        if(mOnClickListeners==null) {
            mOnClickListeners = new ArrayList<>();
        }
        if(!mOnClickListeners.contains(onClickListener)) {
            mOnClickListeners.add(onClickListener);
        }
    }

    @WorkerThread
    public void removeOnClickListener(@NonNull OnClickListener onClickListener){
        if(mOnClickListeners!=null) mOnClickListeners.remove(onClickListener);
    }

    @WorkerThread
    public void addOnTouchListener(@NonNull OnTouchListener onTouchListener){
        if(mOnTouchListeners==null) {
            mOnTouchListeners = new ArrayList<>();
        }
        if(!mOnTouchListeners.contains(onTouchListener)) {
            mOnTouchListeners.add(onTouchListener);
        }
    }

    @WorkerThread
    public void removeOnTouchListener(@NonNull OnClickListener onTouchListener){
        if(mOnTouchListeners!=null) mOnTouchListeners.remove(onTouchListener);
    }

    @WorkerThread
    void render(Canvas canvas){
        if(mIsInCameraRange&&isVisible()){
            if(mBackgroundObjects!=null)
                for(int i=mBackgroundObjects.length-1; i>=0; i--)
                    if(mBackgroundObjects[i]!=null)
                        mBackgroundObjects[i].render(canvas);
            draw(canvas);
            if(mForegroundObjects!=null)
                for(Object obj : mForegroundObjects)
                    if(obj!=null)
                        obj.render(canvas);
        }
    }

    /**
     * 오브젝트의 경계를 계산.
     */
    @WorkerThread
    abstract void calculateBoundary();

    @WorkerThread
    void calculateAndCheckBoundary(){
        calculateBoundary();
        if(mIsPressed&&!checkBoundary(mPressedX, mPressedY)){
            mIsPressed = false;
            onTouchCancel();
        }
    }

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     */
    @WorkerThread
    abstract protected void draw(Canvas canvas);


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
        if(mAttachedWorld!=null) calculateScale();
    }

    @WorkerThread
    public void moveXY(float x, float y){
        mX = x;
        mY = y;
        if(mAttachedWorld!=null) {
            calculateRenderXY();
            if (mForegroundObjects != null)
                for (Object object : mForegroundObjects)
                    if (object != null)
                        object.calculateRenderXY();
            if (mBackgroundObjects != null)
                for (Object object : mBackgroundObjects)
                    if (object != null)
                        object.calculateRenderXY();
        }
        //레이어들은 attached 안되어있기때문에 그냥 mX,mY만 바뀜.

    }

    @WorkerThread
    void calculateScale(){
        if(mZ<mAttachedWorld.mCameraZ) {
            mIsInCameraRange = true;
            mScale = mAttachedWorld.mFocusedZ / (mAttachedWorld.mCameraZ - mZ);
            calculateRenderXY();
        }else{
            mIsInCameraRange = false;
        }
    }

    @WorkerThread
    void calculateRenderXY(){
        if(mLayerHost==null) {
            mRenderX = mAttachedWorld.mViewportWidth/2f + (mX - mAttachedWorld.mViewportX) * mScale;
            mRenderY = mAttachedWorld.mViewportHeight/2f + (mY - mAttachedWorld.mViewportY) * mScale;
        }else{
            mRenderX = mLayerHost.mRenderX + mX * mLayerHost.mScale;
            mRenderY = mLayerHost.mRenderY + mY * mLayerHost.mScale;
        }
        calculateAndCheckBoundary();
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

    /**
     * @exception IndexOutOfBoundsException
     * @param object
     * @param layer
     */
    public void putForegroundLayer(@Nullable Object object, int layer){
        mForegroundObjects[layer] = object;
        if(object!=null) {
            object.attachedHost(this);
            if (mAttachedWorld != null)
                object.attached(mAttachedWorld);
        }
    }

    public void enableForeground(int layers){
        mForegroundObjects = new Object[layers];
    }

    public void disableForeground(){
        for(Object obj : mForegroundObjects)
            if(obj!=null) {
                if(mAttachedWorld!=null)
                    obj.detached();
                obj.detachedHost();
            }
        mForegroundObjects = null;
    }

    /**
     * @exception IndexOutOfBoundsException
     * @param object
     * @param layer
     */
    public void putBackgroundLayer(@Nullable Object object, int layer){
        mBackgroundObjects[layer] = object;
        if(object!=null) {
            object.attachedHost(this);
            if (mAttachedWorld != null)
                object.attached(mAttachedWorld);
        }
    }

    public void enableBackground(int layers){
        mBackgroundObjects = new Object[layers];
    }

    public void disableBackground(){
        for(Object obj :mBackgroundObjects)
            if(obj!=null) {
                if(mAttachedWorld!=null)
                    obj.detached();
                obj.detachedHost();
            }
        mBackgroundObjects = null;
    }

    @WorkerThread
    public boolean isClickable(){
        return mClickable && (mGroup==null||mGroup.isClickable());
    }

    @WorkerThread
    boolean checkTouchDown(int x, int y){
        if(isClickable() &&mIsInCameraRange&&checkBoundary(x, y)){
            mIsPressed = true;
            mPressedX = x;
            mPressedY = y;
            onTouchDown(x, y);
            return mConsumeTouchEvent;
        }else{
            return false;
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
    void checkDrag(int x, int y){
        if(mIsPressed){
            if(mIsInCameraRange&&checkBoundary(x, y)){
                mPressedX = x;
                mPressedY = y;
                onTouchDrag(x, y);
            }else{
                mIsPressed = false;
                onTouchCancel();
            }
        }
    }

    @WorkerThread
    public void onTouchDown(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchDown(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchDown(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchDrag(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchDrag(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchDrag(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchUp(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchUp(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchUp(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchCancel(){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchCancel(mAttachedWorld, this);
        }
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

    Snapshot getSnapshot(){
        return new Snapshot();
    }
    class Snapshot{

    }
}