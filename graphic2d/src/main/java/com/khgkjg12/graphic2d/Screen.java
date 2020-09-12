package com.khgkjg12.graphic2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.SoundPool;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Screen {

    private final LinkedBlockingQueue<Renderable> mWidgetUpdateQueue = new LinkedBlockingQueue<>();
    private HashMap<String, Bitmap> mBitmapResources;
    private HashMap<String, Integer> mSoundResouces;


    public Screen(Context context, String[] bitmapNameArray, String[] soundNameArray){
        Graphic2dDrawer drawer = new Graphic2dDrawer(context.getApplicationContext().getAssets());
        mBitmapResources = new HashMap<>();
        for(String fileName:bitmapNameArray){
            mBitmapResources.put(fileName, drawer.loadBitmap(fileName, Bitmap.Config.ARGB_8888));
        }
        mSoundResouces = new HashMap<>();
        for(String fileName:soundNameArray){
            mSoundResouces.put(fileName, );
        }
    }

    final void update(World world, long deltaTime) {
        LinkedList<Renderable> renderQueue = new LinkedList<>();
        Renderable renderable;
        while((renderable = mWidgetUpdateQueue.poll())!=null){
            renderQueue.addLast(renderable);
        }
        while (!renderQueue.isEmpty()&&(renderable = renderQueue.removeFirst()) != null) {
            renderable.render(world, deltaTime);
        }
    }

    synchronized public final Bitmap getBitmap(String fileName){
        if(mBitmapResources==null){
            return null;
        }
        return mBitmapResources.get(fileName);
    }

    synchronized public final boolean dispose(Context context){
        if(mBitmapResources==null){
            return false;
        }
        for(Bitmap bitmap : mBitmapResources.values()){
            bitmap.recycle();
        }
        mBitmapResources = null;
        return true;
    }

    @MainThread
    public final void loadSounds(Context context, ){

    }

    @WorkerThread
    abstract void onAttached(World world, int viewportWidth, int viewportHeight);
    @WorkerThread
    abstract void updateWorld(long deltaTime, World world);
    @WorkerThread
    abstract void resumeWorld(World world, int viewportWidth, int viewportHeight);


    public final void queueWidgetUpdate(Renderable renderable) {
        if(!mWidgetUpdateQueue.offer(renderable)){
            throw new RuntimeException("Screen:queueWidgetUpdate:Queue Full");
        }
    }
}
