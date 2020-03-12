package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public abstract class Widget {

    private boolean mVisibility;
    private boolean mClickable;
    private Widget.OnClickListener mOnClickListener;
    float mZ;
    float mX, mY;
    private Widget.OnTouchListener mOnTouchListener;
    boolean mIsPressed;
    boolean mConsumeTouchEvent;
    boolean mConsumeDragEvent;
    Widget.ChildListener mChildListener;
    World mAttachedWorld;
    GroupWidget mGroup;

    @WorkerThread
    public Widget(float z, float x, float y, boolean visibility, boolean clickable, Widget.OnClickListener onClickListener) {
        mVisibility = visibility;
        mClickable = clickable;
        mZ = z;
        mX = x;
        mY = y;
        mOnClickListener = onClickListener;
        mIsPressed = false;
        mConsumeTouchEvent = true;
        mConsumeDragEvent = false;
        mAttachedWorld = null;
        mGroup = null;
    }
    @WorkerThread
    void attached(World world){
        mAttachedWorld = world;
    }

    @WorkerThread
    void detached(World world) {
        mAttachedWorld = null;
    }

    @WorkerThread
    void joinGroup(GroupWidget group){
        mChildListener = group.mInnerItemListener;
        mGroup = group;
    }

    @WorkerThread
    void leaveGroup(){
        mChildListener = null;
        mGroup = null;
    }

    @WorkerThread
    public void setConsumeDragEvent(boolean consumeDragEvent){
        mConsumeDragEvent = consumeDragEvent;
    }

    @WorkerThread
    public boolean getConsumeDragEvent(){
        return mConsumeDragEvent;
    }

    @WorkerThread
    public void setConsumeTouchEvent(boolean consumeTouchEvent){
        mConsumeTouchEvent = consumeTouchEvent;
    }

    @WorkerThread
    public boolean getConsumeTouchEvent(){
        return mConsumeTouchEvent;
    }

    @WorkerThread
    public float getZ(){
        return mZ;
    }

    @WorkerThread
    public float getX(){
        return mX;
    }

    @WorkerThread
    public float getY(){
        return mY;
    }

    /**
     * World의 콜백 메서드에서만 사용.
     * @param visible
     */
    @WorkerThread
    public void setVisibility(boolean visible){
        mVisibility = visible;
    }

    /**
     * World의 콜백 메서드에서만 사용.
     * @param clickable
     */
    @WorkerThread
    public void setClickable(boolean clickable){
        mClickable = clickable;
    }

    @WorkerThread
    public void onClick(){
        if(mOnClickListener!=null) mOnClickListener.onClick(mAttachedWorld, this);
        if(mChildListener!=null) mChildListener.onClick(mAttachedWorld, this);
    }

    /**
     * 경계선 채크 메소드.
     * @return x, y 가 경계선 안에 있으면 참.
     */
    @WorkerThread
    abstract boolean checkBoundary(int x, int y);

    @WorkerThread
    public void setOnClickListener(@Nullable Widget.OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    @WorkerThread
    public void setOnTouchListener(@Nullable Widget.OnTouchListener onTouchListener){
        mOnTouchListener = onTouchListener;
    }

    @WorkerThread
    void render(Graphic2dDrawer drawer){
        if(mVisibility){
            draw(drawer);
        }
    }

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     */
    @WorkerThread
    abstract void draw(Graphic2dDrawer drawer);


    public interface OnClickListener{
        @WorkerThread
        void onClick(@Nullable World attachedWorld, Widget widget);
    }

    /**
     * @exception IndexOutOfBoundsException 해당 오브젝트 없음.
     * @param z 새 z 좌표.
     */
    @WorkerThread
    public void moveZ(float z){
        if(mAttachedWorld!=null) {
            if (z >= mZ) {
                int i = 0;
                while (mAttachedWorld.mWidgets[i].mZ > z) {
                    i++;
                }
                Widget tempWidget;
                int j = i;
                while (mAttachedWorld.mWidgets[j] != this) {
                    j++;
                }
                tempWidget = mAttachedWorld.mWidgets[j];
                while (i != j) {
                    mAttachedWorld.mWidgets[j] = mAttachedWorld.mWidgets[j - 1];
                    j--;
                }
                mAttachedWorld.mWidgets[j] = tempWidget;
            } else {
                int i = mAttachedWorld.mWidgetCount - 1;
                while (mAttachedWorld.mWidgets[i].mZ <= z) {
                    i--;
                }
                Widget tempWidget;
                int j = i;
                while (mAttachedWorld.mWidgets[j] != this) {
                    j--;
                }
                tempWidget = mAttachedWorld.mWidgets[j];
                while (i != j) {
                    mAttachedWorld.mWidgets[j] = mAttachedWorld.mWidgets[j + 1];
                    j++;
                }
                mAttachedWorld.mWidgets[j] = tempWidget;
            }
        }
        mZ = z;
    }

    @WorkerThread
    public void moveXY(float x, float y){
        mX = x;
        mY = y;
        calculateBoundary();
    }

    /**
     * 위젯 생성자 마지막에 한번 호출해주기
     */
    @WorkerThread
    abstract void calculateBoundary();

    public interface OnTouchListener{
        @WorkerThread
        void onTouchDown(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
        @WorkerThread
        void onTouchUp(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
        @WorkerThread
        void onTouchCancel(@Nullable World attachedWorld, @NonNull Widget widget);
        @WorkerThread
        void onTouchDrag(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
    }

    @WorkerThread
    public boolean isClickable(){
        return mClickable && (mGroup==null||mGroup.isClickable());
    }

    @WorkerThread
    boolean checkTouchDown(int x, int y){
        if(isClickable()&&checkBoundary(x, y)){
            mIsPressed = true;
            onTouchDown(x, y);
            return mConsumeTouchEvent;
        }else{
            return false;
        }
    }

    @WorkerThread
    void checkTouchCancel(){
        if(mIsPressed){
            mIsPressed = false;
            onTouchCancel();
        }
    }

    @WorkerThread
    void checkTouchUp(int x, int y){
        if(mIsPressed){
            mIsPressed = false;
            if(checkBoundary(x, y)){
                onTouchUp(x, y);
                onClick();
            }else{
                onTouchCancel();
            }
        }
    }

    @WorkerThread
    boolean checkDrag(int x, int y){
        if(mIsPressed){
            if(checkBoundary(x, y)){
                onTouchDrag(x, y);
                return mConsumeDragEvent;
            }else{
                mIsPressed = false;
                onTouchCancel();
            }
        }
        return false;
    }

    @WorkerThread
    public void onTouchDown(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchDown(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchDown(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchDrag(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchDrag(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchDrag(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchUp(int x, int y){
        if(mOnTouchListener!=null) mOnTouchListener.onTouchUp(mAttachedWorld, this, x, y);
        if(mChildListener!=null) mChildListener.onTouchUp(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchCancel(){
        if (mOnTouchListener != null) mOnTouchListener.onTouchCancel(mAttachedWorld, this);
        if (mChildListener != null) mChildListener.onTouchCancel(mAttachedWorld, this);
    }


    interface ChildListener{
        @WorkerThread
        void onClick(@Nullable World attachedWorld, @NonNull Widget widget);
        @WorkerThread
        void onTouchDown(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
        @WorkerThread
        void onTouchUp(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
        @WorkerThread
        void onTouchCancel(@Nullable World attachedWorld, @NonNull Widget widget);
        @WorkerThread
        void onTouchDrag(@Nullable World attachedWorld, @NonNull Widget widget, int x, int y);
    }
}
