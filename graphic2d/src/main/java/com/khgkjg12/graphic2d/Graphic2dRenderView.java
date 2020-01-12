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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graphic2dRenderView extends SurfaceView implements Runnable {
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;
    private Renderer mRenderer;
    private Graphic2dDrawer mDrawer;
    private TouchHandler mInput = null;
    private int mViewportWidth, mViewportHeight;
    private int mPreViewportWidth, mPreViewportHeight;
    private World mWorld;

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

    public void setRenderer(Renderer renderer){
        mRenderer = renderer;
        mRenderer.loadTextures(mDrawer);
        mRenderer.prepareWorld(mWorld);
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
        a.recycle();
        if(mPreViewportHeight==0&&mPreViewportWidth==0){
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
        this.holder = getHolder();
        mDrawer = new Graphic2dDrawer(context.getApplicationContext().getAssets());
        mInput = new TouchHandler(Graphic2dRenderView.this);
        mWorld = new World(worldWidth, worldHeight, viewportX, viewportY, cameraZ, minCameraZ, maxCameraZ, focusedZ, backgroundColor, dragToMove, pinchToZoom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST && widthMode != MeasureSpec.AT_MOST) {
            if(mPreViewportWidth==0){
                mViewportWidth = mPreViewportHeight;
                mViewportHeight = mPreViewportHeight;
            }else if(mPreViewportHeight == 0){
                mViewportWidth = mPreViewportWidth;
                mViewportHeight = mPreViewportWidth;
            }
            height = width * mViewportHeight / mViewportWidth;
        } else if (heightMode != MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            if(mPreViewportWidth==0){
                mViewportWidth = mPreViewportHeight;
                mViewportHeight = mPreViewportHeight;
            }else if(mPreViewportHeight == 0){
                mViewportWidth = mPreViewportWidth;
                mViewportHeight = mPreViewportWidth;
            }
            width = height * mViewportWidth / mViewportHeight;
        } else if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            if(mPreViewportWidth==0){
                mViewportWidth = mPreViewportHeight;
                mViewportHeight = mPreViewportHeight;
            }else if(mPreViewportHeight == 0){
                mViewportWidth = mPreViewportWidth;
                mViewportHeight = mPreViewportWidth;
            }
            height = mViewportHeight;
            width = mViewportWidth;
        }else{
            if(mPreViewportHeight==0){
                mViewportHeight = mPreViewportWidth*height/width;
                mViewportWidth = mPreViewportWidth;
            }else if(mPreViewportWidth==0){
                mViewportWidth = mPreViewportHeight*width/height;
                mViewportHeight= mPreViewportHeight;
            }else{
                mViewportWidth = mPreViewportWidth;
                mViewportHeight = mPreViewportHeight;
            }
        }
        setMeasuredDimension(width,height);
        mInput.setScale((float)mViewportWidth/width, (float)mViewportHeight/height);
        mDrawer.setFrameBuffer(mViewportWidth, mViewportHeight, Bitmap.Config.RGB_565);
        mWorld.setViewportSize(mViewportWidth, mViewportHeight);
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while(running) {
            if(!holder.getSurface().isValid())
                continue;

            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            mWorld.onTouch(mInput);
            mRenderer.updateWorld(deltaTime, mWorld);
            mWorld.render(mDrawer);


            Canvas canvas = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                canvas = holder.lockHardwareCanvas();
            }else{
                canvas = holder.lockCanvas();
            }
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(mDrawer.getFrameBuffer(), null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }

    public interface Renderer{
        public void prepareWorld(World world);
        public void updateWorld(float deltaTime, World world);
        public void loadTextures(Graphic2dDrawer drawer);//사용 오브젝트들의 리소스들을 다 로드함.
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

    public void setPinchToZoom(boolean pinchToZoom){
        mWorld.setPinchToZoom(pinchToZoom);
    }

    public void setDragToMove(boolean dragToMove){
        mWorld.setDragToMove(dragToMove);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
