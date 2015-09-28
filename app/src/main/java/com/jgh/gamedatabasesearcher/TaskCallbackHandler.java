package com.jgh.gamedatabasesearcher;

import java.util.ArrayList;

/**
 * Created by Jon Hough on 9/25/15.
 */
public interface TaskCallbackHandler<T> {

    void onSuccess(ArrayList<T> list);

    void onError();
}
