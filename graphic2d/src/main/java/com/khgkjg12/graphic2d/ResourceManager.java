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
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ResourceManager {

    private static ResourceManager mInstance = null;

    public static ResourceManager getInstance(){
        if(mInstance==null){
            mInstance = new ResourceManager();
        }
        return mInstance;
    }

    private Map<String, SynchronizedBitmap> mBitmapMap = new HashMap<>();

    @WorkerThread
    public synchronized boolean putBitmap(@NonNull Bitmap bitmap, @NonNull String key) {
        SynchronizedBitmap synchronizedBitmap = new SynchronizedBitmap();
        if(!synchronizedBitmap.loadFromBitmap(bitmap)){
            return false;
        }
        return putBitmap(synchronizedBitmap, key);
    }

    @WorkerThread
    public synchronized boolean putBitmap(AssetManager assetManager, String fileName, Bitmap.Config config, @NonNull String key){
        SynchronizedBitmap synchronizedBitmap = new SynchronizedBitmap();
        if(!synchronizedBitmap.loadFromAsset(assetManager, fileName, config)){
            return false;
        }
        return putBitmap(synchronizedBitmap, key);
    }

    @WorkerThread
    private boolean putBitmap(SynchronizedBitmap synchronizedBitmap, String key){
        if(key.isEmpty()){
            synchronizedBitmap.recycle();
            return false;
        }
        if(mBitmapMap.containsKey(key)){
            synchronizedBitmap.recycle();
            return false;
        }
        mBitmapMap.put(key, synchronizedBitmap);
        return true;
    }

    @MainThread
    public void recycleBitmap(String key){
        mBitmapMap.get(key).recycle();
    }
}
