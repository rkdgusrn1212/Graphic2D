package com.khgkjg12.graphic2d;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.InputStream;

//Immutable class
class SynchronizedBitmap {
    private Bitmap mBitmap = null;
    private boolean isLoaded = false;
    private boolean isRecycled = false;

    /**
     * 매개변수로 넘긴 비트맵을 적어도 메소드 종료할때까지는 다른 스레드에서 접근하면 안됨.
     * @param bitmap
     * @return
     */
    @WorkerThread
    synchronized boolean loadFromBitmap(Bitmap bitmap){
        if(isLoaded){
            return false;
        }
        //호출자와 같은 쓰래드에서 수행되기에 호출자 이외의 쓰래드에서 비트맵 접근권을가지고 비트맵을 동시에 수정하려고 하는경우만 고려함.
        if(mBitmap.isRecycled()){
            return false;
        }
        mBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());//혹여나 다른 스레드에서 비트맵을 도중에 recycle시키면 runtime exception 발생가능.
        isLoaded = true;
        return true;
    }

    @WorkerThread
    synchronized boolean loadFromAsset(AssetManager assetManager, String fileName, Bitmap.Config config) {
        if(isLoaded){
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        try {
            in = assetManager.open(fileName);
            mBitmap = BitmapFactory.decodeStream(in);
            if (mBitmap == null)
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
        return true;
    }

    synchronized boolean recycle(){
        if(!isLoaded){
            return false;
        }
        if(isRecycled){
            return false;
        }
        mBitmap.recycle();
        isRecycled = true;
        return true;
    }

    synchronized boolean isRecycled(){
        return isRecycled;
    }

    synchronized boolean isLoaded(){
        return isLoaded;
    }
}
