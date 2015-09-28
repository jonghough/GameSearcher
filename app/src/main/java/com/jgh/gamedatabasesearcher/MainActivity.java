package com.jgh.gamedatabasesearcher;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class MainActivity extends Activity {

    private static final String TAG = "MAIN_ACTIVITY";
    private EditText mNameEditText;
    private Button mQueryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameEditText = (EditText) findViewById(R.id.name_edit);
        mQueryButton = (Button) findViewById(R.id.query_button);


        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = null;
                try {
                    name = URLEncoder.encode(mNameEditText.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (name != null && name.trim().compareTo("") != 0) {
                    //load search word/s into results holder
                    SearchResultsHolder.getInstance().setSearchWords(name);

                    //begin background thread to search for games.
                    new Thread(new GameListRunnable(name, new TaskCallbackHandler<GameDataInfo>() {
                        @Override
                        public void onSuccess(ArrayList<GameDataInfo> list) {
                            final ArrayList<GameDataInfo> l = list;
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Collections.sort(l);
                                    SearchResultsHolder.getInstance().setDataList(l);

                                    // load Results activity, for showing API results in ListView
                                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                                    MainActivity.this.startActivity(intent);
                                }
                            });

                        }

                        @Override
                        public void onError() {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Error loading search results")
                                            .setMessage("List of games could not be loaded. Return to previous screen.")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            });
                        }
                    })).start();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
