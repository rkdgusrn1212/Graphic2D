package com.khgkjg12.graphic2d;


abstract class Renderable {

    private java.lang.Object[] mParams;

    Renderable(java.lang.Object... params){
        mParams = params;
    }

    <T> T getParam(int idx, Class<T> c){
        return (T)mParams[idx];
    }

    java.lang.Object getParam(int idx){
        return mParams[idx];
    }

    abstract void render(World world, long deltaTime);
}