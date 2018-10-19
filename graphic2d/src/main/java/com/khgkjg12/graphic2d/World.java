package com.khgkjg12.graphic2d;

import android.graphics.Color;

import java.util.HashMap;
import java.util.List;

public class World {

    private int mWidth, mHeight;//0은 무한.
    private float mScale = 1.0f;;
    HashMap<String, Object> mObjects;
    boolean isDragging = false;
    int startX, startY;
    boolean isPressed = false;
    private int viewportX = 0;
    private int viewportY = 0;
    private int mFrameBufferWidth;
    private int mFrameBufferHeight;

    World(int width, int height){
        mWidth = width;
        mHeight = height;
        mObjects = new HashMap<>();
    }

    public void putObject(Object obj){
        mObjects.put(obj.mId, obj);
    }

    public void removeObject(String id){
        mObjects.remove(id);
    }

    void setWorldSize(int frameBufferWidth, int frameBufferHeight){
        mFrameBufferWidth = frameBufferWidth;
        mFrameBufferHeight = frameBufferHeight;
    }

    void render(Graphic2dDrawer drawer){
        drawer.clear(Color.BLACK);
        for(Object object: mObjects.values()){
            object.render(drawer, mFrameBufferWidth, mFrameBufferHeight, mWidth, mHeight, mScale, viewportX, viewportY);
        }
    }

    void onTouch(TouchHandler touchHandler){
        List<TouchHandler.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchHandler.TouchEvent event = touchEvents.get(i);
            if(event.type == TouchHandler.TouchEvent.PINCH_TO_ZOOM){
                mScale*=event.scale;
                mScale = Math.max(1.0f, Math.min(mScale, 5.0f));
                isDragging = false;
                isPressed = false;
                break;
            }else if(event.pointer == 0) {
                if(isDragging){
                    if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                        int deltaX = (int)((event.x - startX)/mScale);
                        int deltaY = (int)((event.y - startY)/mScale);
                        if(mWidth==0){
                            viewportX=viewportX-deltaX;
                        }else{
                            viewportX = Math.max(-mWidth/2, Math.min(viewportX-deltaX,mWidth/2));
                        }
                        if(mHeight ==0){
                            viewportY=viewportY-deltaY;
                        }else{
                            viewportY = Math.max(-mHeight/2, Math.min(viewportY-deltaY,mHeight/2));
                        }
                        startX = event.x;
                        startY = event.y;
                    }else{
                        isDragging = false;
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DOWN) {
                    startX = event.x;
                    startY = event.y;
                    isPressed = true;
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                    if(isPressed) {
                        if (Math.abs(event.x - startX) > 50 || Math.abs(event.y - startY) > 50) {
                            isDragging = true;
                        }
                    }
                }else if(event.type == TouchHandler.TouchEvent.TOUCH_UP) {
                    if(isPressed){
                        for(Object object : mObjects.values()){
                            object.onTouch((int)(viewportX+(event.x-mFrameBufferWidth/2)/mScale), (int)(viewportY+(event.y-mFrameBufferHeight/2)/mScale));
                        }
                    }
                    isPressed = false;
                }
            }
        }
    }
}