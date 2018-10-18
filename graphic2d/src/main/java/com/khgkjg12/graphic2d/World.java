package com.khgkjg12.graphic2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {

    private int mWidth, mHeight;
    private float mScale;
    HashMap<String, Object> mObjects;


    World(int width, int height){
        mWidth = width;
        mHeight = height;
        mScale = 1.0f;
    }

    public void putObject(Object obj, String id){
        mObjects.put(id, obj);
    }

    public void removeObject(String id){
        mObjects.remove(id);
    }

    public void setObjectPosition(int x, int y, String id){

    }

    //world를 그리는 메소드
    void render(){

    }

    void onTouch(TouchHandler touchHandler){
        List<TouchHandler.TouchEvent> touchEvents = touchHandler.getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchHandler.TouchEvent event = touchEvents.get(i);
            if(event.type == TouchHandler.TouchEvent.PINCH_TO_ZOOM){
                scale*=event.scale;
                scale = Math.max(1.0f, Math.min(scale, 5.0f));
                isDragging = false;
                isPressed = false;
                break;
            }else if(event.pointer == 0) {
                if(isDragging){
                    if(event.type == TouchHandler.TouchEvent.TOUCH_DRAGGED){
                        int deltaX = (int)((event.x - startX)/scale);
                        int deltaY = (int)((event.y - startY)/scale);
                        viewportX = Math.max(-STONE_WIDTH*STAGE_WIDTH/2, Math.min(viewportX-deltaX,STONE_WIDTH*STAGE_WIDTH/2));
                        viewportY = Math.max(-STONE_HEIGHT*STAGE_HEIGHT/2, Math.min(viewportY-deltaY,STONE_HEIGHT*STAGE_HEIGHT/2));
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
                        int indexX = (int)(((800/2+viewportX)*scale-renderView.getBufferWidth()/2+event.x)/(STONE_WIDTH*scale));
                        int indexY = (int)(((800/2+viewportY)*scale-renderView.getBufferHeight()/2+event.y)/(STONE_HEIGHT*scale));
                        if(indexX<STAGE_WIDTH&&indexY<STAGE_HEIGHT&&indexX>=0&&indexY>=0){
                            onItemClick(indexX, indexY);
                        }
                    }
                    isPressed = false;
                }
            }
        }
    }
}
