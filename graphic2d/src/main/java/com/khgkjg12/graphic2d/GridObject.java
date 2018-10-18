package com.khgkjg12.graphic2d;

import android.graphics.Color;

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
        mObjectList = new Object[row][column];
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
        mObjectList[row][column].dispose();
        mObjectList = null;
    }

    @Override
    void render(Graphic2dDrawer drawer, int worldWidth, int worldHeight, float scale, int viewportX, int viewportY) {
        super.render(drawer, worldWidth, worldHeight, scale, viewportX, viewportY);
        for(int i=0; i<mRow; i++){
            for(int j=0; j<mColumn; j++){
                Object obj = mObjectList[i][j];
                if(obj!=null){
                    obj.render(drawer, worldWidth, worldHeight, scale, viewportX, viewportY);
                }
            }
        }
    }

    public interface OnClickItemListener{
        public void onClickItem(GridObject gridObject, Object[][] objectList, int row, int column);
    }
}
