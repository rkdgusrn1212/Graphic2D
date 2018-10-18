package com.khgkjg12.graphic2d;

public class GridObject extends Object {

    Object[][] mObjectList;
    int mRow, mColumn;
    private OnClickItemListener mOnClickItemListener;

    public GridObject(String texturePath, int width, int height, int row, int column){
        super(texturePath, width, height);
        mObjectList = new Object[row][column];
    }

    boolean onTouch(int x, int y){
        int column = (x-mBoundary.left)*mColumn/mBoundary.width();
        int row = (y-mBoundary.top)*mRow/mBoundary.height();
        if(column>=0&&row>=0&&column<mColumn&&row<mRow){
            if(mOnClickItemListener!=null) {
                mOnClickItemListener.onClickItem(mObjectList, row, column);
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
        mObjectList[row][column] = obj;
    }

    public void removeObject(int row, int column){
        mObjectList[row][column].dispose();
        mObjectList = null;
    }

    interface OnClickItemListener{
        void onClickItem(Object[][] objectList, int row, int column);
    }
}
