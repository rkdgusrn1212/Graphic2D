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

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;

public class GridObject extends GroupObject {
    protected int mRow, mColumn;
    protected ArrayList<OnClickGridListener> mOnClickGridListeners = null;
    protected float mWidth;
    protected float mHeight;
    protected float mRenderWidth;
    protected float mRenderHeight;
    protected float mRenderLeft;
    protected float mRenderRight;
    protected float mRenderTop;
    protected float mRenderBottom;
    protected int mPressedRow;
    protected int mPressedColumn;
    protected boolean mGridClickable;
    protected ArrayList<OnTouchGridListener> mOnTouchGridListeners = null;

    /**
     * @param z z-coordinate.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param gridClickable 그리드 onClickGrid{@link OnClickGridListener} 호출 여부.
     * @param clickable 전체 클릭이벤트  여부.
     * @param width  x-axis length.
     * @param height y-axis length.
     * @param row number of rows.
     * @param column number of columns.
     */
    @WorkerThread
    public GridObject(float z, float x, float y, float width, float height, @IntRange(from = 1) int row, @IntRange(from = 1) int column,boolean visibility, boolean clickable, boolean gridClickable){
        super(z, x, y, row*column, visibility, clickable);
        mWidth = width;
        mHeight = height;
        mRow = row;
        mColumn = column;
        mGridClickable = gridClickable;
    }

    @WorkerThread
    public void addOnTouchGridListener(@NonNull OnTouchGridListener onTouchGridListener){
        if(mOnTouchGridListeners ==null){
            mOnTouchGridListeners = new ArrayList<>();
        }
        if(!mOnTouchGridListeners.contains(onTouchGridListener)) mOnTouchGridListeners.add(onTouchGridListener);
    }

    @WorkerThread
    public void removeOnTouchGridListener(@NonNull OnTouchGridListener onTouchGridLIstener){
        if(mOnTouchGridListeners !=null) mOnTouchGridListeners.remove(onTouchGridLIstener);
    }

    @WorkerThread
    public void changeWidth(float width){
        changeSize(width, mHeight);
    }

    @WorkerThread
    public void changeHeight(float height){
        changeSize(mWidth, height);
    }


    @WorkerThread
    public float getWidth(){
        return mWidth;
    }

    @WorkerThread
    public float getHeight(){
        return mHeight;
    }

