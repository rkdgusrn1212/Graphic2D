package com.khgkjg12.graphic2d;

import android.graphics.Color;
import android.support.annotation.NonNull;

public class TextureObject extends RectObject {
    private Texture mTexture;

    public TextureObject(@NonNull Texture texture, int width, int height, int z, int x, int y, String id) {
        this(texture, width, height, z, x, y, 0, 0, id);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, int z, int x, int y, int degreeH, int degreeV, String id) {
        this(texture, width, height, z, x, y, degreeH, degreeV, true, true, id);
    }

    public TextureObject(@NonNull Texture texture, int width, int height, int z, int x, int y, int degreeH, int degreeV, boolean visibility, boolean cilickable, String id){
        super(Color.TRANSPARENT, width, height, z, x, y, degreeH, degreeV, visibility, cilickable, id);
        mTexture = texture;
    }

    public void setTexture(@NonNull Texture texture){
        mTexture = texture;
    }

    @Override
    void render(Graphic2dDrawer drawer, float scale, float renderX, float renderY, float verticalDegree, float horizontalDegree) {
        float width = mWidth * Math.abs((float) Math.cos(mHoriaontalDegree * Math.PI / 180));
        float height = mHeight * Math.abs((float) Math.cos(mVerticalDegree * Math.PI / 180));
        float left = renderX - width / 2;
        float top = renderY - height / 2;
        float right = left + width * scale;
        float bottom = top + height * scale;
        drawer.drawObject(mTexture, left, top, right, bottom);
    }
}
