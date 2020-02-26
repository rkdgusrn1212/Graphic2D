package com.khgkjg12.graphic2d;

import android.support.annotation.WorkerThread;

interface Group {
    @WorkerThread
    void attached(World world);
    @WorkerThread
    void detached(World world);
}
