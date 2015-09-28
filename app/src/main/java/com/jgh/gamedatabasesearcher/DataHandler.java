package com.jgh.gamedatabasesearcher;

/**
 * Created by Jon Hough on 9/25/15.
 */
public interface DataHandler<T> {

    T handleData(String s) throws Exception;
}
