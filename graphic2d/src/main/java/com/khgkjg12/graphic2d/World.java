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
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.List;

public class World {

    private int mWidth, mHeight;//0은 무한.
    Object[] mObjects;
    boolean isDragging = false;
    int startX, startY;
    boolean isPressed = false;
    int mViewportX;
    int mViewportY;
    int mViewportWidth;
    int mViewportHeight;
    float mCameraZ;
    private float mMinCameraZ;
    private float mMaxCameraZ;
    float mFocusedZ;
    private int mBackgroundColor;
    private Texture mBackgroundTexture;
    private boolean mDragToMove;
    private boolean mPinchToZoom;
    private RectF mRectF;
    private int mMaxObjectCount;
    int mObjectCount;
    private OnClickWorldListener mOnClickWorldListener;


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
        mOnClickWorldListener = null;
    }

    public int getMaxObjectCount(){
        return mMaxObjectCount;
    }

    /**
     * 오직 콜백 메소드를 통해 전달된 World 에서만 호출해야함.
     * @param maxObjectCount
     */
    @WorkerThread
    public void changeMaxObjectCount(int maxObjectCount){
        mMaxObjectCount = maxObjectCount;
        Object[] tempObjects = mObjects;
        mObjects = new Object[maxObjectCount];
        System.arraycopy(tempObjects, 0, mObjects, 0, maxObjectCount);
    }

    /**
     * 오브젝트를 z 우선순위에 맞춰서 배열에 집어넣음.
     * @exception IndexOutOfBoundsException 배열이 가득 찬 상태에서 집어넣음.
     * @param object 오브젝트.
     */
    @WorkerThread
    public void putObject(@NonNull Object object){
        int i = 0;
        while(i!=mObjectCount&&mObjects[i].mZ > object.mZ){
            i++;
        }
        int j = mObjectCount++;
        while(j!=i){
            mObjects[j] = mObjects[j-1];
            j--;
        }
        object.calculateScale(this);
        mObjects[i] = object;
        if(object instanceof Group){
            ((Group) object).attached(this);
        }
    }

    /**
     * world 에서 오브젝트를 제거.
     * 호출시점에서 매개변수로 받은 제거대상이 무조건 존재하고 있음을 가정.
     * @exception IndexOutOfBoundsException 해당 id 와 일치하는 오브젝트가 없을때 발생.
     * @param object 삭제할 오브젝트 레퍼런스.
     */
    @WorkerThread
    public void removeObject(@NonNull Object object){
        int i = 0;
        while(mObjects[i++] != object);
        while(i!=mObjectCount){
            mObjects[i-1] = mObjects[i];
            i++;
        }
        mObjectCount--;
        if(object instanceof Group){
            ((Group)object).detached(this);
        }
    }

    @WorkerThread
    void setViewportSize(int viewportWidth, int viewportHeight){
        mViewportWidth = viewportWidth;
        mViewportHeight = viewportHeight;
        mRectF = new RectF(0, 0, mViewportWidth, mViewportHeight);
        calculateObjectXY();
    }

    @WorkerThread
    private void calculateObjectXY(){
        for(int i=0;i<mObjectCount; i++){
            mObjects[i].calculateRenderXY(this);
        }
    }

    @WorkerThread
    public void setBackgroundTexture(Texture texture){
        mBackgroundTexture = texture;
    }

    @WorkerThread
    void render(Graphic2dDrawer drawer){
        if(mBackgroundTexture==null){
            drawer.clear(mBackgroundColor);
        }else{
            drawer.drawObject(mBackgroundTexture, mRectF);
        }
        for(int i=mObjectCount-1; i>=0; i--){
            mObjects[i].render(drawer);
        }
    }

    @WorkerThread
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
                for(int j=0; j<mObjectCount; j++){
                    mObjects[j].calculateScale(this);
                }
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
                            calculateObjectXY();
                        }
                        startX = event.x;
                        startY = event.y;
                    }else{
                        isDragging = false;
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DOWN) {//onTouchDown 호출 및 영역 일치하면 pressed 로 바꿈.
                    startX = event.x;
                    startY = event.y;
                    isPressed = true;
                    for (int j = 0; j < mObjectCount; j++) {
                        if (mObjects[j].checkTouchDown(this, event.x, event.y)) {
                            isPressed = false;
                            break;
                        }
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){//pressed인 오브젝트중 영역일치 아닌 것들은 모두 onTouchCancel 호출
                    if (Math.abs(event.x - startX) > 50 || Math.abs(event.y - startY) > 50) {
                        isDragging = true;
                    }
                    for (int j = 0; j < mObjectCount; j++) {
                        mObjects[j].checkDrag(this, event.x, event.y);
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_UP) {//pressed인 오브젝트중 영역일치 아닌 것들은 모두 onTouchCancel 호출. 영역 일치하면 onClick 호출.
                    if(isPressed){
                        if(mOnClickWorldListener!=null){
                            if(!mOnClickWorldListener.onClickViewport(this, event.x, event.y)) {
                                mOnClickWorldListener.onClickBackground(this, event.x, event.y);
                            }
                        }
                    }
                    isPressed = false;
                    for (int j = 0; j < mObjectCount; j++) {
                        mObjects[j].checkTouchUp(this, event.x, event.y);
                    }
                }
            }
        }
    }
    @WorkerThread
    void setPinchToZoom(boolean pinchToZoom){
        mPinchToZoom = pinchToZoom;
    }

    @WorkerThread
    void setDragToMove(boolean dragToMove){
        mDragToMove = dragToMove;
    }

    @WorkerThread
    public void moveCameraXY(int x, int y){
        mViewportX = x;
        mViewportY = y;
        for(int j=0; j<mObjectCount; j++){
            mObjects[j].calculateRenderXY(this);
        }
    }

    public interface OnClickWorldListener{
        @WorkerThread
        void onClickBackground(World world, int x, int y);
        @WorkerThread
        boolean onClickViewport(World world, int x, int y);
    }

    public void setOnClickWorldListener(OnClickWorldListener onClickWorldListener){
        mOnClickWorldListener = onClickWorldListener;
    }
}