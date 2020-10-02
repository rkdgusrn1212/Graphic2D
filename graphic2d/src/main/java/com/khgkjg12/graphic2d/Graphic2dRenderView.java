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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.AnyThread;
import android.support.annotation.WorkerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graphic2dRenderView extends SurfaceView implements SurfaceHolder.Callback {
    RenderThread renderThread = null;
    SurfaceHolder mHolder;
    private int mPreViewportWidth, mPreViewportHeight;
    private TouchHandler mInput;
    private Screen mScreen = null;

    private class RenderThread extends Thread{
        private boolean mRunning = true;
        private int mViewportWidth;
        private int mViewportHeight;
        private int mRealWidth;
        private int mRealHeight;
        private World.Snapshot mSnapshot;

        private RenderThread(World.Snapshot snapshot, int viewportWidth, int viewportHeight, int realWidth, int realHeight){
            mViewportWidth = viewportWidth;
            mViewportHeight = viewportHeight;
            mRealHeight = realHeight;
            mRealWidth = realWidth;
            mSnapshot = snapshot;
        }

        @Override
        public void run() {
            World world = mSnapshot.getInstance();
                mInput.setScale((float) mViewportWidth / mRealWidth, (float) mViewportHeight / mRealHeight);
            world.setViewportSize(mViewportWidth, mViewportHeight);
                float canvasScaleX = mRealWidth / mViewportWidth;
                float canvasScaleY = mRealHeight / mViewportHeight;
                if (mScreen != null) {
                    mScreen.onChangeViewportSize(world, mViewportWidth, mViewportHeight);
                }
                long startTime = SystemClock.uptimeMillis();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    while (true) {
                        synchronized (this) {
                            if (!mRunning) {
                                break;
                            }
                            if (mScreen == null) {//있으면 진행하고 없으면 들어올때까지 대기타기
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("Graphic2dRenderView.RenderThread:Interrupted Unexpectedly");
                                }
                                startTime = SystemClock.uptimeMillis();
                            }
                            long currentTime = SystemClock.uptimeMillis();
                            long deltaTime = currentTime - startTime;
                            startTime = currentTime;
                            mScreen.update(world, deltaTime);
                            Canvas canvas = mHolder.lockHardwareCanvas();//일부 하드웨어 shadow 효과 적용안됨. 하려면 bitmap에 그려서 붙여야함(즉 cache 이용)
                            if (canvas != null) {
                                canvas.scale(canvasScaleX, canvasScaleY);
                                world.render(canvas);
                                mHolder.unlockCanvasAndPost(canvas);
                            } else {
                                break;
                            }
                            world.onTouch(mInput);
                        }
                    }
                } else {
                    while (true) {
                        synchronized (this) {
                            if (!mRunning) {
                                break;
                            }
                            if (mScreen == null) {//있으면 진행하고 없으면 들어올때까지 대기타기
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("Graphic2dRenderView.RenderThread:Interrupted Unexpectedly");
                                }
                                startTime = SystemClock.uptimeMillis();
                            }
                            long currentTime = SystemClock.uptimeMillis();
                            long deltaTime = currentTime - startTime;
                            startTime = currentTime;
                            mScreen.update(world, deltaTime);
                            Canvas canvas = mHolder.lockCanvas();
                            if (canvas != null) {
                                canvas.scale(canvasScaleX, canvasScaleY);
                                world.render(canvas);
                                mHolder.unlockCanvasAndPost(canvas);
                            } else {
                                break;
                            }
                            world.onTouch(mInput);
                        }
                    }
                }
            mSnapshot = world.getSnapshot();
        }

        private synchronized boolean onAttachScreen(Screen screen){
            if(mScreen != null){
                return false;
            }
            mScreen = screen;
            mScreen.onAttached(mWorld);
            notify();//만약 wait 상태가 아니었다면 그건 thread가 검사하기전 screen이 attached 된것.
            return true;
        }
        //초기상태 to attached, detached to attachd가 thread의 검사구문 이전에 수행되어 wait가 안걸릴수도있다. 이럴때 아무문제 없다.
        private synchronized boolean onDetachedScreen(){
            if(mScreen == null){
                return false;
            }
            mScreen.onDetached(mWorld);
            mScreen = null;
            return true;
        }
        private World.snapshot finish(){
            synchronized(this){
                mRunning = false;
            }
            try {
                join();//종료되는 쓰래드에서 수행된 명령어들이 join이후의 명령어들에게 happen before 관계.
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("Graphic2dRenderView.RenderThread:Interrupted Unexpectedly");
            }
            return mSnapshot;
        }
    }

    public Graphic2dRenderView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public Graphic2dRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Graphic2dRenderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Graphic2dRenderView, defStyle, 0);

        mPreViewportHeight = a.getInt(
                R.styleable.Graphic2dRenderView_viewportHeight,0);
        mPreViewportWidth = a.getInt(
                R.styleable.Graphic2dRenderView_viewportWidth,
                0);
        int worldWidth = a.getInt(
                R.styleable.Graphic2dRenderView_worldWidth,
                0);
        int worldHeight = a.getInt(
                R.styleable.Graphic2dRenderView_worldHeight,
                0);
        int viewportX = a.getInt(R.styleable.Graphic2dRenderView_viewportX, 0);
        int viewportY = a.getInt(R.styleable.Graphic2dRenderView_viewportY, 0);
        float cameraZ = a.getFloat(R.styleable.Graphic2dRenderView_cameraZ, 100.0f);
        float focusedZ = a.getFloat(R.styleable.Graphic2dRenderView_focusedZ, cameraZ);
        float maxCameraZ = a.getFloat(R.styleable.Graphic2dRenderView_maxCameraZ,cameraZ);
        float minCameraZ = a.getFloat(R.styleable.Graphic2dRenderView_minCameraZ, cameraZ/5);
        int backgroundColor = a.getColor(R.styleable.Graphic2dRenderView_backgroundColor, Color.BLACK);
        boolean dragToMove = a.getBoolean(R.styleable.Graphic2dRenderView_dragToMove, true);
        boolean pinchToZoom = a.getBoolean(R.styleable.Graphic2dRenderView_pinchToZoom, true);
        int maxObjectCount = a.getInt(R.styleable.Graphic2dRenderView_maxObjectCount, 256);
        int maxWidgetCount = a.getInt(R.styleable.Graphic2dRenderView_maxWidgetCount, 128);
        a.recycle();
        if(mPreViewportHeight<=0&&mPreViewportWidth<=0){
            mPreViewportHeight=320;
            mPreViewportWidth=320;
        }
        if(viewportX<-worldWidth/2){
            viewportX = -worldWidth/2;
        }else if(viewportX>worldWidth/2){
            viewportX = worldWidth/2;
        }
        if(viewportY<-worldHeight/2){
            viewportY = -worldHeight/2;
        }else if(viewportY>worldHeight/2){
            viewportY = worldHeight/2;
        }
        mInput = new TouchHandler(Graphic2dRenderView.this);
        mWorld = new World(worldWidth, worldHeight, viewportX, viewportY, cameraZ, minCameraZ, maxCameraZ, focusedZ, backgroundColor, dragToMove, pinchToZoom, maxObjectCount, maxWidgetCount);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(renderThread!=null){
            renderThread.finish();
            renderThread = null;
        }
        int viewportHeight;
        int viewportWidth;
        if(mPreViewportWidth==0){
            viewportHeight = mPreViewportHeight;
            viewportWidth = viewportHeight * width / height;
        } else if(mPreViewportHeight == 0){
            viewportWidth = mPreViewportWidth;
            viewportHeight = viewportWidth * height / width;
        }else{
            viewportWidth = mPreViewportWidth;
            viewportHeight = mPreViewportHeight;
        }
        renderThread = new RenderThread(viewportWidth, viewportHeight, width, height);
        renderThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.finish();
    }

    @AnyThread
    public synchronized boolean attachScreen(Screen screen){
        if(mScreen!=null) {
            return false;
        }
        mScreen.onAttached();
        mScreen = screen;
        notify();
        return true;
    }

    @AnyThread
    public synchronized boolean detachScreen(){
        if(mScreen==null){
            return false;
        }
        if(running){
            pause();
        }
        mScreen = null;
        mScreen.onDetached();
        return true;
    }

    /**
     * this only works after onMeasure() method called
     * do not w:0 h:0, it will be automatically set to 300: 300
     * @param width viewportWidth 0 for match view size, with view's w-h ratio
     * @param height viewportHeight 0 for match view size, with view's w-h ratio
     * */
    public void changeViewPortSize(int width, int height){
        if(width==0&&height==0){
            mPreViewportWidth = 300;
            mPreViewportHeight = 300;
        }else {
            mPreViewportWidth = width;
            mPreViewportHeight = height;
        }
    }

    @WorkerThread
    public void setPinchToZoom(boolean pinchToZoom){
        mWorld.setPinchToZoom(pinchToZoom);
    }

    @WorkerThread
    public void setDragToMove(boolean dragToMove){
        mWorld.setDragToMove(dragToMove);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
