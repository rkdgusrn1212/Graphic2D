package com.khgkjg12.graphic2d;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public class GroupObject extends Object implements Object.OnClickListener, Group{
    private Object[] mObjectList;
    private int mGroupSize;
    private OnClickGroupListener mOnClickGroupListener;
    private World mWorld;

    /**
     * @param z                    그룹 기준 z-coordinate.
     * @param x                    그룹 기준 x-coordinate.
     * @param y                    그룹 기준 y-coordinate.
     * @param clickable            OnClickGroup 호출 여부.
     * @param groupSize            그룹으로 묶일수 있는 최대 갯수.
     * @param onClickGroupListener touch event callback {@link com.khgkjg12.graphic2d.GroupObject.OnClickGroupListener}
     */
    public GroupObject(float z, int x, int y, boolean clickable, int groupSize, @Nullable OnClickGroupListener onClickGroupListener) {
        super(z, x, y, false, clickable, null);
        mOnClickGroupListener = onClickGroupListener;
        mGroupSize = groupSize;
        mObjectList = new Object[mGroupSize];
    }

    public Object getObject(int idx) {
        return mObjectList[idx];
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    @WorkerThread
    public void setGroupSize(@IntRange(from=1) int groupSize) {
        Object[] tempArray = new Object[groupSize];
        System.arraycopy(mObjectList, 0, tempArray, 0, Math.min(groupSize, mGroupSize));
        mGroupSize = groupSize;
        mObjectList = tempArray;
    }

    @WorkerThread
    public void setOnClickItemListener(OnClickGroupListener onClickGroupListener) {
        mOnClickGroupListener = onClickGroupListener;
    }

    /**
     * 해당 인덱스에 오브젝트를 삽입 및 교체.
     * @exception IndexOutOfBoundsException idx 범위가 유효하지 않는경우.
     * @param obj null 입력시 해당 인덱스 오브젝트 제거.
     * @param idx 대상 인덱스.
     */
    @WorkerThread
    public void putObject(Object obj, @IntRange(from = 0) int idx) {
        if (mObjectList[idx] != null) {
            if (mWorld != null) {
                mWorld.removeObject(mObjectList[idx]);
            }
        }
        if (obj != null) {
            obj.setOnClickListener(this);
            if (mWorld != null) {
                mWorld.putObject(obj);
            }
        }
        mObjectList[idx] = obj;
    }

    @WorkerThread
    public void attached(World world) {
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                world.putObject(mObjectList[i]);
            }
        }
        mWorld = world;
    }

    @WorkerThread
    public void detached(World world) {
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                world.removeObject(mObjectList[i]);
            }
        }
        mWorld = null;
    }

    @WorkerThread
    @Override
    boolean checkBoundary(int x, int y) {
        return false;
    }

    @WorkerThread
    @Override
    void calculateBoundary() {
    }


    @WorkerThread
    public void moveXY(World world, int x, int y) {
        int deltaX = x - mX;
        int deltaY = y - mY;
        mX = x;
        mY = y;
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                mObjectList[i].mX += deltaX;
                mObjectList[i].mY += deltaY;
                mObjectList[i].calculateRenderXY(world);
            }
        }
    }

    @Override
    void draw(Graphic2dDrawer drawer) {
    }

    @WorkerThread
    @Override
    public boolean onClick(World world, Object object) {
        if(mClickable&&mOnClickGroupListener!=null) {
            for (int i = 0; i < mGroupSize; i++) {
                if (mObjectList[i] == object) {
                    return mOnClickGroupListener.onClickGroup(world, this, object, i);
                }
            }
        }
        return false;
    }

    public interface OnClickGroupListener {
        @WorkerThread
        boolean onClickGroup(World world, GroupObject groupObject, Object object, int idx);
    }

    @WorkerThread
    @Override
    public void moveZ(World world, float z) {
        float deltaZ = z - mZ;
        mZ = z;
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                mObjectList[i].mZ += deltaZ;
                mObjectList[i].calculateScale(world);
            }
        }
    }
}