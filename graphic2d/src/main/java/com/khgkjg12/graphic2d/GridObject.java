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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public class GridObject extends Object implements Object.OnClickListener {
    private Object[][] mObjectList;
    private int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;
    private World mWorld;
    private int mWidth;
    private int mHeight;
    private float mRenderWidth;
    private float mRenderHeight;
    private float mRenderLeft;
    private float mRenderRight;
    private float mRenderTop;
    private float mRenderBottom;

    /**
     * @param z z-coordinate.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param clickable 오직 그리드 평면에 대한 터치 이벤트. 즉, onClickGrid{@link OnClickItemListener} 호출 여부.
     * @param width  x-axis length.
     * @param height y-axis length.
     * @param row number of rows.
     * @param column number of columns.
     * @param onClickItemListener touch event callback {@link OnClickItemListener}
     */
    public GridObject(float z, int x, int y, boolean clickable, int width, int height, int row, int column, @Nullable OnClickItemListener onClickItemListener){
        super(z, x, y, false, clickable, null);
        mWidth = width;
        mHeight = height;
        mRow = row;
        mColumn = column;
        mOnClickItemListener = onClickItemListener;
        mObjectList = new Object[mRow][mColumn];
    }
    public Object getObject(int row, int column){
        return mObjectList[row][column];
    }

    public int getRowSize(){
        return mRow;
    }

    public int getColumnSize(){
        return mColumn;
    }

    @WorkerThread
    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        mOnClickItemListener = onClickItemListener;
    }

    @WorkerThread
    public void putObject(@NonNull Object obj, int row, int column){
        obj.setOnClickListener(this);
        mObjectList[row][column] = obj;
        if(mWorld != null) {
            mWorld.putObject(obj);
        }
    }

    @WorkerThread
    public void removeObject(int row, int column){
        if(mWorld!=null) {
            mWorld.removeObject(mObjectList[row][column]);
        }
        mObjectList[row][column] = null;
    }

    @WorkerThread
    void attached(World world) {
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                if(mObjectList[i][j]!=null){
                    world.putObject(mObjectList[i][j]);
                }
            }
        }
        mWorld = world;
    }

    @WorkerThread
    void detached(World world) {
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
    boolean checkBoundary(int x, int y) {
        if(mOnClickItemListener!=null&&x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop){
            int column = (int)((x - mRenderLeft) * mColumn / mRenderWidth);
            int row = (int)((y - mRenderTop) * mRow / mRenderHeight);
            return mOnClickItemListener.onClickGrid(mWorld, this, mObjectList[row][column], row, column);
        }else{
            return false;
        }
    }

    @Override
    void calculateBoundary() {
        mRenderWidth = mWidth * mScale;
        mRenderHeight = mHeight *mScale;
        mRenderLeft =  mRenderX - mRenderWidth/2;
        mRenderTop = mRenderY - mRenderHeight/2;
        mRenderRight = mRenderLeft + mRenderWidth;
        mRenderBottom = mRenderTop + mRenderHeight;
    }


    @WorkerThread
    public void moveXY(World world, int x, int y){
        int deltaX = x-mX;
        int deltaY = y-mY;
        mX = x;
        mY = y;
        for(int i=0; i<mRow; i++){
            for(int j=0; j< mColumn; j++){
                mObjectList[i][j].mX+=deltaX;
                mObjectList[i][j].mY+=deltaY;
                mObjectList[i][j].calculateRenderXY(world);
            }
        }
        calculateBoundary();
    }

    @Override
    void draw(Graphic2dDrawer drawer) { }

    @Override
    public boolean onClick(World world, Object object) {
        if(mOnClickItemListener!=null) {
            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mColumn; j++) {
                    if (mObjectList[i][j] == object) {
                        return mOnClickItemListener.onClickItem(world, this, object, i, j);
                    }
                }
            }
        }
        return false;
    }

    public interface OnClickItemListener{
        /**
         * 해당 그리드의 셀에 들어있는 오브젝트를 콜벡함수를 통해 반환.
         * @param gridObject
         * @param object
         * @param row
         * @param column
         * @return 터치이벤트의 소멸 여부. true 는 소멸, false 는 전달.
         */
        @WorkerThread
        boolean onClickItem(World world, GridObject gridObject, Object object, int row, int column);

        @WorkerThread
        boolean onClickGrid(World world, GridObject gridObject, @Nullable Object object, int row, int column);
    }

    /**
     * get Y coordinate of given row.
     * @param row
     * @return y-coordinate of given row.
     */
    public int getRowY(int row){
        return mY-(mHeight>>1)+mHeight*row/mRow+((mHeight/mRow)>>1);
    }

    /**
     * get X coordinate of given column.
     * @param column
     * @return y-coordinate of given row.
     */
    public int getColumnX(int column){
        return mX-(mWidth>>1)+mWidth*column/mColumn+((mWidth/mColumn)>>1);
    }
}