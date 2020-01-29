/**
 * Copyright 2018 Hyungu Kang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.khgkjg12.graphic2dtestapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.khgkjg12.graphic2d.GridObject;
import com.khgkjg12.graphic2d.Object;
import com.khgkjg12.graphic2d.RoundRectObject;
import com.khgkjg12.graphic2d.TextObject;
import com.khgkjg12.graphic2d.Texture;
import com.khgkjg12.graphic2d.Graphic2dDrawer;
import com.khgkjg12.graphic2d.Graphic2dRenderView;
import com.khgkjg12.graphic2d.TextureGridObject;
import com.khgkjg12.graphic2d.TextureObject;
import com.khgkjg12.graphic2d.World;

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
    private float timeAcc = 0;
    private int count = 0;

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
        /*for(int i=0; i< 9; i++) {
            Object verLine = new Object(Color.parseColor(i%3==0?"#021aee":"#d602ee"), 1, 801, "vertical_line_"+i);
            verLine.setPosition(-400+100*i,0);
            if(i%3!=0){
                verLine.setZ(+0.01f);
            }
            world.putObject(verLine);
        }
        for(int i=0; i< 9; i++) {
            Object horLine = new Object(Color.parseColor(i%3==0?"#021aee":"#d602ee"), 801, 1, "horizontal_line_"+i);
            horLine.setPosition(0,-400 + 100 * i);
            if(i%3!=0){
                horLine.setZ(+0.01f);
            }
            world.putObject(horLine);
        }*/
        TextureGridObject gridObject = new TextureGridObject(background, 800, 800, 8, 8, 0, 0, 0, "board");
        gridObject.setPosition(0, 0);
        gridObject.setOnClickItemListener(this);
        world.putObject(gridObject);
        world.putObject(new TextObject("lplplp", Color.BLACK, 100, 0.1f, 0, 0, true, true, null));
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
        count++;
        timeAcc += deltaTime;
        if(count == 50){
            Log.d("frame_rate", 50/timeAcc+"fps");
            count = 0;
            timeAcc = 0;
        }
        if(pop){
            startTime+=deltaTime;
            if(startTime>=2){
                pop = false;
                popObject.setZ(0);
                popObject = null;
                startTime = 0;
            }else{
                popObject.setZ(98*startTime-0.5f*98*startTime*startTime);
            }
        }
    }

    @Override
    public void loadTextures(Graphic2dDrawer graphic2dDrawer){
        background = graphic2dDrawer.newTexture("background.png", Texture.Format.ARGB8888);
        blackStone = graphic2dDrawer.newTexture("black_stone.png", Texture.Format.ARGB8888);
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
            gridObject.putObject(new TextObject("IgIg", Color.BLACK, 100, 0, 100, 100, null ), row, column);
        }else{
            pop = true;
            popObject = objectList[row][column];
            //flip = true;
            //flipObject = objectList[row][column];
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            renderView.changeViewPortSize(0, 801 );
        }else{
            renderView.changeViewPortSize(801, 0 );
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        background.dispose();
        blackStone.dispose();
        super.onDestroy();
    }
}