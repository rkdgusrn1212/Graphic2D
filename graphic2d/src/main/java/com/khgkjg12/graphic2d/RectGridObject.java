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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RectGridObject extends RectObject implements GridObject {

    Object[][] mObjectList;
    int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;


    public RectGridObject(int width, int height, int row, int column, int z, int x, int y, String id){
        super(Color.TRANSPARENT, width, height, z, x, y, 0, 0, false, true, id);
        init(row, column);
    }

    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y, String id){
        super(color, width, height, z, x, y, 0, 0, true, true, id);
        init(row, column);
    }

    public RectGridObject(int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, String id){
        super(Color.TRANSPARENT, width, height, z, x, y, degreeH, degreeV, false, true, id);
        init(row, column);
    }

    public RectGridObject(int color, int width, int height, int row, int column, int z, int x, int y, int degreeH, int degreeV, String id){
        super(color, width, height, z, x, y, degreeH, degreeV, true, true, id);
        init(row, column);
    }

    private void init(int row, int column){
        mColumn = column;
        mRow = row;
        mObjectList = new Object[row][column];
    }

    public Object getObject(int row, int column){
        return mObjectList[row][column];
    }

    public Object[][] getObjectArray(){
        return mObjectList;
    }

    public int getRowSize(){
        return mRow;
    }
    public int getColumnSize(){
        return mColumn;
    }

    @Override
    boolean checkBoundary(int x, int y) {
        if(super.checkBoundary(x, y)){
            if (mOnClickItemListener != null) {
                int column = (x - mX - mWidth/2) * mColumn / mWidth;
                int row = (y - mY - mHeight/2) * mRow / mHeight;
                mOnClickItemListener.onClickItem(this, mObjectList, row, column);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        mOnClickItemListener = onClickItemListener;
    }

    @Override
    public void putObject(Object obj, int row, int column){
        obj.setPosition(mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1) ,mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1));
        obj.setHorizontalFlip(mHoriaontalDegree);
        obj.setVerticalFlip(mVerticalDegree);
        mObjectList[row][column] = obj;
    }

    @Override
    public void putObject(Texture texture, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        putObject(new TextureObject(texture, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHoriaontalDegree, mVerticalDegree, true, true, null), row, column);
    }

    @Override
    public void putObject(int color, int row, int column){
        int x = mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
        int y = mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
        putObject(new RectObject(color, mWidth/mColumn, mHeight/mRow, mZ, x, y, mHoriaontalDegree, mVerticalDegree, true, true, null), row, column);
    }

    @Override
    public void removeObject(int row, int column){
        mObjectList[row][column] = null;
    }

    @Override
    public List<Object> getObjects(){
        List<Object> objects = new ArrayList<Object>();
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                Object obj = mObjectList[i][j];
                if(obj!=null){
                    objects.add(obj);
                }
            }
        }
        return objects;
    }
}
