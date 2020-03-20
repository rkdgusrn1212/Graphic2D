package com.khgkjg12.graphic2d;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;

public abstract class Widget {

    protected boolean mVisibility;
    protected boolean mClickable;
    protected ArrayList<Widget.OnClickListener> mOnClickListeners = null;
    protected float mZ;
    protected float mX, mY;
    protected ArrayList<Widget.OnTouchListener> mOnTouchListeners = null;
    protected boolean mIsPressed = false;
    protected boolean mConsumeTouchEvent = true;
    protected Widget.ChildListener mChildListener;
    protected World mAttachedWorld = null;
    protected GroupWidget mGroup = null;
    protected int mPressedX, mPressedY;

    @WorkerThread
    public Widget(){
        mZ =0;
        mX =0;
        mY =0;
        mVisibility = true;
        mClickable = true;
    }

    @WorkerThread
    public Widget(float z, float x, float y, boolean visibility, boolean clickable) {
        mVisibility = visibility;
        mClickable = clickable;
        mZ = z;
        mX = x;
        mY = y;
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

    @WorkerThread
    public boolean getVisibility(){
        return mVisibility;
    }

    @WorkerThread
    public boolean isVisible(){
        return mVisibility&&(mGroup==null||mGroup.isVisible());
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
    public boolean getClickable(){
        return mClickable;
    }

    @WorkerThread
    public void onClick(){
        if(mOnClickListeners!=null) {
            int size = mOnClickListeners.size();
            for(int i=0;i<size;i++) mOnClickListeners.get(i).onClick(mAttachedWorld, this);
        }
        if(mChildListener!=null) mChildListener.onClick(mAttachedWorld, this);
    }

    /**
     * 경계선 채크 메소드.
     * @return x, y 가 경계선 안에 있으면 참.
     */
    @WorkerThread
    abstract boolean checkBoundary(int x, int y);


    @WorkerThread
    void calculateAndCheckBoundary(){
        calculateBoundary();
        if(mIsPressed&&!checkBoundary(mPressedX, mPressedY)){
            mIsPressed = false;
            onTouchCancel();
        }
    }

    @WorkerThread
    public void addOnClickListener(@NonNull OnClickListener onClickListener){
        if(mOnClickListeners==null) {
            mOnClickListeners = new ArrayList<>();
        }
        if(!mOnClickListeners.contains(onClickListener)) {
            mOnClickListeners.add(onClickListener);
        }
    }

    @WorkerThread
    public void removeOnClickListener(@NonNull OnClickListener onClickListener){
        if(mOnClickListeners!=null) mOnClickListeners.remove(onClickListener);
    }

    @WorkerThread
    public void addOnTouchListener(@NonNull OnTouchListener onTouchListener){
        if(mOnTouchListeners==null) {
            mOnTouchListeners = new ArrayList<>();
        }
        if(!mOnTouchListeners.contains(onTouchListener)) {
            mOnTouchListeners.add(onTouchListener);
        }
    }

    @WorkerThread
    public void removeOnTouchListener(@NonNull OnClickListener onTouchListener){
        if(mOnTouchListeners!=null) mOnTouchListeners.remove(onTouchListener);
    }
    @WorkerThread
    void render(Graphic2dDrawer drawer){
        if(isVisible()){
            draw(drawer);
        }
    }

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     */
    @WorkerThread
    abstract protected void draw(Graphic2dDrawer drawer);


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
        calculateAndCheckBoundary();
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
            mPressedX = x;
            mPressedY = y;
            onTouchDown(x, y);
            return mConsumeTouchEvent;
        }else{
            return false;
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
    void checkDrag(int x, int y){
        if(mIsPressed){
            if(checkBoundary(x, y)){
                mPressedX = x;
                mPressedY = y;
                onTouchDrag(x, y);
            }else{
                mIsPressed = false;
                onTouchCancel();
            }
        }
    }

    @WorkerThread
    public void onTouchDown(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchDown(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchDown(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchDrag(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchDrag(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchDrag(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchUp(int x, int y){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchUp(mAttachedWorld, this, x, y);
        }
        if(mChildListener!=null) mChildListener.onTouchUp(mAttachedWorld, this, x, y);
    }

    @WorkerThread
    public void onTouchCancel(){
        if(mOnTouchListeners!=null) {
            int size = mOnTouchListeners.size();
            for (int i = 0; i <size; i++) mOnTouchListeners.get(i).onTouchCancel(mAttachedWorld, this);
        }
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
