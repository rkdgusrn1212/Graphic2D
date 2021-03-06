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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

public class World {

    private int mWidth, mHeight;//0은 무한.
    Object[] mObjects;
    Widget[] mWidgets;
    int mWidgetCount;
    private int mMaxWidgetCount;
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
    private int mBackgroundR, mBackgroundG, mBackgroundB;
    private Bitmap mBackgroundBitmap;
    private boolean mDragToMove;
    private boolean mPinchToZoom;
    private RectF mRectF;
    private int mMaxObjectCount;
    int mObjectCount;
    private ArrayList<OnClickBackgroundListener> mOnClickBackgroundListeners = null;
    private OnCameraGestureListener mOnCameraGestureListener = null;
    private Paint mBitmapPaint;

    World(int width, int height, int viewportX, int viewportY, float cameraZ, float minCameraZ, float maxCameraZ, float focusedZ, int backgroundColor, boolean dragToMove, boolean pinchToZoom, int maxObjectCount, int maxWidgetCount){
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
        mMaxWidgetCount = maxWidgetCount;
        mWidgetCount = 0;
        mWidgets = new Widget[maxWidgetCount];
        mBackgroundR = (backgroundColor & 0xff0000) >> 16;
        mBackgroundG = (backgroundColor & 0xff00) >> 8;
        mBackgroundB = (backgroundColor & 0xff);
        mDragToMove = dragToMove;
        mPinchToZoom = pinchToZoom;
    }

    public Paint getPaint(){
        if(mBitmapPaint==null) mBitmapPaint = new Paint();
        return mBitmapPaint;
    }
    
    public void setOnChangeCameraGestureListener(OnCameraGestureListener onCameraGestureListener){
        mOnCameraGestureListener = onCameraGestureListener;
    }

    public interface OnCameraGestureListener {
        void onPinchToZoom(World world, float lastZ, float z);
        void onDragToMove(World world, float lastX, float x, float lastY, float y);
    }

    @WorkerThread
    public int getViewportWidth(){
        return mViewportWidth;
    }

    @WorkerThread
    public int getViewportHeight(){
        return mViewportHeight;
    }

    @WorkerThread
    public int getCameraX(){
        return mViewportX;
    }

    @WorkerThread
    public int getCameraY(){
        return mViewportY;
    }

    @WorkerThread
    public float getCameraZ(){
        return mCameraZ;
    }

    @WorkerThread
    public float getMaxCameraZ(){
        return mMaxCameraZ;
    }

    @WorkerThread
    public float getMinCameraZ(){
        return mMinCameraZ;
    }
    @WorkerThread
    public float getFocusedCameraZ(){
        return mFocusedZ;
    }

    public int getMaxObjectCount(){
        return mMaxObjectCount;
    }

    public int getMaxWidgetCount(){
        return mMaxWidgetCount;
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

    @WorkerThread
    public void changeMaxWidgetCount(int maxWidgetCount){
        mMaxWidgetCount = maxWidgetCount;
        Widget[] tempWidgets = mWidgets;
        mWidgets = new Widget[maxWidgetCount];
        System.arraycopy(tempWidgets, 0, mWidgets, 0, maxWidgetCount);
    }

    /**
     * 오브젝트를 z 우선순위에 맞춰서 배열에 집어넣음.
     * @exception IndexOutOfBoundsException 배열이 가득 찬 상태에서 집어넣음.
     * @exception PutAttachedObjectException Attempt to insert an object that has already been inserted.
     * @param object 오브젝트.
     */
    @WorkerThread
    public void putObject(@NonNull Object object){
        if(object.mAttachedWorld==null) {
            int i = 0;
            while (i != mObjectCount && mObjects[i].mZ > object.mZ) {
                i++;
            }
            int j = mObjectCount++;
            while (j != i) {
                mObjects[j] = mObjects[j - 1];
                j--;
            }
            mObjects[i] = object;
            mObjects[i].attached(this);
        }else{
            throw new PutAttachedObjectException();
        }
    }

    @WorkerThread
    public void putWidget(@NonNull Widget widget){
        if(widget.mAttachedWorld==null) {
            int i = 0;
            while (i != mWidgetCount && mWidgets[i].mZ > widget.mZ) {
                i++;
            }
            int j = mWidgetCount++;
            while (j != i) {
                mWidgets[j] = mWidgets[j - 1];
                j--;
            }
            mWidgets[i] = widget;
            mWidgets[i].attached(this);
        }else{
            throw new PutAttachedWidgetException();
        }
    }

    /**
     * world 에서 오브젝트를 제거.
     * 호출시점에서 매개변수로 받은 제거대상이 무조건 존재하고 있음을 가정.
     * @exception IndexOutOfBoundsException 해당 id 와 일치하는 오브젝트가 없을때 발생.
     * @exception RemoveChildFromWorldException 그룹이 아닌 World에서 자식 오브젝트를 제거하려함.
     * @param object 삭제할 오브젝트 레퍼런스.
     */
    @WorkerThread
    public void removeObject(@NonNull Object object){
        if(object.mGroup==null) {
            detachObject(object);
        }else{
            throw new RemoveChildFromWorldException();
        }
    }

    void detachObject(@NonNull Object object){
        int i = 0;
        while (mObjects[i++] != object) ;
        while (i != mObjectCount) {
            mObjects[i - 1] = mObjects[i];
            i++;
        }
        mObjectCount--;
        object.detached();
    }

    @WorkerThread
    public void removeWidget(@NonNull Widget widget){
        if(widget.mGroup==null) {
            detachWidget(widget);
        }else{
            throw new RemoveChildFromWorldException();
        }
    }

    void detachWidget(@NonNull Widget widget){
        int i = 0;
        while (mWidgets[i++] != widget) ;
        while (i != mWidgetCount) {
            mWidgets[i - 1] = mWidgets[i];
            i++;
        }
        mWidgetCount--;
        widget.detached();
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
            mObjects[i].calculateRenderXY();
        }
    }

