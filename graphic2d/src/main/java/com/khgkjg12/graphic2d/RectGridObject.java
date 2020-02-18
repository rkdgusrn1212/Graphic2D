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

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;


public class RectGridObject extends RectObject implements GridObject {

    private Object[][] mObjectList;
    private int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;
    private World mWorld;

    public RectGridObject(int width, int height, int row, int column, int z, int x, int y, @Nullable OnClickItemListener onClickItemListener){
        this(Color.TRANSPARENT, width, height, row, column, z, x, y, 0, 0, onClickItemListener);
    }

    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y, @Nullable OnClickItemListener onClickItemListener){
        this(color, width, height, row, column, z, x, y, 0, 0, onClickItemListener);
    }

    public RectGridObject(int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, @Nullable OnClickItemListener onClickItemListener){
        this(Color.TRANSPARENT, width, height, row, column, z, x, y, degreeH, degreeV, onClickItemListener);
    }

    /**
     * @exception ArrayIndexOutOfBoundsException parameter objects's size does not match parameter row x parameter column.
     * @param color background color.
     * @param width total grid width.
     * @param height total grid height.
     * @param row the number of row in grid.
     * @param column the number of column in grid.
     * @param z z coordinate of center of grid.
     * @param x x coordinate of center of grid.
     * @param y y coordinate of center of grid.
     * @param degreeH horizontal degree of grid.
     * @param degreeV vertical degree of grid.
     * @param onClickItemListener Item click event callback.
     */
    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, @Nullable OnClickItemListener onClickItemListener){
        super(color, width, height, z, x, y, degreeH, degreeV, true, true, null);
        mColumn = column;
        mRow = row;
        mObjectList = new Object[mRow][mColumn];
        mOnClickItemListener = onClickItemListener;
    }

    @Override
    public Object getObject(int row, int column){
        return mObjectList[row][column];
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
    public void putObject(@NonNull Object obj, int row, int column){
        mObjectList[row][column] = obj;
        if(mWorld != null) {
            mWorld.putObject(obj);
        }
    }
    @Override
    @WorkerThread
    public void putObjectAndAdjust(@NonNull Object obj, int row, int column){
        obj.mX = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        obj.mY = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        obj.mZ = mZ;
        mObjectList[row][column] = obj;
        if(mWorld!=null) {
            mWorld.putObject(obj);
            mObjectList[row][column].calculateScale(mWorld);
        }
    }

    @Override
    @WorkerThread
    public void createAndPutTextureObject(@NonNull Texture texture, int padding, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object obj = new TextureObject(texture, mWidth/mColumn-padding*2, mHeight/mRow-padding*2, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = obj;
        if (mWorld != null){
            mWorld.putObject(obj);
        }
    }

    @Override
    @WorkerThread
    public void createAndPutRectObject(int color, int padding, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object object = new RectObject(color, mWidth/mColumn-padding*2, mHeight/mRow-padding*2, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = object;
        if(mWorld!=null){
            mWorld.putObject(object);
        }
    }

    @Override
    @WorkerThread
    public void createAndPutRoundRectObject(int color, float rX, float rY, int padding, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object object = new RoundRectObject(color, rX, rY,mWidth/mColumn-padding*2, mHeight/mRow-padding*2, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = object;
        if(mWorld != null){
            mWorld.putObject(object);
        }
    }
    @Override
    @WorkerThread
    public void createAndPutTextureObject(@NonNull Texture texture, int width, int height, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object obj = new TextureObject(texture, width, height, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = obj;
        if(mWorld != null) {
            mWorld.putObject(obj);
        }
    }

    @Override
    @WorkerThread
    public void createAndPutRectObject(int color, int width, int height, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object object = new RectObject(color, width, height, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = object;
        if(mWorld != null) {
            mWorld.putObject(object);
        }
    }

    @Override
    @WorkerThread
    public void createAndPutRoundRectObject(int color, float rX, float rY, int row, int width, int height, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object object = new RoundRectObject(color, rX, rY,width, height, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true, null);
        mObjectList[row][column] = object;
        if(mWorld!=null) {
            mWorld.putObject(object);
        }
    }
    @Override
    @WorkerThread
    public void removeObject(int row, int column){
        if(mWorld!=null) {
            mWorld.removeObject(mObjectList[row][column]);
        }
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
        mWorld = world;
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
        mWorld = null;
    }

    @WorkerThread
    @Override
    boolean onTouch(World world, int x, int y){
        if(mIsInCameraRange&&checkBoundary(x, y)){
            if (mClickable&&mOnClickItemListener != null) {
                int column = (int)((x - mRenderLeft) * mColumn / mRenderWidth);
                int row = (int)((y - mRenderTop) * mRow / mRenderHeight);
                return mOnClickItemListener.onClickItem(world,this, mObjectList[row][column], row, column);
            }
            return mClickable;
        }
        return false;
    }
}
