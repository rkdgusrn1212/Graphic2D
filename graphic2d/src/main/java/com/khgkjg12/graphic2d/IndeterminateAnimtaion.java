package com.khgkjg12.graphic2d;

import android.support.annotation.AnyThread;
import android.support.annotation.WorkerThread;

public abstract class IndeterminateAnimation {

    private Screen mScreen;
    private boolean mIsStarted = false;
    private boolean mIsCanceled = false;

    @WorkerThread
    public IndeterminateAnimation(Screen screen){
        mScreen = screen;
    }

    @AnyThread
    synchronized public boolean start(){
        if(mIsStarted){
            return false;
        }
        mIsStarted = true;
        mScreen.queueWidgetUpdate(new Renderable() {
            @Override
            void render(World world, long deltaTime) {
                onPreAnimation(world);
                mScreen.queueWidgetUpdate(new Renderable() {
                    long mTimer = 0;
                    @Override
                    void render(World world, long deltaTime) {
                        boolean isCanceled;
                        synchronized(IndeterminateAnimation.this){
                            isCanceled = mIsCanceled;
                        }
                        if(isCanceled){
                            onCancelAnimation(world, mTimer);
                            return;
                        }
                        mTimer+=deltaTime;
                        if(onProgressAnimation(world, mTimer)){
                            mScreen.queueWidgetUpdate(this);
                        }else{
                            onPostAnimation(world, mTimer);
                        }
                    }
                });
            }
        });
        return true;
    }
    @AnyThread
    synchronized public boolean cancel(){
        if(mIsStarted){
            return false;
        }
        mIsCanceled = true;
        return true;
    }

    protected abstract void onPreAnimation(World world);

    protected abstract boolean onProgressAnimation(World world, long elapsedTime);

    protected abstract void onPostAnimation(World world, long elapsedTime);

    protected abstract void onCancelAnimation(World world, long elapsedTime);
}
