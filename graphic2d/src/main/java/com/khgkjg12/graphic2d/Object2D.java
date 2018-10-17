package com.khgkjg12.graphic2d;

import android.graphics.Rect;

public class Object2D {

    Texture mTexture;
    Rect mBoundary;

    public Object2D(Texture texture, Rect boundary) {
        mTexture = texture;
        mBoundary = boundary;
    }


    public void dispose(){
        mTexture.dispose();
    }
}
