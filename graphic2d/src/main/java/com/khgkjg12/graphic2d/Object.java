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

import android.support.annotation.Nullable;

public abstract class Object {

    private boolean mVisibility;
    boolean mClickable;
    String mId;
    private OnClickListener mOnClickListener;
    float mZ;
    int mX, mY;

    public Object(float z, int x, int y, boolean visibility, boolean clickable, @Nullable String id){
        mVisibility = visibility;
        mClickable = clickable;
        mId = id;
        mZ = z;
        mX = x;
        mY = y;
    }

    //객체의 좌표를 설정.
    public void setPosition(int x, int y){
        mX = x;
        mY = y;
    }

    //객체를 물리적 상태를 변화.
    public void setVisibility(boolean visible){
        mVisibility = visible;
    }

    public void setClickable(boolean clickable){
        mClickable = clickable;
    }

    boolean onTouch(int x, int y){
        if(mClickable&&checkBoundary(x, y)){
            if(mOnClickListener!=null) {
                mOnClickListener.onClick(this);
            }
            return true;
        }
        return false;
    }

    /**
     * 경계선 채크 메소드.
     * @param x
     * @param y
     * @return x, y 가 경계선 안에 있으면 참.
     */
    abstract boolean checkBoundary(int x, int y);

    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    void render(Graphic2dDrawer drawer, int viewportWidth, int viewportHeight, float cameraZ, float focusedZ, int viewportX, int viewportY){
        if(mVisibility&&mZ<cameraZ) {
            float scale = focusedZ / (cameraZ - mZ);
            float renderX = (viewportWidth / 2f) - (viewportX - mX) * scale;
            float renderY = (viewportHeight / 2f) - (viewportY - mY) * scale;
            render(drawer, scale, renderX, renderY);
        }
    }

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     * @param scale 너비 및 높이 또는 반지름에 곱해야함.
     * @param renderX 렌더프레임상 오브젝트 중심 x.
     * @param renderY 렌더프레임상 오브젝트 중심 y.
     */
    abstract void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY);

    public interface OnClickListener{
        public void onClick(Object object);
    }

    public void setZ(float z){
        mZ = z;
    }

    public float getZ(){
        return mZ;
    }
}