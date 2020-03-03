package com.khgkjg12.graphic2d;

public class PutAttachedObjectException extends RuntimeException {
    public PutAttachedObjectException(){
        super("World.putObject(): Attempt to insert an object that has already been inserted.");
    }
}
