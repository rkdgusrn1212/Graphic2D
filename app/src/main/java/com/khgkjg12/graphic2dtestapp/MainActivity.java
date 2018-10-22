package com.khgkjg12.graphic2dtestapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.khgkjg12.graphic2d.GridObject;
import com.khgkjg12.graphic2d.Object;
import com.khgkjg12.graphic2d.Texture;
import com.khgkjg12.graphic2d.Graphic2dDrawer;
import com.khgkjg12.graphic2d.Graphic2dRenderView;
import com.khgkjg12.graphic2d.TouchHandler;
import com.khgkjg12.graphic2d.World;

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

public class MainActivity extends Activity implements Graphic2dRenderView.Renderer, GridObject.OnClickItemListener {

    public static Texture background;
    public static Texture blackStone;
    private Graphic2dRenderView renderView;
    private float startTime = 0;
    private boolean flip = false;
    private Object flipObject = null;
    private boolean pop = false;
    private Object popObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        renderView = findViewById(R.id.render_view);
        renderView.setRenderer(this);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void prepareWorld(World world) {
        GridObject gridObject = new GridObject(background, 800, 800, 8, 8, "board");
        gridObject.setPosition(0, 0);
        gridObject.setOnClickItemListener(this);
        world.putObject(gridObject);
    }

    @Override
    public void updateWorld(float deltaTime, World world) {
        /*if(flip){
            startTime+=deltaTime;
            flipObject.setHorizontalFlip((int)(startTime*90));
            if(startTime>=2){
                flip = false;
                flipObject = null;
                startTime = 0;
            }
        }*/
        if(pop){
            startTime+=deltaTime;
            if(startTime>=2){
                pop = false;
                popObject.setZ(0);
                popObject = null;
                startTime = 0;
            }else{
                popObject.setZ(980*startTime-0.5f*980*startTime*startTime);
            }
        }
    }

    @Override
    public void loadTextures(Graphic2dDrawer graphic2dDrawer){
        background = graphic2dDrawer.newGraphic2D("background.png", Texture.Format.ARGB8888);
        blackStone = graphic2dDrawer.newGraphic2D("black_stone.png", Texture.Format.ARGB8888);
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

    @Override
    public void onClickItem(GridObject gridObject, Object[][] objectList, int row, int column) {

        if(objectList[row][column]==null){
            gridObject.putObject(blackStone, row, column);
        }else{
            pop = true;
            popObject = objectList[row][column];
            //flip = true;
            //flipObject = objectList[row][column];
        }
    }

    @Override
    protected void onDestroy() {
        background.dispose();
        blackStone.dispose();
        super.onDestroy();
    }
}