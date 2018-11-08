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

import java.util.ArrayList;
import java.util.List;

public class GridObject extends Object {

    Object[][] mObjectList;
    int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;

    public GridObject(Texture texture, int width, int height, int row, int column, String id){
        super(texture, width, height, id);
        init(row, column);
    }
    public GridObject(int color, int width, int height, int row, int column, String id){
        super(color, width, height, id);
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

    boolean onTouch(int x, int y){
        int column = (x-mBoundary.left)*mColumn/mBoundary.width();
        int row = (y-mBoundary.top)*mRow/mBoundary.height();
        if(column>=0&&row>=0&&column<mColumn&&row<mRow){
            if(mOnClickItemListener!=null) {
                mOnClickItemListener.onClickItem(this, mObjectList, row, column);
            }
            return true;
        }else{
            return false;
        }
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        mOnClickItemListener = onClickItemListener;
    }

    public void putObject(Object obj, int row, int column){
        obj.setPosition((int)(mBoundary.left+mBoundary.width()*(column+0.5)/mColumn),(int)(mBoundary.top+mBoundary.height()*(row+0.5)/mRow));
        mObjectList[row][column] = obj;
    }

    public void putObject(Texture texture, int row, int column){
        putObject(new Object(texture, mBoundary.width()/mColumn, mBoundary.height()/mRow, null), row, column);
    }
    public void putObject(int color, int row, int column){
        putObject(new Object(color, mBoundary.width()/mColumn, mBoundary.height()/mRow, null), row, column);
    }

    public void removeObject(int row, int column){
        mObjectList[row][column] = null;
    }

    List<Object> getObjects(){
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

    public interface OnClickItemListener{
        public void onClickItem(GridObject gridObject, Object[][] objectList, int row, int column);
    }
}
