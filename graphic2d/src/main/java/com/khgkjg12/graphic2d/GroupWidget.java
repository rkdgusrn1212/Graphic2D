package com.khgkjg12.graphic2d;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public class GroupWidget extends Widget {
    private Widget[] mWidgetList;
    private int mGroupSize;
    private GroupWidget.OnClickChildListener mOnClickChildListener;
    GroupWidget.InnerItemListener mInnerItemListener;

    /**
     * @param z                    그룹 기준 z-coordinate.
     * @param x                    그룹 기준 x-coordinate.
     * @param y                    그룹 기준 y-coordinate.
     * @param groupSize            그룹으로 묶일수 있는 최대 갯수.
     * @param clickable            OnClickGroup 호출 여부.
     * @param onClickChildListener touch event callback {@link GroupObject.OnClickChildListener}
     */
    @WorkerThread
    public GroupWidget(float z, float x, float y, int groupSize, boolean clickable, @Nullable GroupWidget.OnClickChildListener onClickChildListener) {
        super(z, x, y, false, clickable, null);
        mOnClickChildListener = onClickChildListener;
        mGroupSize = groupSize;
        mWidgetList = new Widget[mGroupSize];
        mInnerItemListener = new GroupWidget.InnerItemListener();
    }

    public Widget getChild(int idx) {
        return mWidgetList[idx];
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    @WorkerThread
    public void changeGroupSize(@IntRange(from=1) int groupSize) {
        Widget[] tempArray = new Widget[groupSize];
        System.arraycopy(mWidgetList, 0, tempArray, 0, Math.min(groupSize, mGroupSize));
        mGroupSize = groupSize;
        mWidgetList = tempArray;
    }

    @WorkerThread
    public void setOnClickGroupListener(@Nullable GroupWidget.OnClickChildListener onClickGroupListener) {
        mOnClickChildListener = onClickGroupListener;
    }

    /**
     * 해당 인덱스에 오브젝트를 삽입 및 교체.
     * @exception IndexOutOfBoundsException idx 범위가 유효하지 않는경우.
     * @param widget null 입력시 해당 인덱스 오브젝트 제거.
     * @param idx 대상 인덱스.
     */
    @WorkerThread
    public void putChild(Widget widget, @IntRange(from = 0) int idx) {
        if (mWidgetList[idx] != null) {
            mWidgetList[idx].leaveGroup();
            if (mAttachedWorld != null) {
                mAttachedWorld.removeWidget(mWidgetList[idx]);
            }
        }
        if (widget != null) {
            widget.joinGroup(this);
            if (mAttachedWorld != null) {
                mAttachedWorld.putWidget(widget);
            }
        }
        mWidgetList[idx] = widget;
    }

    @WorkerThread
    @Override
    void attached(World world) {
        super.attached(world);
        for (int i = 0; i < mGroupSize; i++) {
            if(mWidgetList[i]!=null) {
                mAttachedWorld.putWidget(mWidgetList[i]);
            }
        }
    }

    @WorkerThread
    @Override
    void detached(World world) {//super.detach 후 발생하는 NullPointer 때문
        super.detached(world);
        for (int i = 0; i < mGroupSize; i++) {
            if(mWidgetList[i]!=null) {
                world.removeWidget(mWidgetList[i]);
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
    public void moveXY(float x, float y) {
        float deltaX = x - mX;
        float deltaY = y - mY;
        mX = x;
        mY = y;
        for (int i = 0; i < mGroupSize; i++) {
            if(mWidgetList[i]!=null) {
                mWidgetList[i].moveXY(mWidgetList[i].mX+deltaX, mWidgetList[i].mY+deltaY);
            }
        }
    }

    @Override
    void calculateBoundary() { }

    @Override
    void draw(Graphic2dDrawer drawer) {
    }

    /**
     * To get child object idx.
     * @param widget 그룹의 자식오브젝트.
     * @return 오브젝트의 인덱스. 없으면 -1.
     */
    @WorkerThread
    public int getChildIndex(@Nullable Widget widget){
        for(int i=0; i<mGroupSize; i++){
            if(widget == mWidgetList[i]){
                return i;
            }
        }
        return -1;
    }

    class InnerItemListener implements Widget.ChildListener {
        @WorkerThread
        @Override
        public void onClick(@Nullable World attachedWorld, @NonNull Widget widget) {
            onChildClick(attachedWorld, widget);
        }

        @Override
        public void onTouchDown(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y) {
            onChildTouchDown(attachedWorld, x, y, widget);
        }

        @Override
        public void onTouchUp(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y) {
            onChildTouchUp(attachedWorld, x, y, widget);
        }

        @Override
        public void onTouchCancel(@Nullable World attachedWorld, @NonNull Widget widget) {
            onChildTouchCancel(attachedWorld, widget);
        }

        @Override
        public void onTouchDrag(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y) {
            onChildTouchDrag(attachedWorld, x, y, widget);
        }
    }

    public interface OnClickChildListener {
        /**
         * 항상 호출은 오브젝트트리의 말단부터 시작되고 부모 클래스의 콜백이 우선이다.
         * @param attachedWorld
         * @param groupWidget
         * @param widget
         * @param idx -1 이면 콜백 호출 직전에 해당 오브젝트가 그룹에서 제거된것.
         */
        @WorkerThread
        void onClickChild(@Nullable World attachedWorld, @NonNull GroupWidget groupWidget, @NonNull Widget widget, int idx);
    }

    @WorkerThread
    @Override
    public void moveZ(float z) {
        float deltaZ = z - mZ;
        mZ = z;
        for (int i = 0; i < mGroupSize; i++) {
            if(mWidgetList[i]!=null) {
                mWidgetList[i].moveZ(mWidgetList[i].mZ+deltaZ);
            }
        }
    }

    @WorkerThread
    public void onChildTouchDown(@Nullable World attachedWorld, int x, int y, @NonNull Widget widget){
        onTouchDown(x, y);
    }

    @WorkerThread
    public void onChildTouchDrag(@Nullable World attachedWorld, int x, int y, @NonNull Widget widget){
        onTouchDrag(x, y);
    }

    @WorkerThread
    public void onChildTouchUp(@Nullable World attachedWorld, int x, int y, @NonNull Widget widget){
        onTouchUp(x, y);
    }

    @WorkerThread
    public void onChildTouchCancel(@Nullable World attachedWorld, @NonNull Widget widget){
        onTouchCancel();
    }

    @WorkerThread
    public void onChildClick(@Nullable World attachedWorld, @NonNull Widget widget){
        onClick();
        if(mOnClickChildListener !=null) mOnClickChildListener.onClickChild(attachedWorld, this, widget, getChildIndex(widget));
    }
}