    @WorkerThread
    public void setBackgroundBitmap(Bitmap bitmap){
        mBackgroundBitmap = bitmap;
    }

    @WorkerThread
    public void changeBackgroundColor(int color){
        mBackgroundR = (color >> 16) & 0xff;
        mBackgroundG = (color >> 8) & 0xff;
        mBackgroundB = color & 0xff;
    }

    @WorkerThread
    void render(Canvas canvas){
        if(mBackgroundBitmap==null){
            canvas.drawRGB(mBackgroundR, mBackgroundG,
                    mBackgroundB);
        }else{
            canvas.drawBitmap(mBackgroundBitmap, null, mRectF, mBitmapPaint);
        }
        for(int i=mObjectCount-1; i>=0; i--){
            mObjects[i].render(canvas);
        }
        for(int i=mWidgetCount-1; i>=0; i--){
            mWidgets[i].render(canvas, false);
        }
    }

    @WorkerThread
    void onTouch(TouchHandler touchHandler){
        List<TouchHandler.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchHandler.TouchEvent event = touchEvents.get(i);
            if(mPinchToZoom && event.type == TouchHandler.TouchEvent.PINCH_TO_ZOOM){
                float cameraZ = mCameraZ/event.scale;
                if(cameraZ<mMinCameraZ){
                    cameraZ = mMinCameraZ;
                }else if(cameraZ>mMaxCameraZ){
                    cameraZ = mMaxCameraZ;
                }
                if(mOnCameraGestureListener !=null) mOnCameraGestureListener.onPinchToZoom(this, mCameraZ, cameraZ);
                moveCamreraZ(cameraZ);
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
                            int viewportX = mViewportX - deltaX;
                            int viewportY = mViewportY - deltaY;
                            if (mWidth != 0) {
                                viewportX = Math.max(-mWidth / 2, Math.min(viewportX, -mWidth / 2 + mWidth));
                            }
                            if (mHeight != 0) {
                                viewportY = Math.max(-mHeight / 2, Math.min(viewportY, -mHeight / 2 + mHeight));
                            }
                            if(mOnCameraGestureListener !=null) mOnCameraGestureListener.onDragToMove(this, mViewportX, viewportX, mViewportY, viewportY);
                            moveCameraXY(viewportX, viewportY);
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
                    for(int j=0; j<mWidgetCount; j++){
                        if(mWidgets[j].checkTouchDown(event.x, event.y)){
                            isPressed = false;
                            break;
                        }
                    }
                    if(isPressed) {
                        for (int j = 0; j < mObjectCount; j++) {
                            if (mObjects[j].checkTouchDown(event.x, event.y)) {
                                isPressed = false;
                                break;
                            }
                        }
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){//pressed인 오브젝트중 영역일치 아닌 것들은 모두 onTouchCancel 호출
                    for(int j=0;j<mWidgetCount; j++){
                        mWidgets[j].checkDrag(event.x, event.y);
                    }
                    for (int j=0; j < mObjectCount; j++) {
                        mObjects[j].checkDrag(event.x, event.y);
                    }
                    if(isPressed){
                        if (Math.abs(event.x - startX) > 50 || Math.abs(event.y - startY) > 50) {
                            isDragging = true;
                        }
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_UP) {//pressed인 오브젝트중 영역일치 아닌 것들은 모두 onTouchCancel 호출. 영역 일치하면 onClick 호출.
                    if(isPressed) {
                        if (mOnClickBackgroundListeners != null) {
                            int size = mOnClickBackgroundListeners.size();
                            for(int j=0; j<size;j++)
                                mOnClickBackgroundListeners.get(j).onClickBackground(this, event.x, event.y);
                        }
                        isPressed = false;
                    }
                    for(int j=0; j<mWidgetCount; j++){
                        mWidgets[j].checkTouchUp(event.x, event.y);
                    }
                    for (int j = 0; j < mObjectCount; j++) {
                        mObjects[j].checkTouchUp(event.x, event.y);
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
        calculateObjectXY();
    }

    @WorkerThread
    public void moveCamreraZ(float z){
        mCameraZ = z;
        for(int j=0; j<mObjectCount; j++){
            mObjects[j].calculateScale();
        }
    }

    public interface OnClickBackgroundListener{
        @WorkerThread
        void onClickBackground(World world, int x, int y);
    }

    public void addOnClickBackgroundListener(OnClickBackgroundListener onClickBackgroundListener){
        if(mOnClickBackgroundListeners==null)
            mOnClickBackgroundListeners = new ArrayList<>();
        if(!mOnClickBackgroundListeners.contains(onClickBackgroundListener))
            mOnClickBackgroundListeners.add(onClickBackgroundListener);
    }

    public void removeOnClickBackgroundListener(OnClickBackgroundListener onClickBackgroundListener){
        if(mOnClickBackgroundListeners!=null)
            mOnClickBackgroundListeners.remove(onClickBackgroundListener);
    }

    /**
     * return screenshot bitmap. 다 쓰고 꼭 recycle 하기.
     * @return Screenshot bitmap.
     */
    public Bitmap getScreenshot(Bitmap.Config config){
        Bitmap screenshot = Bitmap.createBitmap(mViewportWidth, mViewportHeight, config);
        render(new Canvas(screenshot));
        return screenshot;
    }

    @WorkerThread
    Snapshot getSnapshot(){
    }

    class Snapshot{

        private int mWidth, mHeight;
        private Object.Snapshot[] mObjects;//일단 이버전에선 스킵.
        private Widget.Snapshot[] mWidgets;
        int mWidgetCount;
        private int mMaxWidgetCount;
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
        private int mBackgroundR, mBackgroundG, mBackgroundB;
        private Bitmap mBackgroundBitmap = null;
        private boolean mDragToMove;
        private boolean mPinchToZoom;
        private RectF mRectF;
        private int mMaxObjectCount;
        int mObjectCount;
        private ArrayList<OnClickBackgroundListener> mOnClickBackgroundListeners = null;
        private OnCameraGestureListener mOnCameraGestureListener = null;
        private Paint mBitmapPaint;

        private Snapshot(World world){
            //비트맵이랑 외부 리스너들은 외부 라이플사이클에 의존한다.
            mWidth = world.mWidth;
            mHeight = world.mHeight;
            mWidgetCount = world.mWidgetCount;
            mMaxWidgetCount = world.mMaxWidgetCount;
            mWidgets = new Widget.Snapshot[mMaxWidgetCount];
            for(int i=0; i<mWidgetCount; i++){
                mWidgets[i] = world.mWidgets[i].getSnapshot();
            };
            isDragging = world.isDragging;
            startX = world.startX;
            startY = world.startY;
            isPressed = world.isPressed;
            mViewportX = world.mViewportX;
            mViewportY = world.mViewportY;
            mViewportWidth = world.mViewportWidth;
            mViewportHeight = world.mViewportHeight;
            mCameraZ = world.mCameraZ;
            mMinCameraZ = world.mMinCameraZ;
            mMaxCameraZ = world.mMaxCameraZ;
            mFocusedZ = world.mFocusedZ;
            mBackgroundR = world.mBackgroundR;
            mBackgroundG = world.mBackgroundG;
            mBackgroundB = world.mBackgroundB;
            if(mBackgroundBitmap!=null) {
                mBackgroundBitmap = world.mBackgroundBitmap.copy(mBackgroundBitmap.getConfig(), mBackgroundBitmap.isMutable());
            }
            mDragToMove = world.mDragToMove;
            mPinchToZoom = world.mPinchToZoom;
            mRectF = new RectF(world.mRectF);
            mMaxObjectCount = world.mMaxObjectCount;
            mObjectCount = world.mObjectCount;
            mObjects = new Object.Snapshot[mMaxObjectCount];
            for(int i=0; i<mObjectCount; i++){
                mObjects[i] = world.mObjects[i].getSnapshot();
            }
            if(world.mOnClickBackgroundListeners!=null) {
                mOnClickBackgroundListeners = new ArrayList<>();
                mOnClickBackgroundListeners.addAll(world.mOnClickBackgroundListeners);
            }
            mOnCameraGestureListener = world.mOnCameraGestureListener;
            mBitmapPaint = new Paint(world.mBitmapPaint);
        }

        World getInstance(){
            return new World();
        }
    }
}