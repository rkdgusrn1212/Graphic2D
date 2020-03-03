package com.khgkjg12.graphic2d;

public class RemoveChildFromWorldException extends RuntimeException {
    public RemoveChildFromWorldException(){
        super("World.removeObject(): Attempt to delete a child object of a group from the world.");
    }
}
