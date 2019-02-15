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
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

public class Graphic2dDrawer {
    AssetManager assets;
    Bitmap mFrameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    public Graphic2dDrawer(AssetManager assets) {
        this.assets = assets;
        this.paint = new Paint();
    }

    void setFrameBuffer(int bufferWidth, int bufferHeight, Bitmap.Config config){

        mFrameBuffer = Bitmap.createBitmap(bufferWidth,
                bufferHeight, config);
        this.canvas = new Canvas(mFrameBuffer);
    }

    public Texture newTexture(String fileName, Texture.Format format) {

        Bitmap.Config config = null;
        if (format == Texture.Format.RGB565)
            config = Bitmap.Config.RGB_565;
        else if (format == Texture.Format.ARGB4444)
            config = Bitmap.Config.ARGB_4444;
        else
            config = Bitmap.Config.ARGB_8888;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = assets.open(fileName);
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
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    public void drawRect(int left, int top, int right, int bottom, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void drawObject(Texture texture, int left, int top, int right, int bottom, float srcLeftOffsetRatio, float srcTopOffsetRatio,
                              float srcWidthRatio, float srcHeightRatio) {

        srcRect.left = (int)(texture.bitmap.getWidth()*srcLeftOffsetRatio);
        srcRect.top = (int)(texture.bitmap.getHeight()*srcTopOffsetRatio);
        srcRect.right = srcRect.left+(int)(texture.bitmap.getWidth()*srcWidthRatio)-1;
        srcRect.bottom = srcRect.top+(int)(texture.bitmap.getHeight()*srcHeightRatio)-1;

        dstRect.left = left;
        dstRect.top = top;
        dstRect.right = right;
        dstRect.bottom = bottom;

        canvas.drawBitmap(texture.bitmap, srcRect, dstRect,null);
    }

    public Bitmap getFrameBuffer() {
        return mFrameBuffer;
    }
}
