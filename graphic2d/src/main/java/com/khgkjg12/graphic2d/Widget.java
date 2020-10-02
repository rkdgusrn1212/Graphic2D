package com.khgkjg12.graphic2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
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
    protected float mRenderX, mRenderY;
    protected ArrayList<Widget.OnTouchListener> mOnTouchListeners = null;
    protected boolean mIsPressed = false;
    protected boolean mConsumeTouchEvent = true;
    protected Widget.ChildListener mChildListener;
    protected World mAttachedWorld = null;
    protected GroupWidget mGroup = null;
    protected int mPressedX, mPressedY;
    protected Widget mLayerHost = null;
    protected ArrayList<Widget> mForegroundWidgets = null;
    protected ArrayList<Widget> mBackgroundWidgets = null;
    protected RectF mOuterBoundary = new RectF();
    protected Bitmap mCacheBitmap = null;
    protected boolean mIgnoreCache = false;//상위 widget에게 cache되는 여부. 본인이 cache하는건 상관없다
    protected boolean mIsCached = false;//cache bitmap 위에 그려진 하위 widget들은 true. 해당 bitmap을 가진 widget은 false다
    protected boolean mHasCacheBitmap = false;

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
        calculateRenderXY();
    }

    @WorkerThread
    void attachedHost(Widget widget){
        mLayerHost = widget;
        if(getAttachedWorld()!=null){
            calculateRenderXY();
        }
    }

    @WorkerThread
    void detachedHost(){
        mLayerHost = null;
    }

    @WorkerThread
    void detached() {
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
        if(mIsCached) throw new RuntimeException("Try to change visibility of cached layer widget");
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
    void render(Canvas canvas, boolean drawOnCache){
        if(isVisible()&&(!drawOnCache||!mIgnoreCache)){
            if (mBackgroundWidgets != null)
                for (int i = mBackgroundWidgets.size() - 1; i >= 0; i--)
                    mBackgroundWidgets.get(i).render(canvas, drawOnCache);
            if(!mIsCached) {
                drawWithCache(canvas);
                mIsCached = drawOnCache;//아직 그려지지 않은 객체를 draw하고 만약 이게 cache에 그리는거면 mIsCached를 true로.
            }
            if (mForegroundWidgets != null)
                for (Widget widget : mForegroundWidgets)
                    widget.render(canvas, drawOnCache);
        }
    }

    void drawWithCache(Canvas canvas){
        if(mHasCacheBitmap){
            canvas.drawBitmap(mCacheBitmap, null, mOuterBoundary, null);
        }else{
            draw(canvas);
        }
    }

    /**
     * 렌더 프레임상 x, y좌표 와 카메라 위치에 따른 스케일.
     */
    @WorkerThread
    abstract protected void draw(Canvas canvas);


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
        World attachedWorld = getAttachedWorld();
        if(attachedWorld!=null) {
            if (z >= mZ) {
                int i = 0;
                while (attachedWorld.mWidgets[i].mZ > z) {
                    i++;
                }
                Widget tempWidget;
                int j = i;
                while (attachedWorld.mWidgets[j] != this) {
                    j++;
                }
                tempWidget = attachedWorld.mWidgets[j];
                while (i != j) {
                    attachedWorld.mWidgets[j] = attachedWorld.mWidgets[j - 1];
                    j--;
                }
                attachedWorld.mWidgets[j] = tempWidget;
            } else {
                int i = attachedWorld.mWidgetCount - 1;
                while (attachedWorld.mWidgets[i].mZ <= z) {
                    i--;
                }
                Widget tempWidget;
                int j = i;
                while (attachedWorld.mWidgets[j] != this) {
                    j--;
                }
                tempWidget = attachedWorld.mWidgets[j];
                while (i != j) {
                    attachedWorld.mWidgets[j] = attachedWorld.mWidgets[j + 1];
                    j++;
                }
                attachedWorld.mWidgets[j] = tempWidget;
            }
        }
        mZ = z;
    }

    @WorkerThread
    public void moveXY(float x, float y){
        mX = x;
        mY = y;
        if(getAttachedWorld()!=null) {
            calculateRenderXY();
        }
    }

    @WorkerThread
    public void moveX(float x){
        mX = x;
        if(getAttachedWorld()!=null) {
            calculateRenderXY();
        }
    }
    @WorkerThread
    public void moveY(float y){
        mY = y;
        if(getAttachedWorld()!=null) {
            calculateRenderXY();
        }
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

    @WorkerThread
    void calculateRenderXY() {
        float tempX = mRenderX;
        float tempY = mRenderY;
        if(mLayerHost==null) {
            mRenderX = mX;
            mRenderY = mY;
        }else {
            mRenderX = mLayerHost.mRenderX + mX;
            mRenderY = mLayerHost.mRenderY + mY;
        }
        float dx = mRenderX-tempX;
        float dy = mRenderY-tempY;
        if(mHasCacheBitmap){
            mOuterBoundary.offset(dx, dy);
        }
        if(mBackgroundWidgets!=null)
            for(Widget widget:mBackgroundWidgets)
                widget.calculateRenderXY();
        if(mForegroundWidgets!=null)
            for(Widget widget:mForegroundWidgets)
                widget.calculateRenderXY();
        calculateAndCheckBoundary();
    }
    /**
     * @exception IndexOutOfBoundsException
     * @param widget
     */
    public void addForegroundLayer(@NonNull Widget widget){
        if(widget.mAttachedWorld!=null) throw new RuntimeException("try to add a layer widget attached world");
        if(mForegroundWidgets==null) mForegroundWidgets = new ArrayList<>();
        if(!mForegroundWidgets.contains(widget)){
            mForegroundWidgets.add(widget);
            widget.attachedHost(this);
        }
    }

    public void removeForegroundLayer(@NonNull Widget widget){
        if(mForegroundWidgets!=null){
            mForegroundWidgets.remove(widget);
            widget.detachedHost();
        }
    }

    public void addBackgroundLayer(@NonNull Widget widget){
        if(widget.mAttachedWorld!=null) throw new RuntimeException("try to add a layer widget attached world");
        if(mBackgroundWidgets==null) mBackgroundWidgets = new ArrayList<>();
        if(!mBackgroundWidgets.contains(widget)){
            mBackgroundWidgets.add(widget);
            widget.attachedHost(this);
        }
    }

    public void removeBackgroundLayer(@NonNull Widget widget){
        if(mBackgroundWidgets!=null){
            mBackgroundWidgets.remove(widget);
            widget.detachedHost();
        }
    }

    abstract void calculateOuterBound();

    void rCalculateOuterBound() {
        calculateOuterBound();
        if (mBackgroundWidgets != null)
            for (Widget widget : mBackgroundWidgets) {
                if (widget.mVisibility && !widget.mIgnoreCache) {
                    widget.rCalculateOuterBound();
                    mOuterBoundary.union(widget.mOuterBoundary);
                }
            }
        if (mForegroundWidgets != null)
            for (Widget widget : mForegroundWidgets) {
                if (widget.mVisibility && !widget.mIgnoreCache) {
                    widget.rCalculateOuterBound();
                    mOuterBoundary.union(widget.mOuterBoundary);
                }
            }
    }

    /**
     * world.put 다음 ignoreCache(선택적) 다음 enableCache 다음 disableCache
     */
    public void enableCache(){
        if(getAttachedWorld()==null) throw new RuntimeException("Do not cache before attached");
        if(mIsCached) throw new RuntimeException("Try to cache cached widget");
        if(mHasCacheBitmap) throw new RuntimeException("Try to call enable cache after enable cache");
        rCalculateOuterBound();
        mCacheBitmap = Bitmap.createBitmap((int)mOuterBoundary.width(), (int)mOuterBoundary.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCacheBitmap);
        canvas.translate(-mOuterBoundary.left,-mOuterBoundary.top);
        render(canvas, true);
        mIsCached = false;
        mHasCacheBitmap = true;
    }

    public void disableCache(){
        if(mHasCacheBitmap&&!mIsCached) {
            mCacheBitmap.recycle();
            mHasCacheBitmap = false;
            rDisableCache();
        }else{
            throw new RuntimeException("try to disable null cache or cached widget");
        }
    }

    void rDisableCache(){
        if(mVisibility&&!mIgnoreCache){
            if(!mHasCacheBitmap) {
                if (mBackgroundWidgets != null)
                    for (int i = mBackgroundWidgets.size() - 1; i >= 0; i--) {
                        mBackgroundWidgets.get(i).rDisableCache();
                    }
                if (mForegroundWidgets != null)
                    for (Widget widget : mForegroundWidgets)
                        widget.rDisableCache();
            }
            mIsCached = false;
        }
    }

    public void setIgnoreCache(boolean ignoreCache){
        if(mIsCached) throw new RuntimeException("Try to change ignoreCache flag of cached layer widget");
        mIgnoreCache = ignoreCache;
    }

    public World getAttachedWorld(){
        if(mLayerHost!=null){
            return mLayerHost.getAttachedWorld();
        }else{
            return mAttachedWorld;
        }
    }

    Snapshot getSnapshot(){
        return new Snapshot();
    }



    class Snapshot{
        private boolean mVisibility;
        private boolean mClickable;
        private ArrayList<Widget.OnClickListener> mOnClickListeners = null;
        private float mZ;
        private float mX, mY;
        private float mRenderX, mRenderY;
        private ArrayList<Widget.OnTouchListener> mOnTouchListeners = null;
        private boolean mIsPressed = false;
        private boolean mConsumeTouchEvent = true;
        private Widget.ChildListener mChildListener;
        private World. mAttachedWorld = null;
        private GroupWidget mGroup = null;
        private int mPressedX, mPressedY;
        private Widget mLayerHost = null;
        private ArrayList<Widget> mForegroundWidgets = null;
        private ArrayList<Widget> mBackgroundWidgets = null;
        private RectF mOuterBoundary = new RectF();
        private Bitmap mCacheBitmap = null;
        private boolean mIgnoreCache = false;//상위 widget에게 cache되는 여부. 본인이 cache하는건 상관없다
        private boolean mIsCached = false;//cache bitmap 위에 그려진 하위 widget들은 true. 해당 bitmap을 가진 widget은 false다
        private boolean mHasCacheBitmap = false;
        private Snapshot(Widget widget){
            mVisibility = widget.mVisibility;
            mClickable = widget.mClickable;
            mOnClickListeners = widget.mOnClickListeners;
            mZ = widget.mZ;
            mX = widget.mX;
            mY = widget.mY;
            mRenderX = widget.mRenderX;
            mRenderY = widget.mRenderY;
            mOnTouchListeners = widget.mOnTouchListeners;
            mIsPressed = widget.mIsPressed;
            mConsumeTouchEvent = widget.mConsumeTouchEvent;
            mChildListener = widget.mChildListener.getSnapshot();
            mAttachedWorld = widget.mAttachedWorld
            protected Widget.ChildListener mChildListener;
            protected World mAttachedWorld = null;
            protected GroupWidget mGroup = null;
            protected int mPressedX, mPressedY;
            protected Widget mLayerHost = null;
            protected ArrayList<Widget> mForegroundWidgets = null;
            protected ArrayList<Widget> mBackgroundWidgets = null;
            protected RectF mOuterBoundary = new RectF();
            protected Bitmap mCacheBitmap = null;
            protected boolean mIgnoreCache = false;//상위 widget에게 cache되는 여부. 본인이 cache하는건 상관없다
            protected boolean mIsCached = false;//cache bitmap 위에 그려진 하위 widget들은 true. 해당 bitmap을 가진 widget은 false다
            protected boolean mHasCacheBitmap = false;
        }
        Widget getInstance(){
            return new Widget();
        }
    }
}
