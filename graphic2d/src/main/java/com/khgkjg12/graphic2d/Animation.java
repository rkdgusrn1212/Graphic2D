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

import android.support.annotation.FloatRange;
import android.support.annotation.WorkerThread;

public abstract class Animation {

    private float mTimer;
    private float mDuration;
    private boolean mProgress;
    private boolean mActivated;

    public Animation(float duration){
        mDuration=duration;
        mProgress = false;
        mActivated = false;
    }

    /**
     * updateWorld를 통해 호출.
     * @param world
     * @param deltaTime 음수는 되감기(progress 상태에서만 가능하며 계속 progress 상태에 머뭄). 양수는 진행.
     */
    public void animate(World world, float deltaTime) {
        if (mActivated) {
            mTimer += deltaTime;
            if (mTimer >= 0) {
                if (mProgress) {
                    if (mTimer < mDuration) {
                        onProgressAnimate(world, mTimer / mDuration);
                    } else {
                        mProgress = false;
                        onPostAnimate(world);
                        mActivated = false;
                    }
                } else {
                    onPreAnimate(world);
                    mProgress = true;
                }
            }
        }
    }

    /**
     * 활성화된 에니메이션 강제종료.
     * @param world
     * @return 이미 비활성화된 에니메이션이면 false;
     */
    @WorkerThread
    public boolean forceFinish(World world){
        if(mActivated) {
            if(!mProgress){
                onPreAnimate(world);
            }
            mProgress = false;
            onPostAnimate(world);
            mActivated = false;
            return true;
        }else{
            return false;
        }
    }

    @WorkerThread
    public boolean isActivated(){
        return mActivated;
    }

    @WorkerThread
    public boolean isProgress(){
        return mProgress;
    }

    /**
     * 에니메이션 wait 초 뒤에 시작.
     * @param wait 대기시간(초)
     * @return 이미 활성화된 에니메이션이면 false;
     */
    @WorkerThread
    public boolean start(@FloatRange(from = 0) float wait){
        if(!mActivated) {
            mTimer = -wait;
            mActivated = true;
            return true;
        }else{
            return false;
        }
    }

    /**
     * 에니메이션의 수행시간을 변경. 이미 수행중인 에니메이션도 가능.
     * @param duration
     */
    @WorkerThread
    public void setDuration(@FloatRange(from = 0, fromInclusive = false)float duration){
        mDuration = duration;
    }

    @WorkerThread
    public float getDuration(){
        return mDuration;
    }

    protected abstract void onPreAnimate(World world);

    protected abstract void onProgressAnimate(World world, float progress);

    protected abstract void onPostAnimate(World world);
}
