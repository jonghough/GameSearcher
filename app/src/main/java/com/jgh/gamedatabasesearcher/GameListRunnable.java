package com.jgh.gamedatabasesearcher;

import android.util.Log;

import com.jgh.gamedatabasesearcher.models.models.game.Game;
import com.jgh.gamedatabasesearcher.utils.ApiUtils;
import com.jgh.gamedatabasesearcher.xml.XmlGameHandler;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class GameListRunnable implements Runnable {

        private static final String TAG = "GameListRunnable";
        private String mGameName;
        private TaskCallbackHandler<GameDataInfo> mTaskCallbackHandler;
        public GameListRunnable(String gameName, TaskCallbackHandler<GameDataInfo> tch) {
            mGameName = gameName;
            mTaskCallbackHandler = tch;
        }

        @Override
        public void run() {

            query(ApiUtils.API_BASE + ApiUtils.API_NAME_SEARCH + mGameName).flatMap(new Func1<String, Observable<Game>>() {
                @Override
                public Observable<Game> call(String s) {
                    try {
                        List<Game> games = new XmlGameHandler().handleData(s);
                        return Observable.from(games);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception getting game data " + e.getMessage());
                        return null;
                    }
                }
            }).flatMap(new Func1<Game, Observable<GameDataInfo>>() {

                @Override
                public Observable<GameDataInfo> call(Game game) {
                    GameDataInfo gdi = new GameDataInfo(game);
                    return Observable.from(new GameDataInfo[]{gdi});
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<GameDataInfo>() {
                        ArrayList<GameDataInfo> gameList = new ArrayList<GameDataInfo>();

                        @Override
                        public void onCompleted() {

                            if (gameList == null) {
                                mTaskCallbackHandler.onError();
                            } else {
                                mTaskCallbackHandler.onSuccess(gameList);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Error building game list. " + e.getMessage());
                            mTaskCallbackHandler.onError();
                        }

                        @Override
                        public void onNext(GameDataInfo gameDataInfo) {
                            gameList.add(gameDataInfo);
                        }
                    });
        }


    /**
     * Queries the API for Games list for given URL.
     * @param url
     * @return
     */
    private Observable<String> query(String url) {
        List<String> s = new ArrayList<String>();
        s.add(new ApiController().callApi(url));

        return Observable.from(s);
    }

}
