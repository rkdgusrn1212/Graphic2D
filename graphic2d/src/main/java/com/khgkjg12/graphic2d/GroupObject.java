package com.khgkjg12.graphic2d;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public class GroupObject extends Object{
    private Object[] mObjectList;
    private int mGroupSize;
    private OnClickChildListener mOnClickChildListener;
    InnerItemListener mInnerItemListener;
    boolean mChildClickable;

    /**
     * @param z                    그룹 기준 z-coordinate.
     * @param x                    그룹 기준 x-coordinate.
     * @param y                    그룹 기준 y-coordinate.
     * @param groupSize            그룹으로 묶일수 있는 최대 갯수.
     * @param childClickable       OnClickGroup 호출 여부.
     * @param onClickChildListener touch event callback {@link OnClickChildListener}
     */
    @WorkerThread
    public GroupObject(float z, float x, float y, int groupSize, boolean childClickable, @Nullable OnClickChildListener onClickChildListener) {
        super(z, x, y, false, false, null);
        mOnClickChildListener = onClickChildListener;
        mGroupSize = groupSize;
        mObjectList = new Object[mGroupSize];
        mInnerItemListener = new InnerItemListener();
        mChildClickable = childClickable;
    }

    @WorkerThread
    public void setChildClickable(boolean childClickable){
        mChildClickable = childClickable;
    }

    public boolean getChildClickable(){
        return mChildClickable;
    }

    public Object getChild(int idx) {
        return mObjectList[idx];
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    @WorkerThread
    public void changeGroupSize(@IntRange(from=1) int groupSize) {
        Object[] tempArray = new Object[groupSize];
        System.arraycopy(mObjectList, 0, tempArray, 0, Math.min(groupSize, mGroupSize));
        mGroupSize = groupSize;
        mObjectList = tempArray;
    }

    @WorkerThread
    public void setOnClickGroupListener(@Nullable OnClickChildListener onClickGroupListener) {
        mOnClickChildListener = onClickGroupListener;
    }

    /**
     * 해당 인덱스에 오브젝트를 삽입 및 교체.
     * @exception IndexOutOfBoundsException idx 범위가 유효하지 않는경우.
     * @param obj null 입력시 해당 인덱스 오브젝트 제거.
     * @param idx 대상 인덱스.
     */
    @WorkerThread
    public void putChild(Object obj, @IntRange(from = 0) int idx) {
        if (mObjectList[idx] != null) {
            mObjectList[idx].leaveGroup();
            if (mAttachedWorld != null) {
                mAttachedWorld.removeObject(mObjectList[idx]);
            }
        }
        if (obj != null) {
            obj.joinGroup(this);
            if (mAttachedWorld != null) {
                mAttachedWorld.putObject(obj);
            }
        }
        mObjectList[idx] = obj;
    }

    @WorkerThread
    @Override
    void attached(World world) {
        super.attached(world);
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                mAttachedWorld.putObject(mObjectList[i]);
            }
        }
    }

    @WorkerThread
    @Override
    void detached(World world) {//super.detach 후 발생하는 NullPointer 때문
        super.detached(world);
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                world.removeObject(mObjectList[i]);
            }
        }
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
    @Override
    public void moveXY(float x, float y) {
        float deltaX = x - mX;
        float deltaY = y - mY;
        mX = x;
        mY = y;
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                mObjectList[i].moveXY(mObjectList[i].mX+deltaX, mObjectList[i].mY+deltaY);
            }
        }
    }
    @Override
    void draw(Graphic2dDrawer drawer) {
    }

    /**
     * To get child object idx.
     * @param object 그룹의 자식오브젝트.
     * @return 오브젝트의 인덱스. 없으면 -1.
     */
    @WorkerThread
    public int getChildIndex(@Nullable Object object){
        for(int i=0; i<mGroupSize; i++){
            if(object == mObjectList[i]){
                return i;
            }
        }
        return -1;
    }

    class InnerItemListener implements ChildListener{
        @WorkerThread
        @Override
        public void onClick(@Nullable World attachedWorld, @NonNull Object object) {
            onChildClick(attachedWorld, object);
        }

        @Override
        public void onTouchDown(@Nullable World attachedWorld, @NonNull Object object, int x, int y) {
            onChildTouchDown(attachedWorld, x, y, object);
        }

        @Override
        public void onTouchUp(@Nullable World attachedWorld, @NonNull Object object, int x, int y) {
            onChildTouchUp(attachedWorld, x, y, object);
        }

        @Override
        public void onTouchCancel(@Nullable World attachedWorld, @NonNull Object object) {
            onChildTouchCancel(attachedWorld, object);
        }

        @Override
        public void onTouchDrag(@Nullable World attachedWorld, @NonNull Object object, int x, int y) {
            onChildTouchDrag(attachedWorld, x, y, object);
        }
    }

    public interface OnClickChildListener {
        /**
         * 항상 호출은 오브젝트트리의 말단부터 시작되고 부모 클래스의 콜백이 우선이다.
         * @param attachedWorld
         * @param groupObject
         * @param object
         * @param idx -1 이면 콜백 호출 직전에 해당 오브젝트가 그룹에서 제거된것.
         */
        @WorkerThread
        void onClickChild(@Nullable World attachedWorld, @NonNull GroupObject groupObject, @NonNull Object object, int idx);
    }

    @WorkerThread
    @Override
    public void moveZ(float z) {
        float deltaZ = z - mZ;
        mZ = z;
        for (int i = 0; i < mGroupSize; i++) {
            if(mObjectList[i]!=null) {
                mObjectList[i].moveZ(mObjectList[i].mZ+deltaZ);
            }
        }
    }

    @WorkerThread
    public void onChildTouchDown(@Nullable World attachedWorld, int x, int y, @NonNull Object object){
        onTouchDown(x, y);
    }

    @WorkerThread
    public void onChildTouchDrag(@Nullable World attachedWorld, int x, int y, @NonNull Object object){
        onTouchDrag(x, y);
    }

    @WorkerThread
    public void onChildTouchUp(@Nullable World attachedWorld, int x, int y, @NonNull Object object){
        onTouchUp(x, y);
    }

    @WorkerThread
    public void onChildTouchCancel(@Nullable World attachedWorld, @NonNull Object object){
        onTouchCancel();
    }

    @WorkerThread
    public void onChildClick(@Nullable World attachedWorld, @NonNull Object object){
        onClick();
        if(mOnClickChildListener !=null) mOnClickChildListener.onClickChild(attachedWorld, this, object, getChildIndex(object));
    }
}