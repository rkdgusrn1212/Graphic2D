package com.khgkjg12.graphic2dtestapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import com.khgkjg12.graphic2d.Texture;
import com.khgkjg12.graphic2d.Graphic2dDrawer;
import com.khgkjg12.graphic2d.Graphic2dRenderView;
import com.khgkjg12.graphic2d.TouchHandler;

import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * 테스트 어플은 다음과 같이 작동한다.
 * 2D오브젝트 출력하기: 배경이미지, 검은 돌과 하얀돌을 임의의 위치에 출력.
 * 2D오브젝트의 애니메이션: 2D 오브젝트가 클릭시 회전, 길게 클릭시 올라갔다 내려옴.
 * 2DWorld의 확대 축소 및 초점 이동 기능: pinch to scale 구현. draw to move 구현
 * Grid 좌표 시스템 구현.
 * 2D오브젝트의 클릭: 검은 돌과 하얀돌을 클릭하면 해당 객체에 지정된 클릭 리스너가 실행됨. 클릭시 효과음도 설정;
 */

public class MainActivity extends Activity implements Graphic2dRenderView.Renderer {

    private final static int STAGE_WIDTH = 8;
    private final static int STAGE_HEIGHT = 8;
    public static Texture background;
    public static Texture blackStone;
    private Graphic2dRenderView renderView;
    private final int STONE_WIDTH = 100;
    private final int STONE_HEIGHT = 100;
    private int viewportX = 0;
    private int viewportY = 0;
    private float scale = 1.0f;
    boolean isDragging = false;
    int startX, startY;
    boolean isPressed = false;

    private byte[][] mOccupancyList = new byte[STAGE_HEIGHT][STAGE_WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        renderView = findViewById(R.id.render_view);
        renderView.setRenderer(this);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void loadGraphics(Graphic2dDrawer graphic2dDrawer){
        background = graphic2dDrawer.newGraphic2D("background.png", Texture.Format.ARGB8888);
        blackStone = graphic2dDrawer.newGraphic2D("black_stone.png", Texture.Format.ARGB8888);
    }

    @Override
    public void update(float deltaTime, Graphic2dDrawer drawer, TouchHandler input) {
        drawer.clear(Color.BLACK);

        drawBoard(drawer);

        List<TouchHandler.TouchEvent> touchEvents = input.getTouchEvents();
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

    private void onItemClick(int x, int y){
        mOccupancyList[y][x] = 0x01;
    }

    private void drawBoard(Graphic2dDrawer drawer){
        int centerX = renderView.getBufferWidth()/2-(int)(viewportX*scale);
        int centerY = renderView.getBufferHeight()/2-(int)(viewportY*scale);
        int boardWidth = (int)(STAGE_WIDTH*STONE_WIDTH*scale);
        int boardHeight = (int)(STAGE_HEIGHT*STONE_HEIGHT*scale);
        int stoneWidth = (int)(STONE_WIDTH*scale);
        int stoneHeight = (int)(STONE_HEIGHT*scale);

        drawer.drawGraphic2D(background,centerX-boardWidth/2, centerY-boardHeight/2, boardWidth, boardHeight);

        for(int i =0; i< STAGE_HEIGHT; i++){
            for(int j=0; j<STAGE_WIDTH; j++){
                if(mOccupancyList[i][j] == 0x01) {
                    drawer.drawGraphic2D(blackStone, centerX - boardWidth / 2 + j * stoneWidth, centerY - boardHeight / 2 + i * stoneHeight, (int) (STONE_WIDTH * scale), (int) (STONE_HEIGHT * scale));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        renderView.pause();
    }
}