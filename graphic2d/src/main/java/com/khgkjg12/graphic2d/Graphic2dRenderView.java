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
    private int mBufferWidth, mBufferHeight;
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
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Graphic2dRenderView, defStyle, 0);

        mBufferHeight = a.getInt(
                R.styleable.Graphic2dRenderView_bufferHeight,0);
        mBufferWidth = a.getInt(
                R.styleable.Graphic2dRenderView_bufferWidth,
                0);
        a.recycle();
        if(mBufferHeight==0&&mBufferWidth==0){
            mBufferHeight=320;
            mBufferWidth=320;
        }
        this.holder = getHolder();
        mDrawer = new Graphic2dDrawer(context.getApplicationContext().getAssets());
        mInput = new TouchHandler(Graphic2dRenderView.this);
        mWorld = new World(mBufferWidth, mBufferHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode != MeasureSpec.AT_MOST && widthMode != MeasureSpec.AT_MOST){
            if(mBufferHeight==0){
                mBufferHeight = mBufferWidth*height/width;
            }else if(mBufferWidth==0){
                mBufferWidth = mBufferHeight*width/height;
            }
        }
        if(mBufferWidth==0){
            mBufferWidth = mBufferHeight;
        }else if(mBufferHeight == 0){
            mBufferHeight = mBufferWidth;
        }
        if (heightMode == MeasureSpec.AT_MOST && widthMode != MeasureSpec.AT_MOST) {
            height = width * mBufferHeight / mBufferWidth;
        } else if (heightMode != MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            width = height * mBufferWidth / mBufferHeight;
        } else if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {
            height = mBufferHeight;
            width = mBufferWidth;
        }
        setMeasuredDimension(width,height);
        mInput.setScale((float)mBufferWidth/width, (float)mBufferHeight/height);
        mDrawer.setFrameBuffer(mBufferWidth, mBufferHeight, Bitmap.Config.RGB_565);
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
            mWorld.render();

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

    public int getBufferWidth(){
        return mBufferWidth;
    }

    public int getBufferHeight(){
        return mBufferHeight;
    }

    public interface Renderer{
        public void updateWorld(float deltaTime, World world);
        public void loadTextures(Graphic2dDrawer drawer);//사용 오브젝트들의 리소스들을 다 로드함.
    }
}
