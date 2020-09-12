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

import android.support.annotation.AnyThread;
import android.support.annotation.WorkerThread;

public abstract class DeterminateAnimation {

    private Screen mScreen;
    private boolean mIsStarted = false;
    private boolean mIsCanceled = false;

    @WorkerThread
    public DeterminateAnimation(Screen screen){
        mScreen = screen;
    }

    @AnyThread
    synchronized public boolean start(final long duration){
        if(mIsStarted){
            return false;
        }
        mIsStarted = true;
        mScreen.queueWidgetUpdate(new Renderable() {
            @Override
            void render(World world, long deltaTime) {
                onPreAnimation(world, duration);
                mScreen.queueWidgetUpdate(new Renderable() {
                    long mTimer = 0;
                    @Override
                    void render(World world, long deltaTime) {
                        boolean isCanceled;
                        synchronized(DeterminateAnimation.this){
                            isCanceled = mIsCanceled;
                        }
                        if(isCanceled){
                            onCancelAnimation(world, mTimer);
                            return;
                        }
                        mTimer+=deltaTime;
                        if(mTimer<duration){
                            onProgressAnimation(world, mTimer, duration);
                            mScreen.queueWidgetUpdate(this);
                        }else{
                            onPostAnimation(world, duration);
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

    protected abstract void onPreAnimation(World world, long duration);

    protected abstract void onProgressAnimation(World world, long elapsedTime, long duration);

    protected abstract void onPostAnimation(World world, long duration);

    protected abstract void onCancelAnimation(World world, long elapsedTime);
}
