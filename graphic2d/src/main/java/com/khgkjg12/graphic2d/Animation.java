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

public abstract class Animation {

    Object object;
    private float mTimer=0;
    private float mDuration;

    public Animation(Object object, float duration, float delay){
        mDuration=duration;
        mTimer = -delay;
        this.object = object;
    }

    //끝남 여부를 반환.
    public boolean animate(float deltaTime){
        mTimer+=deltaTime;
        if(mTimer<0){
            return false;
        }
        if(mTimer<mDuration){
            onProgressAnimate(object,mTimer/mDuration);
            return false;
        }else{
            onPostAnimate(object);
            return true;
        }
    }

    protected abstract void onProgressAnimate(Object object, float progress);

    protected abstract void onPostAnimate(Object object);
}
