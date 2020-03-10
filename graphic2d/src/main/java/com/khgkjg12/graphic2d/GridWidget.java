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

import android.support.annotation.GuardedBy;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public class GridWidget extends GroupWidget {
    private int mRow, mColumn;
    private OnClickGridListener mOnClickGridListener;
    private float mWidth;
    private float mHeight;
    private float mLeft;
    private float mTop;
    private float mBottom;
    private float mRight;
    private int mPressedRow;
    private int mPressedColumn;
    private OnTouchGridListener mOnTouchGridListener;

    /**
     * @param z z-coordinate.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param gridClickable 그리드 onClickGrid{@link OnClickGridListener} 호출 여부.
     * @param childClickable 그리드 {@link OnClickChildListener} 호출여부.
     * @param width  x-axis length.
     * @param height y-axis length.
     * @param row number of rows.
     * @param column number of columns.
     * @param onClickGridListener touch event callback {@link OnClickGridListener}
     */
    @WorkerThread
    public GridWidget(float z, float x, float y, float width, float height, int row, int column, boolean childClickable, @Nullable OnClickChildListener onClickChildListener, boolean gridClickable, @Nullable OnClickGridListener onClickGridListener){
        super(z, x, y, row*column,childClickable, onClickChildListener);
        mWidth = width;
        mHeight = height;
        mRow = row;
        mColumn = column;
        mLeft =  mX - mWidth/2;
        mTop = mY - mHeight/2;
        mRight = mLeft + mWidth;
        mBottom = mTop + mHeight;
        mOnClickGridListener = onClickGridListener;
        mOnTouchGridListener = null;
        setClickable(gridClickable);
    }

    @WorkerThread
    public void setOnTouchGridListener(@Nullable OnTouchGridListener onTouchGridListener){
        mOnTouchGridListener = onTouchGridListener;
    }

    @WorkerThread
    public Widget getChild(int row, int column){
        return super.getChild(mColumn*row+column);
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
    public void setOnClickGridListener(@Nullable OnClickGridListener onClickGridListener){
        mOnClickGridListener = onClickGridListener;
    }

    @WorkerThread
    public void putChild(Widget widget, int row, int column){
        super.putChild(widget, row*mColumn+column);
    }

    /**
     * @param widget
     * @return child가 아니면 -1.
     */
    @WorkerThread
    public int getChildRow(Widget widget){
        int idx = getChildIndex(widget);
        if(idx!=-1){
            return idx/mColumn;
        }else{
            return -1;
        }
    }

    /**
     * @param widget
     * @return child가 아니면 -1.
     */
    @WorkerThread
    public int getChildColumn(Widget widget){
        int idx = getChildIndex(widget);
        if(idx!=-1){
            return idx%mColumn;
        }else{
            return -1;
        }
    }

    @WorkerThread
    @Override
    boolean checkBoundary(int x, int y) {
        return x < mRight && x > mLeft && y < mBottom && y > mTop;
    }


    //해당 메소드가 오직 world.onTouch를 통해서만 호출된다 가정.
    @WorkerThread
    boolean checkTouchDown(int x, int y){
        if(isClickable()&&checkBoundary(x, y)){
            mPressedColumn = (int) ((x - mLeft) * mColumn / mWidth);
            mPressedRow = (int) ((y - mTop) * mRow / mHeight);
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
            int column = (int) ((x - mLeft) * mColumn / mWidth);
            int row = (int) ((y - mTop) * mRow / mHeight);
            if(checkBoundary(x, y)&&column==mPressedColumn&&row==mPressedRow){
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
    boolean checkDrag(int x, int y){
        if(mIsPressed){
            int column = (int) ((x - mLeft) * mColumn / mWidth);
            int row = (int) ((y - mTop) * mRow / mHeight);
            if(checkBoundary(x, y)&&column==mPressedColumn&&row==mPressedRow){
                onTouchDrag(x, y);
                onGridTouchDrag(x, y, mPressedRow, mPressedColumn);
                return mConsumeDragEvent;
            }else{
                mIsPressed = false;
                onTouchCancel();
                onGridTouchCancel(mPressedRow, mPressedColumn);
            }
        }
        return false;
    }

    public interface OnTouchGridListener{
        @WorkerThread
        void onTouchGridDown(World world, GridWidget gridWidget, int x, int y, int row, int column);
        @WorkerThread
        void onTouchGridUp(World world, GridWidget gridWidget, int x, int y, int row, int column);
        @WorkerThread
        void onTouchGridCancel(World world, GridWidget gridWidget, int row, int column);
        @WorkerThread
        void onTouchGridDrag(World world, GridWidget gridWidget, int x, int y, int row, int column);
    }

    @WorkerThread
    public void onGridTouchDown(int x, int y, int row, int column){
        if(mOnTouchGridListener!=null){
            mOnTouchGridListener.onTouchGridDown(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchDrag(int x, int y, int row, int column){
        if(mOnTouchGridListener!=null){
            mOnTouchGridListener.onTouchGridDrag(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchUp(int x, int y, int row, int column){
        if(mOnTouchGridListener!=null){
            mOnTouchGridListener.onTouchGridUp(mAttachedWorld, this, x, y, row, column);
        }
    }
    @WorkerThread
    public void onGridTouchCancel(int row, int column){
        if(mOnTouchGridListener!=null){
            mOnTouchGridListener.onTouchGridCancel(mAttachedWorld, this, row, column);
        }
    }

    @WorkerThread
    public void onGridClick(int row, int column){
        if (mOnClickGridListener != null)
            mOnClickGridListener.onClickGrid(mAttachedWorld, this, getChild(row,column), row, column);
    }

    @Override
    void calculateBoundary() {
        mLeft =  mX - mWidth/2;
        mTop = mY - mHeight/2;
        mRight = mLeft + mWidth;
        mBottom = mTop + mHeight;
    }

    @Override
    void draw(Graphic2dDrawer drawer) { }

    public interface OnClickGridListener {
        @WorkerThread
        void onClickGrid(World world, GridWidget gridWidget, @Nullable Widget widget, int row, int column);
    }


    /**
     * get Y coordinate of given row.
     * @param row
     * @return y-coordinate of given row.
     */
    @WorkerThread
    public float getRowY(int row){
        return mY-(mHeight/2)+mHeight*row/mRow+((mHeight/mRow)/2);
    }

    /**
     * get X coordinate of given column.
     * @param column
     * @return y-coordinate of given row.
     */
    @WorkerThread
    public float getColumnX(int column){
        return mX-(mWidth/2)+mWidth*column/mColumn+((mWidth/mColumn)/2);
    }

    @Override
    public void changeGroupSize(int groupSize) {
        throw new RuntimeException("Try to change grid size");
    }
}