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
package com.khgkjg12.graphic2d;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.IOException;
import java.io.InputStream;

public class Graphic2dDrawer {
    AssetManager mAssets;
    Bitmap mFrameBuffer;
    Canvas mCanvas;
    Paint mPaint;

    public Graphic2dDrawer(AssetManager assets) {
        this.mAssets = assets;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    void setFrameBuffer(int bufferWidth, int bufferHeight, Bitmap.Config config){

        if(mFrameBuffer!=null){
            Bitmap tempBitmap =  mFrameBuffer;
            mFrameBuffer = Bitmap.createBitmap(bufferWidth, bufferHeight, config);
            tempBitmap.recycle();
            this.mCanvas.setBitmap(mFrameBuffer);
        }else {
            mFrameBuffer = Bitmap.createBitmap(bufferWidth,
                    bufferHeight, config);
            this.mCanvas = new Canvas(mFrameBuffer);
        }
    }

    public Texture newTexture(String fileName, Texture.Format format) {

        Bitmap.Config config;
        if (format == Texture.Format.RGB565)
            config = Bitmap.Config.RGB_565;
        else if (format == Texture.Format.ARGB4444)
            config = Bitmap.Config.ARGB_4444;
        else
            config = Bitmap.Config.ARGB_8888;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap;
        try {
            in = mAssets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        if (bitmap.getConfig() == Bitmap.Config.RGB_565)
            format = Texture.Format.RGB565;
        else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444)
            format = Texture.Format.ARGB4444;
        else
            format = Texture.Format.ARGB8888;

        return new Texture(bitmap, format);
    }

    public void clear(int color) {
        mCanvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    public void drawPoint(float x, float y, float width, int color) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        mCanvas.drawPoint(x, y, mPaint);
    }

    public void drawLine(float x, float y, float x2, float y2, float width, int color) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        mCanvas.drawLine(x, y, x2, y2, mPaint);
    }

    public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, int color){
        mPaint.setColor(color);
        mCanvas.drawRoundRect(new RectF(left, top, right, bottom), rx, ry, mPaint);
    }

    public void drawRect(float left, float top, float right, float bottom, int color) {
        mPaint.setColor(color);
        mCanvas.drawRect(left, top, right, bottom, mPaint);
    }

    public void drawTexture(Texture texture, RectF rectF) {
        mCanvas.drawBitmap(texture.bitmap, null, rectF,null);
    }
}
