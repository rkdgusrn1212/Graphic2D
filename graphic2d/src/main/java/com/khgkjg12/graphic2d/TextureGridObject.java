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

import android.support.annotation.WorkerThread;

public class TextureGridObject extends TextureObject implements GridObject {

    private Object[][] mObjectList;
    private int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;

    public TextureGridObject(Texture texture, int width, int height, int row, int column, int z, int x, int y, OnClickItemListener onClickItemListener){
        this(texture, width, height, row, column, z, x, y, 0, 0, onClickItemListener);
    }

    public TextureGridObject(Texture texture, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, OnClickItemListener onClickItemListener){
        super(texture, width, height, z, x, y, degreeH, degreeV, true, true,null);
        mColumn = column;
        mRow = row;
        mObjectList = new Object[row][column];
        mOnClickItemListener = onClickItemListener;
    }

    @Override
    public int getRowSize(){
        return mRow;
    }
    @Override
    public int getColumnSize(){
        return mColumn;
    }

    @Override
    @WorkerThread
    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        mOnClickItemListener = onClickItemListener;
    }
    @Override
    @WorkerThread
    public void putObject(World world, Object obj, int row, int column){
        obj.moveXY(world,mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1) ,mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1));
        mObjectList[row][column] = obj;
        world.putObject(obj);
    }

    @Override
    @WorkerThread
    public void putObject(World world, Texture texture, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object obj = new TextureObject(texture, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = obj;
        world.putObject(obj);
    }

    @Override
    @WorkerThread
    public void putObject(World world, int color, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object object = new RectObject(color, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = object;
        world.putObject(object);
    }
    @Override
    @WorkerThread
    public void removeObject(World world, int row, int column){
        world.removeObject(mObjectList[row][column]);
        mObjectList[row][column] = null;
    }

    @Override
    @WorkerThread
    public void attached(World world) {
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                if(mObjectList[i][j]!=null){
                    world.putObject(mObjectList[i][j]);
                }
            }
        }
    }

    @Override
    @WorkerThread
    public void detached(World world) {
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                if(mObjectList[i][j]!=null){
                    world.removeObject(mObjectList[i][j]);
                }
            }
        }
    }

    @Override
    public Object getObject(int row, int column){
        return mObjectList[row][column];
    }

    @WorkerThread
    @Override
    boolean onTouch(World world, int x, int y){
        if(mIsInCameraRange&&checkBoundary(x, y)){
            if (mClickable&&mOnClickItemListener != null) {
                int column = (int)((x - mRectF.left) * mColumn / mRenderWidth);
                int row = (int)((y - mRectF.top) * mRow / mRenderHeight);
                return mOnClickItemListener.onClickItem(world, this, mObjectList[row][column], row, column);
            }
            return mClickable;
        }
        return false;
    }

}
