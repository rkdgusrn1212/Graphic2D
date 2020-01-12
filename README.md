# Graphic2D
Android 2D Graphic Framework.

# Usage
Write .xml

    <com.khgkjg12.graphic2d.Graphic2dRenderView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:viewportWidth:800
        app:viewportHeight:800
        app:worldWidth:0 //0 is infinite
        app:worldHeight:0 //center of the world is (0,0)
        app:viewportX:0
        app:viewportY:0 //viewportX,Y are restricted to the inside of the world's border.
        app:cameraZ:100
        app:focusedZ:100
        app:maxCameraZ:100
        app:minCameraZ:20
        app:pinchToZoom:true //default true.
        app:dragToMove:true //default true.
        android:layout_gravity="center"
        android:id="@+id/render_view"/>
 
 Set Renderer
 
     Graphic2dRenderView renderView = findViewById(R.id.render_view);
     renderView.setRenderer(new Graphic2dRenderView.Renderer());
     
Texture Lifecycle
    
    Renderer.loadTexture(Graphic2dDrawer drawer){
        //load Texture with drawer. Textures are member of the Activity.
    }
    
    Activity.onDestroy(){
        //Texture.dispose();
    }
    
Handle Click Events
    
    Object.setOnClickListener(new OnClickListener(){});
    GridObject.setOnItemClickListener(new OnClickItemListener(){});

Change Viewport Size


    renderView.changeViewPortSize(width, height);

    //needed when screen size or view size changes
    //do not call with params 0: 0 -> automatically 300:300
    //0 is auto fit.
    //only works when onMeasure() is called.

# Gradle Dependency
    implementation 'com.khgkjg12.graphic2d:graphic2d:1.2.1'

# License
    Copyright 2018 Hyungu Kang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# Contact
    khgkjg12@naver.com, please mail me!
