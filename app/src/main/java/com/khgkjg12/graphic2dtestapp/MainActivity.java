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
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.khgkjg12.graphic2d.CircleWidget;
import com.khgkjg12.graphic2d.GridObject;
import com.khgkjg12.graphic2d.GridWidget;
import com.khgkjg12.graphic2d.GroupObject;
import com.khgkjg12.graphic2d.GroupWidget;
import com.khgkjg12.graphic2d.Object;
import com.khgkjg12.graphic2d.OvalWidget;
import com.khgkjg12.graphic2d.RoundRectObject;
import com.khgkjg12.graphic2d.RoundRectWidget;
import com.khgkjg12.graphic2d.TextObject;
import com.khgkjg12.graphic2d.TextWidget;
import com.khgkjg12.graphic2d.Texture;
import com.khgkjg12.graphic2d.Graphic2dDrawer;
import com.khgkjg12.graphic2d.Graphic2dRenderView;
import com.khgkjg12.graphic2d.Widget;
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

public class MainActivity extends Activity implements Graphic2dRenderView.Renderer, GridWidget.OnClickGridListener, GroupWidget.OnClickChildListener {

    public static Texture background;
    public static Texture blackStone;
    private Graphic2dRenderView renderView;
    private float startTime = 0;
    private boolean flip = false;
    private Widget flipObject = null;
    private boolean pop = false;
    private Widget popObject = null;
    private float timeAcc = 0;
    private int count = 0;
    GridWidget gridObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        renderView = findViewById(R.id.render_view);
        renderView.setRenderer(this);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void startWorld(World world, int viewportWidth, int viewportHeight) {
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
        gridObject = new GridWidget(0,400,viewportHeight-400,800, 800, 8, 8,true, this, true, this);
        world.putWidget(gridObject);
        /*for(int i=0;i<8;i++){
            for(int j=0; j<8; j++) {
                gridObject.putObject(world, new TextObject("lplplp", Typeface.SANS_SERIF, Color.BLACK, 100, 0, 0, 0, true, true), i, j);
            }
        }

        for(int i=0;i<8;i++){
            for(int j=0; j<8; j++) {
                world.putObject(new TextObject("lplplp", Typeface.SANS_SERIF, Color.BLACK, 100, 1, i*100, j*100, true, true));
            }
        }*/
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
        if(count == 100){
            Log.d("frame_rate", 100/timeAcc+"fps");
            count = 0;
            timeAcc = 0;
        }
        if(pop){
            startTime+=deltaTime;
            if(startTime>=2){
                pop = false;
                popObject.moveZ( 0);
                popObject = null;
                startTime = 0;
            }else{
                popObject.moveZ(98*startTime-0.5f*98*startTime*startTime);
            }
        }
    }

    @Override
    public void loadTextures(Graphic2dDrawer graphic2dDrawer){
        background = graphic2dDrawer.newTexture("background.png", Texture.Format.ARGB8888);
        blackStone = graphic2dDrawer.newTexture("black_stone.png", Texture.Format.ARGB8888);
    }

    @Override
    public void resumeWorld(World world,int viewportWidth, int viewportHeight) {
        gridObject.moveXY(viewportWidth-400,viewportHeight-400);
    }

    @Override
    public void onClickGrid(World world, GridWidget gridWidget, Widget widget, int row, int column) {
        CircleWidget rro = new CircleWidget(gridObject.getZ(), gridObject.getColumnX(column), gridObject.getRowY(row), true, true, null, Color.YELLOW, true, 50);
        gridObject.putChild(rro, row, column);
        world.putWidget(new TextWidget(gridObject.getZ(), gridObject.getColumnX(column), gridObject.getRowY(row), true, false, null, Color.BLACK, false, "158", 60, Paint.Align.CENTER,Typeface.SERIF));
        //gridObject.putObject(new TextObject( gridObject.getZ(), gridObject.getColumnX(column),gridObject.getRowY(row), true , true,null, "IgIg", 100, Typeface.SERIF, Color.WHITE), row, column);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            renderView.changeViewPortSize(0, 800 );
        }else{
            renderView.changeViewPortSize(800, 0 );
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        background.dispose();
        blackStone.dispose();
        super.onDestroy();
    }

    @Override
    public void onClickChild(World attachedWorld, GroupWidget groupWidget, Widget widget, int idx) {
        if(gridObject.getRowSize()==4) {
            gridObject.changeGridSize(8,8);
        }else{
            gridObject.changeGridSize(4, 4);
        }
        //pop = true;
        // popObject = gridObject;
    }
}