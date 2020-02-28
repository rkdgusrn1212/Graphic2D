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
import android.support.annotation.WorkerThread;

public class GridObject extends Object implements Group {
    private Object[][] mObjectList;
    private int mRow, mColumn;
    private OnClickGridListener mOnClickGridListener;
    private World mWorld;
    private int mWidth;
    private int mHeight;
    private float mRenderWidth;
    private float mRenderHeight;
    private float mRenderLeft;
    private float mRenderRight;
    private float mRenderTop;
    private float mRenderBottom;
    private boolean mItemClickable;
    private InnerItemListener mInnerItemListener;

    /**
     * @param z z-coordinate.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param clickable 그리드 onClickGrid{@link OnClickGridListener} 호출 여부.
     * @param itemClickable 그리드 onClickItem 호출여부.
     * @param width  x-axis length.
     * @param height y-axis length.
     * @param row number of rows.
     * @param column number of columns.
     * @param onClickGridListener touch event callback {@link OnClickGridListener}
     */
    public GridObject(float z, int x, int y, boolean clickable, boolean itemClickable, int width, int height, int row, int column, @Nullable OnClickGridListener onClickGridListener){
        super(z, x, y, false, clickable, null);
        mWidth = width;
        mHeight = height;
        mRow = row;
        mColumn = column;
        mOnClickGridListener = onClickGridListener;
        mObjectList = new Object[mRow][mColumn];
        mItemClickable = itemClickable;
        mInnerItemListener = new InnerItemListener();
    }

    @WorkerThread
    public void setItemClickable(boolean itemClickable) {
        mItemClickable = itemClickable;
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
    public void setOnClickItemListener(OnClickGridListener onClickGridListener){
        mOnClickGridListener = onClickGridListener;
    }

    @WorkerThread
    public void putObject(Object obj, int row, int column){
        if(mObjectList[row][column]!=null) {
            if(mWorld!=null){
                mWorld.removeObject(mObjectList[row][column]);
            }
        }
        if(obj!=null) {
            obj.setOnClickListener(mInnerItemListener);
            if (mWorld != null) {
                mWorld.putObject(obj);
            }
        }
        mObjectList[row][column] = obj;
    }

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
    boolean checkBoundary(int x, int y) {
        return x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }

    @Override
    void onTouchUp(World world, int x, int y) {
        super.onTouchUp(world, x, y);
        if(mOnClickGridListener !=null){
            int column = (int)((x - mRenderLeft) * mColumn / mRenderWidth);
            int row = (int)((y - mRenderTop) * mRow / mRenderHeight);
            mOnClickGridListener.onClickGrid(mWorld, this, mObjectList[row][column], row, column);
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
        for(int i=0; i<mRow; i++){
            for(int j=0; j< mColumn; j++){
                if(mObjectList[i][j]!=null) {
                    mObjectList[i][j].mX += deltaX;
                    mObjectList[i][j].mY += deltaY;
                    mObjectList[i][j].calculateRenderXY(world);
                }
            }
        }
        super.moveXY(world, x, y);
    }

    @Override
    public void moveZ(World world, float z) {
        float deltaZ = z-mZ;
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                if(mObjectList[i][j]!=null){
                    mObjectList[i][j].mZ+=deltaZ;
                    mObjectList[i][j].calculateScale(world);
                }
            }
        }
        super.moveZ(world, z);
    }

    @Override
    void draw(Graphic2dDrawer drawer) { }

    class InnerItemListener implements OnClickListener{

        @WorkerThread
        @Override
        public void onClick(World world, Object object) {
            if(mItemClickable){
                if(mOnClickGridListener!=null){
                    for (int i = 0; i < mRow; i++) {
                        for (int j = 0; j < mColumn; j++) {
                            if (mObjectList[i][j] == object) {
                                mOnClickGridListener.onClickItem(world, GridObject.this, object, i,j);
                                break;
                            }
                        }
                    }
                }
                if(mOnClickListener!=null){
                    mOnClickListener.onClick(world, GridObject.this);
                }
            }
        }
    }

    public interface OnClickGridListener {
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