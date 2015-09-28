package com.jgh.gamedatabasesearcher;

import android.util.Log;

import com.jgh.gamedatabasesearcher.xml.XmlArtHandler;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Runnable for downloaidng image url/s for a given game.
 * Created by Jon Hough on 9/28/15.
 */
public class ImageRunnable implements Runnable {

    private static final String TAG = "ImageRunnable";

    private TaskCallbackHandler<String> mTaskCallbackHandler;
    private String mUrl;

    public ImageRunnable(String Url, TaskCallbackHandler<String> taskCallbackHandler) {
        mTaskCallbackHandler = taskCallbackHandler;
        mUrl = Url;

    }

    @Override
    public void run() {
        query(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    List<String> results = new ArrayList<String>();

                    @Override
                    public void onCompleted() {
                        mTaskCallbackHandler.onSuccess(new ArrayList<String>(results));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                        mTaskCallbackHandler.onError();
                    }

                    @Override
                    public void onNext(String s) {
                        try {
                            results = new XmlArtHandler().handleData(s);
                        }catch(Exception ex){
                            //Leave it.
                            //Empty list will be sent to caller, no problem.
                            Log.e(TAG,"exception "+ex.getMessage());
                        }
                    }
                });
    }

    public Observable<String> query(String url) {
        List<String> s = new ArrayList<String>();
        s.add(new ApiController().callApi(url));

        return Observable.from(s);
    }
}
