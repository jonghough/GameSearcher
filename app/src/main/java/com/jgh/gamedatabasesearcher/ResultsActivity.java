package com.jgh.gamedatabasesearcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jgh.gamedatabasesearcher.adapters.GamesAdapter;
import com.jgh.gamedatabasesearcher.models.models.platform.Platform;

import java.util.ArrayList;
import java.util.Calendar;

import rx.Observer;
import rx.functions.Func1;

/**
 * Created by Jon Hough on 9/24/15.
 */
public class ResultsActivity extends Activity {

    private static final String TAG = "ResultsActivity";
    private TextView mTitle, mSearchTitle;
    private Button mNoFilterBtn, mReleaseBtn;
    private ListView mListView;
    private Spinner mSpinner;
    private GamesAdapter mGamesAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_results);
        mTitle = (TextView) findViewById(R.id.result_title_textView);
        mSearchTitle = (TextView) findViewById(R.id.result_search_textView);
        mListView = (ListView) findViewById(R.id.result_listView);
        mSpinner = (Spinner) findViewById(R.id.platform_spinner);

        //Set the search title and typeface.
        mSearchTitle.setText(mSearchTitle.getText() + SearchResultsHolder.getInstance().getSearchWords());
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "Unique.ttf");
        mSearchTitle.setTypeface(typeFace);

        //no filter button
        mNoFilterBtn = (Button)findViewById(R.id.reset_button);
        mNoFilterBtn.setTypeface(typeFace);

        mNoFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LoadListView();
            }
        });

        //the filter button
        mReleaseBtn = (Button)findViewById(R.id.filter_release_button);
        mReleaseBtn.setTypeface(typeFace);

        mReleaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the current date as default datepicker values
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(ResultsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = year+"/"+monthOfYear+"/"+dayOfMonth;
                        getGameForReleaseData(date);
                    }
                }, year, month, day).show();
            }
        });



        // Must get the platform list. Once platform list is loaded, can load the ListView with
        // GamesAdapter and all the data. If platform list fails to load, need to return to previous
        // activity.
        PlatformRunnable platformRunnable = new PlatformRunnable(new TaskCallbackHandler<Platform>() {
            @Override
            public void onSuccess(ArrayList<Platform> list) {
                final ArrayList<Platform> l = list;
                Log.d(TAG, "size of list " + list.size());
                ResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadListView();
                        setupPlatformSpinner(l);

                    }
                });
            }

            @Override
            public void onError() {
                Log.e(TAG, "Could not get platform list");

                // Error indicates platform list could not be downloaded,
                // meaning we need to fallback to previous actvitiy, with an error message.
                ResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(ResultsActivity.this)
                                .setTitle("Error loading search results")
                                .setMessage("List of games could not be loaded. Return to previous screen.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

            }
        });

        new Thread(platformRunnable).start();

    }


    @Override
    public void onResume(){
        super.onResume();
        if(SearchResultsHolder.getInstance().getDataList() == null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    /**
     * Sets up the spinner with the list of platform names. Also adds
     * an extra "All" item to the list to filter for all platforms.
     * @param list
     */
    private void setupPlatformSpinner(ArrayList<Platform> list){
        ArrayList<String> platformNames = new ArrayList<String>();
        platformNames.add("All");

        for(Platform p : list){
            platformNames.add(p.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, platformNames);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView)view).getText().toString();
                getGameForPlatform(name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Loads listview with games adapter.
     */
    private void LoadListView(){
        mGamesAdapter = new GamesAdapter((ArrayList<GameDataInfo>) SearchResultsHolder.getInstance().getDataList(), this);
        mListView.setAdapter(mGamesAdapter);
        mGamesAdapter.notifyDataSetChanged();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {

                    if (mGamesAdapter != null) {
                        mGamesAdapter.setState(GamesAdapter.State.IDLE);
                        mGamesAdapter.notifyDataSetChanged();

                    }
                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (mGamesAdapter != null) {
                        mGamesAdapter.setState(GamesAdapter.State.LOAD);
                        //load the images
                        mGamesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * Filters the list of search result games and gets those games
     * for the given platform. Will update the GamesAdapter with the
     * filtered list and notifies data has changed.
     * @param platform
     */
    private void getGameForPlatform(final String platform){
        //check if we want "all", i.e. no filtering.
        if(platform.trim().compareTo("All") == 0){
            LoadListView();
        }
        else {
            Func1 filter = new Func1<GameDataInfo, Boolean>() {

                @Override
                public Boolean call(GameDataInfo gameDataInfo) {
                    return gameDataInfo.getGame().Platform.trim().compareTo(platform) == 0;
                }
            };

            Observer<GameDataInfo> filterObserver = new Observer<GameDataInfo>() {
                ArrayList<GameDataInfo> filteredList = new ArrayList<GameDataInfo>();

                @Override
                public void onCompleted() {
                    mGamesAdapter.clearCache();
                    mGamesAdapter.setList(filteredList);
                    mGamesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "Error filtering list, " + e.getMessage());
                }

                @Override
                public void onNext(GameDataInfo gameDataInfo) {
                    filteredList.add(gameDataInfo);
                }
            };


            SearchResultsHolder.getInstance().getFilteredList(filter, filterObserver);
        }
    }


    /**
     * Filters the list of search result games and gets those games
     * released for a given date. Will update the GamesAdapter with the
     * filtered list and notifies data has changed.
     * @param date
     */
    private void getGameForReleaseData(final String date){

        Func1 filter = new Func1<GameDataInfo, Boolean>(){

            @Override
            public Boolean call(GameDataInfo gameDataInfo) {
                return gameDataInfo.getGame().Platform.trim().compareTo(date) == 0;
            }
        };

        Observer<GameDataInfo> filterObserver = new Observer<GameDataInfo>() {
            ArrayList<GameDataInfo> filteredList = new ArrayList<GameDataInfo>();

            @Override
            public void onCompleted() {
                mGamesAdapter.clearCache();
                mGamesAdapter.setList(filteredList);
                mGamesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error filtering list, " + e.getMessage());
            }

            @Override
            public void onNext(GameDataInfo gameDataInfo) {
                filteredList.add(gameDataInfo);
            }
        };

        SearchResultsHolder.getInstance().getFilteredList(filter, filterObserver);

    }
}
