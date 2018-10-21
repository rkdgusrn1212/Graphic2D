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

        mViewportHeight = a.getInt(
                R.styleable.Graphic2dRenderView_viewportHeight,0);
        mViewportWidth = a.getInt(
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
        int cameraZ = a.getInt(R.styleable.Graphic2dRenderView_cameraZ, 100);
        int maxCameraZ = a.getInt(R.styleable.Graphic2dRenderView_maxCameraZ,cameraZ);
        int minCameraZ = a.getInt(R.styleable.Graphic2dRenderView_minCameraZ, cameraZ/5);
                a.recycle();
        if(mViewportHeight==0&&mViewportWidth==0){
            mViewportHeight=320;
            mViewportWidth=320;
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
        mWorld = new World(worldWidth, worldHeight, viewportX, viewportY, cameraZ, minCameraZ, maxCameraZ);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode != MeasureSpec.AT_MOST && widthMode != MeasureSpec.AT_MOST){
            if(mViewportHeight==0){
                mViewportHeight = mViewportWidth*height/width;
            }else if(mViewportWidth==0){
                mViewportWidth = mViewportHeight*width/height;
            }
        }
        if(mViewportWidth==0){
            mViewportWidth = mViewportHeight;
        }else if(mViewportHeight == 0){
            mViewportHeight = mViewportWidth;
        }
        if (heightMode == MeasureSpec.AT_MOST && widthMode != MeasureSpec.AT_MOST) {
            height = width * mViewportHeight / mViewportWidth;
        } else if (heightMode != MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            width = height * mViewportWidth / mViewportHeight;
        } else if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            height = mViewportHeight;
            width = mViewportWidth;
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

            Canvas canvas = holder.lockCanvas();
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
