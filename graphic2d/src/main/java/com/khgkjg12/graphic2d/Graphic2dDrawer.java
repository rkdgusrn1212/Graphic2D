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
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    public Graphic2dDrawer(AssetManager assets) {
        this.assets = assets;
        this.paint = new Paint();
    }

    void setFrameBuffer(int bufferWidth, int bufferHeight, Bitmap.Config config){

        this.frameBuffer = Bitmap.createBitmap(bufferWidth,
                bufferHeight, config);
        this.canvas = new Canvas(frameBuffer);
    }

    public Graphic2d newGraphic2D(String fileName, Graphic2d.Format format) {

        Bitmap.Config config = null;
        if (format == Graphic2d.Format.RGB565)
            config = Bitmap.Config.RGB_565;
        else if (format == Graphic2d.Format.ARGB4444)
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
            format = Graphic2d.Format.RGB565;
        else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444)
            format = Graphic2d.Format.ARGB4444;
        else
            format = Graphic2d.Format.ARGB8888;

        return new Graphic2d(bitmap, format);
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

    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + width - 1, paint);
    }

    public void drawGraphic2D(Graphic2d graphic2D, int x, int y, int width, int height, int srcX, int srcY,
                              int srcWidth, int srcHeight) {

        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + width - 1;
        dstRect.bottom = y + height - 1;

        canvas.drawBitmap(graphic2D.bitmap, srcRect, dstRect,null);
    }

    public void drawGraphic2D(Graphic2d graphic2D, int x, int y, int srcX, int srcY,
                              int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth - 1;
        dstRect.bottom = y + srcHeight - 1;

        canvas.drawBitmap(graphic2D.bitmap, srcRect, dstRect,null);
    }

    public void drawGraphic2D(Graphic2d graphic2D, int x, int y) {
        canvas.drawBitmap(graphic2D.bitmap, x, y, null);
    }

    public void drawGraphic2D(Graphic2d graphic2D, int x, int y, int width, int height) {
        srcRect.left = 0;
        srcRect.top = 0;
        srcRect.right = graphic2D.getWidth();
        srcRect.bottom = graphic2D.getHeight();

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + width - 1;
        dstRect.bottom = y + height - 1;

        canvas.drawBitmap(graphic2D.bitmap, srcRect, dstRect, null);
    }

    public int getWidth() {
        return frameBuffer.getWidth();
    }

    public int getHeight() {
        return frameBuffer.getHeight();
    }

    public Bitmap getFrameBuffer() {
        return frameBuffer;
    }
}
