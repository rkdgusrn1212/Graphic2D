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

import android.graphics.Color;

import java.util.HashMap;
import java.util.List;

public class World {

    private int mWidth, mHeight;//0은 무한.
    HashMap<String, Object> mObjects;
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
    World(int width, int height, int viewportX, int viewportY, float cameraZ, float minCameraZ, float maxCameraZ, float focusedZ){
        mWidth = width;
        mHeight = height;
        mViewportX = viewportX;
        mViewportY = viewportY;
        mCameraZ = cameraZ;
        mMinCameraZ = minCameraZ;
        mMaxCameraZ = maxCameraZ;
        mFocusedZ = focusedZ;
        mObjects = new HashMap<>();
    }

    public void putObject(Object obj){
        mObjects.put(obj.mId, obj);
    }

    public void removeObject(String id){
        mObjects.remove(id);
    }

    void setViewportSize(int viewportWidth, int viewportHeight){
        mViewportWidth = viewportWidth;
        mViewportHeight = viewportHeight;
    }

    void render(Graphic2dDrawer drawer){
        drawer.clear(Color.BLACK);
        for(Object object: mObjects.values()){
            object.render(drawer, mViewportWidth, mViewportHeight, mCameraZ, mFocusedZ, mViewportX, mViewportY);
        }
    }

    void onTouch(TouchHandler touchHandler){
        List<TouchHandler.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchHandler.TouchEvent event = touchEvents.get(i);
            if(event.type == TouchHandler.TouchEvent.PINCH_TO_ZOOM){
                mCameraZ/=event.scale;
                mCameraZ = Math.max(mMinCameraZ, Math.min(mCameraZ, mMaxCameraZ));
                isDragging = false;
                isPressed = false;
                break;
            }else if(event.pointer == 0) {
                float scale = mFocusedZ/mCameraZ;
                if(isDragging){
                    if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                        int deltaX = (int)((event.x - startX)/scale);
                        int deltaY = (int)((event.y - startY)/scale);
                        if(mWidth==0){
                            mViewportX=mViewportX-deltaX;
                        }else{
                            mViewportX = Math.max(-mWidth/2, Math.min(mViewportX-deltaX,-mWidth/2+mWidth));
                        }
                        if(mHeight ==0){
                            mViewportY=mViewportY-deltaY;
                        }else{
                            mViewportY = Math.max(-mHeight/2, Math.min(mViewportY-deltaY,-mHeight/2+mHeight));
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
                        for(Object object : mObjects.values()){
                            object.onTouch((int)(mViewportX+(event.x-mViewportWidth/2)/scale), (int)(mViewportY+(event.y-mViewportHeight/2)/scale));
                        }
                    }
                    isPressed = false;
                }
            }
        }
    }
}