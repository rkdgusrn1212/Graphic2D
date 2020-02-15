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

import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class World {

    private int mWidth, mHeight;//0은 무한.
    Object[] mObjects;
    boolean isDragging = false;
    int startX, startY;
    boolean isPressed = false;
    private int mViewportX;
    private int mViewportY;
    private int mViewportWidth;
    private int mViewportHeight;
    private float mCameraZ;
    private float mMinCameraZ;
    private float mMaxCameraZ;
    private float mFocusedZ;
    private int mBackgroundColor;
    private Texture mBackgroundTexture;
    private boolean mDragToMove;
    private boolean mPinchToZoom;
    private RectF mRectF;
    private int mMaxObjectCount;
    private int mObjectCount;

    World(int width, int height, int viewportX, int viewportY, float cameraZ, float minCameraZ, float maxCameraZ, float focusedZ, int backgroundColor, boolean dragToMove, boolean pinchToZoom, int maxObjectCount){
        mWidth = width;
        mHeight = height;
        mViewportX = viewportX;
        mViewportY = viewportY;
        mCameraZ = cameraZ;
        mMinCameraZ = minCameraZ;
        mMaxCameraZ = maxCameraZ;
        mFocusedZ = focusedZ;
        mMaxObjectCount = maxObjectCount;
        mObjectCount = 0;
        mObjects = new Object[maxObjectCount];
        mBackgroundColor = backgroundColor;
        mDragToMove = dragToMove;
        mPinchToZoom = pinchToZoom;
        mRectF = new RectF(0, 0, mViewportWidth, mViewportHeight);
    }

    public int getMaxObjectCount(){
        return mMaxObjectCount;
    }

    /**
     * 오직 콜백 메소드를 통해 전달된 World 에서만 호출해야함.
     * @param maxObjectCount
     */
    public void changeMaxObjectCount(int maxObjectCount){
        mMaxObjectCount = maxObjectCount;
        Object[] tempObjects = mObjects;
        mObjects = new Object[maxObjectCount];
        System.arraycopy(tempObjects, 0, mObjects, 0, maxObjectCount);
    }

    /**
     * 오브젝트를 z 우선순위에 맞춰서 배열에 집어넣음.
     * @exception IndexOutOfBoundsException 배열이 가득 찬 상태에서 집어넣음.
     * @param obj 오브젝트.
     */
    public void putObject(Object obj){
        int i = 0;
        while(i!=mObjectCount&&mObjects[i].mZ > obj.mZ){
            i++;
        }
        int j = mObjectCount++;
        while(j!=i){
            mObjects[j] = mObjects[j-1];
            j--;
        }
        mObjects[i] = obj;
        if(obj instanceof GridObject){
            ((GridObject) obj).attached(this);
        }
    }

    /**
     * world 에서 오브젝트를 제거.
     * 호출시점에서 매개변수로 받은 제거대상이 무조건 존재하고 있음을 가정.
     * @exception IndexOutOfBoundsException 해당 id 와 일치하는 오브젝트가 없을때 발생.
     * @param object 삭제할 오브젝트 레퍼런스.
     */
    public void removeObject(Object object){
        int i = 0;
        while(mObjects[i++] != object);
        if(mObjects[i-1] instanceof GridObject){
            ((GridObject)mObjects[i-1]).detached(this);
        }
        while(i!=mObjectCount){
            mObjects[i-1] = mObjects[i++];
        }
        mObjectCount--;
    }

    void setViewportSize(int viewportWidth, int viewportHeight){
        mViewportWidth = viewportWidth;
        mViewportHeight = viewportHeight;
    }

    public void setBackgroundTexture(Texture texture){
        mBackgroundTexture = texture;
    }

    void render(Graphic2dDrawer drawer){
        if(mBackgroundTexture==null){
            drawer.clear(mBackgroundColor);
        }else{
            drawer.drawObject(mBackgroundTexture, mRectF);
        }
        for(int i=mObjectCount-1; i>=0; i--){
            mObjects[i].render(drawer, mViewportWidth, mViewportHeight, mCameraZ, mFocusedZ, mViewportX, mViewportY);
        }
    }

    void onTouch(TouchHandler touchHandler){
        List<TouchHandler.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchHandler.TouchEvent event = touchEvents.get(i);
            if(mPinchToZoom && event.type == TouchHandler.TouchEvent.PINCH_TO_ZOOM){
                mCameraZ /= event.scale;
                mCameraZ = Math.max(mMinCameraZ, Math.min(mCameraZ, mMaxCameraZ));
                isDragging = false;
                isPressed = false;
                break;
            }else if(event.pointer == 0) {
                float scale = mFocusedZ/mCameraZ;
                if(isDragging){
                    if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                        if(mDragToMove) {
                            int deltaX = (int) ((event.x - startX) / scale);
                            int deltaY = (int) ((event.y - startY) / scale);
                            if (mWidth == 0) {
                                mViewportX = mViewportX - deltaX;
                            } else {
                                mViewportX = Math.max(-mWidth / 2, Math.min(mViewportX - deltaX, -mWidth / 2 + mWidth));
                            }
                            if (mHeight == 0) {
                                mViewportY = mViewportY - deltaY;
                            } else {
                                mViewportY = Math.max(-mHeight / 2, Math.min(mViewportY - deltaY, -mHeight / 2 + mHeight));
                            }
                        }
                        startX = event.x;
                        startY = event.y;
                    }else{
                        isDragging = false;
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DOWN) {
                    startX = event.x;
                    startY = event.y;
                    isPressed = true;
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                    if(isPressed) {
                        if (Math.abs(event.x - startX) > 50 || Math.abs(event.y - startY) > 50) {
                            isDragging = true;
                        }
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_UP) {
                    if(isPressed){
                        for(int j=0; j< mObjectCount; j++){
                            if(mObjects[j].onTouch(this, event.x, event.y)){
                                break;
                            }
                        }
                    }
                    isPressed = false;
                }
            }
        }
    }
    void setPinchToZoom(boolean pinchToZoom){
        mPinchToZoom = pinchToZoom;
    }

    void setDragToMove(boolean dragToMove){
        mDragToMove = dragToMove;
    }
}