package com.jgh.gamedatabasesearcher;

import android.util.Log;


import com.jgh.gamedatabasesearcher.models.models.platform.Platform;
import com.jgh.gamedatabasesearcher.utils.ApiUtils;
import com.jgh.gamedatabasesearcher.xml.XmlPlatformHandler;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class PlatformRunnable implements Runnable {
    private static final String TAG = "PlatformRunnable";

    private TaskCallbackHandler<Platform> mTaskCallbackHandler;

    public PlatformRunnable(TaskCallbackHandler<Platform> taskCallbackHandler){
        mTaskCallbackHandler = taskCallbackHandler;
    }
    @Override
    public void run() {
        final String url = ApiUtils.PLATFORM_API;
        List<String> lst = new ArrayList<String>();
        lst.add(new ApiController().callApi(url));

        rx.Observable.from(lst).flatMap(new Func1<String, rx.Observable<Platform>>() {
            @Override
            public rx.Observable<Platform> call(String s) {

                try {
                    List<Platform> platforms = new XmlPlatformHandler().handleData(s);

                    return Observable.from(platforms);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).subscribe(new Observer<Platform>() {

            final ArrayList<Platform> platformList = new ArrayList<Platform>();

            @Override
            public void onCompleted() {
                if(platformList == null || platformList.isEmpty())
                    mTaskCallbackHandler.onError();
                else
                    mTaskCallbackHandler.onSuccess(platformList);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error getting platform list: " + e.getMessage());
                mTaskCallbackHandler.onError();
            }

            @Override
            public void onNext(Platform platform) {
                platformList.add(platform);
            }
        });
    }
}
