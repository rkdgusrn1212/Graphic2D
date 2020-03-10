package com.khgkjg12.graphic2d;

public class PutAttachedWidgetException extends RuntimeException {
    public PutAttachedWidgetException(){
        super("World.putWidget(): Attempt to insert an widget that has already been inserted.");
    }
}
