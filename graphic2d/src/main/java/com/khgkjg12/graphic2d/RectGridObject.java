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
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RectGridObject extends RectObject implements GridObject {

    private Object[][] mObjectList;
    private int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;


    public RectGridObject(int width, int height, int row, int column, int z, int x, int y){
        super(Color.TRANSPARENT, width, height, z, x, y, 0, 0, false, true);
        init(row, column);
    }

    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y){
        super(color, width, height, z, x, y, 0, 0, true, true);
        init(row, column);
    }

    public RectGridObject(int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV){
        super(Color.TRANSPARENT, width, height, z, x, y, degreeH, degreeV, false, true);
        init(row, column);
    }

    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV){
        super(color, width, height, z, x, y, degreeH, degreeV, true, true);
        init(row, column);
    }

    private void init(int row, int column){
        mColumn = column;
        mRow = row;
        mObjectList = new Object[row][column];
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
    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        mOnClickItemListener = onClickItemListener;
    }

    @Override
    public void putObject(World world, Object obj, int row, int column){
        obj.setPosition(mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1) ,mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1));
        mObjectList[row][column] = obj;
        world.putObject(obj);
    }

    @Override
    public void putObject(World world, Texture texture, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object obj = new TextureObject(texture, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true);
        mObjectList[row][column] = obj;
        world.putObject(obj);
    }

    @Override
    public void putObject(World world, int color, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        Object obj = new RectObject(color, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHorizontalDegree, mVerticalDegree, true, true);
        mObjectList[row][column] = obj;
        world.putObject(obj);
    }
    @Override
    public void removeObject(World world, int row, int column){
        world.removeObject(mObjectList[row][column]);
        mObjectList[row][column] = null;
    }

    @Override
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
    public void detached(World world) {
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                if(mObjectList[i][j]!=null){
                    world.removeObject(mObjectList[i][j]);
                }
            }
        }
    }

    @WorkerThread
    @Override
    boolean onTouch(World world, int x, int y){
        if(isInCameraRange&&checkBoundary(x, y)){
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
