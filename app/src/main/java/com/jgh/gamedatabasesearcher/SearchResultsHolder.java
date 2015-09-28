package com.jgh.gamedatabasesearcher;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

/**
 * Singleton object, for holding search result data to be passed
 * between activities etc.
 *
 * Created by Jon Hough on 9/24/15.
 */
public final class SearchResultsHolder {

    private static final String TAG = "SearchResultsHolder";

    private static SearchResultsHolder sInstance;

    private ArrayList<? extends GameDataInfo> mList = new ArrayList<>();

    private String mSearchWords = null;

    private SearchResultsHolder(){
    }

    /**
     * Returns the instance of the singleton.
     * @return
     */
    public synchronized static SearchResultsHolder getInstance(){
        //synchronized(sInstance) {
            if (sInstance == null) {
                sInstance = new SearchResultsHolder();
            }
            return sInstance;
        //}
    }

    public synchronized void setDataList(ArrayList<? extends GameDataInfo> lst){
        mList = new ArrayList<>(lst);
    }

    public synchronized void setSearchWords(String searchW){
        mSearchWords = searchW;
    }

    public synchronized String getSearchWords(){
        return mSearchWords;
    }

    public synchronized ArrayList<? extends GameDataInfo> getDataList(){
        return mList;
    }

    /**
     * Filters the current search result data list.
     * @param filter
     * @param filterObserver
     */
    public synchronized void getFilteredList(Func1<GameDataInfo, Boolean> filter, Observer<GameDataInfo> filterObserver ){
        if(mList == null) return;
        Observable.from(mList).filter(filter).subscribe(filterObserver);
    }

}
