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

public interface GridObject {

    Object getObject(int row, int column);

    int getRowSize();
    int getColumnSize();

    /**
     * put object without position adjustment.
     * @param world
     * @param obj
     * @param row
     * @param column
     */
    @WorkerThread
    void putObject(@NonNull World world, @NonNull Object obj, int row, int column);

    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * @param obj
     * @param row
     * @param column
     */
    @WorkerThread
    void putObjectAndAdjust(@NonNull World world, @NonNull Object obj, int row, int column);

    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * @param texture
     * @param padding
     * @param row
     * @param column
     */
    @WorkerThread
    void createAndPutTextureObject(@NonNull World world,@NonNull Texture texture, int padding, int row, int column);

    @WorkerThread
    void createAndPutTextureObject(@NonNull World world,@NonNull Texture texture, int width, int height, int row, int column);

    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * @param color
     * @param padding
     * @param row
     * @param column
     */
    @WorkerThread
    void createAndPutRectObject(@NonNull World world, int color, int padding, int row, int column);

    @WorkerThread
    void createAndPutRoundRectObject(@NonNull World world, int color, float rX, float rY, int row, int padding, int column);


    @WorkerThread
    void createAndPutRectObject(@NonNull World world, int color, int width, int height, int row, int column);

    @WorkerThread
    void createAndPutRoundRectObject(@NonNull World world, int color, float rX, float rY, int width, int height, int row, int column);

    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * @param row
     * @param column
     */
    @WorkerThread
    void removeObject(World world, int row, int column);
    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * */
    @WorkerThread
    void attached(World world);
    /**
     * Only Use in Callback Method that has World Parameter.
     * @param world from Callback Method Parameter.
     * */
    @WorkerThread
    void detached(World world);

    interface OnClickItemListener{
        /**
         * 해당 그리드의 셀에 들어있는 오브젝트를 콜벡함수를 통해 반환.
         * @param gridObject
         * @param object
         * @param row
         * @param column
         * @return 터치이벤트의 소멸 여부. true 는 소멸, false 는 전달.
         */
        @WorkerThread
        boolean onClickItem(World world, GridObject gridObject, @Nullable Object object, int row, int column);
    }

    @WorkerThread
    void setOnClickItemListener(OnClickItemListener onClickItemListener);
}
