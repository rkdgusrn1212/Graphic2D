package com.khgkjg12.graphic2d;

import java.util.List;

public interface GridObject {

    List<Object> getObjects();

    void putObject(Object obj, int row, int column);

    void putObject(Texture texture, int row, int column);

    void putObject(int color, int row, int column);

    void removeObject(int row, int column);

    interface OnClickItemListener{
        void onClickItem(GridObject gridObject, Object[][] objectList, int row, int column);
    }

    void setOnClickItemListener(OnClickItemListener onClickItemListener);
}