    @WorkerThread
    public void changeSize(float width, float height){
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                Object object = getChild(i, j);
                if(object!=null) {
                    float deltaX = object.mX - mX;
                    float deltaY = object.mY - mY;
                    deltaX *= width / mWidth;
                    deltaY *= height / mHeight;
                    object.moveXY(mX + deltaX, mY + deltaY);
                }
            }
        }
        mWidth = width;
        mHeight = height;
        calculateAndCheckBoundary();
    }

    @WorkerThread
    public Object getChild(int row, int column){
        return mObjectList[mColumn*row+column];
    }

    @WorkerThread
    public int getRowSize(){
        return mRow;
    }

    @WorkerThread
    public int getColumnSize(){
        return mColumn;
    }

    @WorkerThread
    public void addOnClickGridListener(@NonNull OnClickGridListener onClickGridListener){
        if(mOnClickGridListeners == null)
            mOnClickGridListeners = new ArrayList<>();
        if(!mOnClickGridListeners.contains(onClickGridListener))
            mOnClickGridListeners.add(onClickGridListener);
    }

    @WorkerThread
    public void removeOnClickGridListener(@NonNull OnClickGridListener onClickGridListener){
        if(mOnClickGridListeners !=null)
            mOnClickGridListeners.remove(onClickGridListener);
    }

    @WorkerThread
    public void putChild(Object obj, int row, int column){
        super.putChild(obj, row*mColumn+column);
    }

    /**
     * @param object
     * @return child가 아니면 -1.
     */
    @WorkerThread
    public int getChildRow(Object object){
        int idx = getChildIndex(object);
        if(idx!=-1){
            return idx/mColumn;
        }else{
            return -1;
        }
    }

    /**
     * @param object
     * @return child가 아니면 -1.
     */
    @WorkerThread
    public int getChildColumn(Object object){
        int idx = getChildIndex(object);
        if(idx!=-1){
            return idx%mColumn;
        }else{
            return -1;
        }
    }

    @WorkerThread
    @Override
    boolean checkBoundary(int x, int y) {
        return mGridClickable && x < mRenderRight && x > mRenderLeft && y < mRenderBottom && y > mRenderTop;
    }


    //해당 메소드가 오직 world.onTouch를 통해서만 호출된다 가정.
    @WorkerThread
    boolean checkTouchDown(int x, int y){
        if(isClickable()&&mIsInCameraRange&&checkBoundary(x, y)){
            mPressedColumn = (int) ((x - mRenderLeft) * mColumn / mRenderWidth);
            mPressedRow = (int) ((y - mRenderTop) * mRow / mRenderHeight);
            mIsPressed = true;
            onTouchDown(x, y);
            onGridTouchDown(x, y, mPressedRow, mPressedColumn);
            return mConsumeTouchEvent;
        }else{
            return false;
        }
    }

    //해당 메소드가 오직 world.onTouch를 통해서만 호출된다 가정.
    @WorkerThread
    void checkTouchCancel(){
        if(mIsPressed){
            mIsPressed = false;
            onTouchCancel();
            onGridTouchCancel(mPressedRow, mPressedColumn);
        }
    }

    //해당 메소드가 오직 world.onTouch를 통해서만 호출된다 가정.
    @WorkerThread
    void checkTouchUp(int x, int y){
        if(mIsPressed){
            mIsPressed = false;
            int column = (int) ((x - mRenderLeft) * mColumn / mRenderWidth);
            int row = (int) ((y - mRenderTop) * mRow / mRenderHeight);
            if(mIsInCameraRange&&checkBoundary(x, y)&&column==mPressedColumn&&row==mPressedRow){
                onTouchUp(x, y);
                onGridTouchUp(x, y, mPressedRow, mPressedColumn);
                onClick();
                onGridClick(mPressedRow, mPressedColumn);
            }else{
                onTouchCancel();
                onGridTouchCancel(mPressedRow, mPressedColumn);
            }
        }
    }

    //해당 메소드가 오직 world.onTouch를 통해서만 호출된다 가정.
    @WorkerThread
    void checkDrag(int x, int y){
        if(mIsPressed){
            int column = (int) ((x - mRenderLeft) * mColumn / mRenderWidth);
            int row = (int) ((y - mRenderTop) * mRow / mRenderHeight);
            if(mIsInCameraRange&&checkBoundary(x, y)&&column==mPressedColumn&&row==mPressedRow){
                onTouchDrag(x, y);
                onGridTouchDrag(x, y, mPressedRow, mPressedColumn);
            }else{
                mIsPressed = false;
                onTouchCancel();
                onGridTouchCancel(mPressedRow, mPressedColumn);
            }
        }
    }

    public interface OnTouchGridListener{
        @WorkerThread
        void onTouchGridDown(World world, GridObject gridObject, int x, int y, int row, int column);
        @WorkerThread
        void onTouchGridUp(World world, GridObject gridObject, int x, int y, int row, int column);
        @WorkerThread
        void onTouchGridCancel(World world, GridObject gridObject, int row, int column);
        @WorkerThread
        void onTouchGridDrag(World world, GridObject gridObject, int x, int y, int row, int column);
    }

    @WorkerThread
    public void onGridTouchDown(int x, int y, int row, int column){
        if(mOnTouchGridListeners !=null){
            int size = mOnTouchGridListeners.size();
            for(int i=0;i<size;i++)
                mOnTouchGridListeners.get(i).onTouchGridDown(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchDrag(int x, int y, int row, int column){
        if(mOnTouchGridListeners !=null){
            int size = mOnTouchGridListeners.size();
            for(int i=0;i<size;i++)
                mOnTouchGridListeners.get(i).onTouchGridDrag(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchUp(int x, int y, int row, int column){
        if(mOnTouchGridListeners !=null){
            int size = mOnTouchGridListeners.size();
            for(int i=0;i<size;i++)
                mOnTouchGridListeners.get(i).onTouchGridUp(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchCancel(int row, int column){
        if(mOnTouchGridListeners !=null){
            int size = mOnTouchGridListeners.size();
            for(int i=0;i<size;i++)
                mOnTouchGridListeners.get(i).onTouchGridCancel(mAttachedWorld, this, row, column);
        }
    }

    @WorkerThread
    public void onGridClick(int row, int column){
        if (mOnClickGridListeners != null) {
            int size = mOnClickGridListeners.size();
            for (int i = 0; i < size; i++)
                mOnClickGridListeners.get(i).onClickGrid(mAttachedWorld, this, getChild(row, column), row, column);
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

    public interface OnClickGridListener {
        @WorkerThread
        void onClickGrid(World world, GridObject gridObject, @Nullable Object object, int row, int column);
    }


    /**
     * get Y coordinate of given row.
     * @param row
     * @return y-coordinate of given row.
     */
    @WorkerThread
    public float getRowY(int row){
        return getRowY(row, mRow);
    }

    /**
     * get X coordinate of given column.
     * @param column
     * @return y-coordinate of given row.
     */
    @WorkerThread
    public float getColumnX(int column){
        return getColumnX(column, mColumn);
    }

    float getColumnX(int column, int columnSize){
        return mX-(mWidth/2)+mWidth*column/columnSize+((mWidth/columnSize)/2);
    }
    float getRowY(int row, int rowSize){
        return mY-(mHeight/2)+mHeight*row/rowSize+((mHeight/rowSize)/2);
    }

    @Override
    public void changeGroupSize(int groupSize) {
        throw new RuntimeException("Try to change grid size");
    }

    @Override
    public void moveZ(float z) {
        super.moveZ(z);
        if(mAttachedWorld!=null)calculateScale();
    }

    public void changeGridSize(int row, int column){
        super.changeGroupSize(row*column);
        float[] lastColumnX = new float[mColumn];
        for(int i=0; i<mColumn;i++){
            lastColumnX[i] = getColumnX(i);
        }
        float[] columnX = new float[column];
        float[] rowY = new float[row];
        for(int i=0; i<column;i++){
            columnX[i] = getColumnX(i, column);
        }
        for(int i=0; i<row;i++){
            rowY[i] = getRowY(i, row);
        }
        int count =0;
        for(int i=0; i<mRow; i++){
            float lastRowY = getRowY(i);
            for(int j=0; j<mColumn; j++){
                if(count<mGroupSize) {
                    Object object = mObjectList[count];
                    if (object != null) {
                        float deltaX = object.mX - lastColumnX[j];
                        float deltaY = object.mY - lastRowY;
                        object.moveXY(columnX[count % column] + deltaX, rowY[count / column] + deltaY);
                    }
                    count++;
                }else{
                    break;
                }
            }
        }
        mColumn = column;
        mRow = row;
    }

    public void alignChild(){
        float[] columnX = new float[mColumn];
        for(int i=0; i<mColumn; i++){
            columnX[i] = getColumnX(i);
        }
        int count = 0;
        for(int i=0;i<mRow;i++){
            float rowY = getRowY(i);
            for(int j=0; j<mColumn;j++){
                Object object = mObjectList[count++];
                if(object!=null){
                    object.moveXY(columnX[i],rowY);
                }
            }
        }
    }
}