package com.jgh.gamedatabasesearcher.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgh.gamedatabasesearcher.ApiController;
import com.jgh.gamedatabasesearcher.GameDataInfo;
import com.jgh.gamedatabasesearcher.ImageRunnable;
import com.jgh.gamedatabasesearcher.R;
import com.jgh.gamedatabasesearcher.TaskCallbackHandler;
import com.jgh.gamedatabasesearcher.utils.ApiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Jon Hough on 9/9/15.
 */
public class GamesAdapter extends BaseAdapter {

    private ArrayList<GameDataInfo> mGameList = new ArrayList<GameDataInfo>();
    private static final String TAG = "GamesAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    private LruCache<Integer, Bitmap> mDataMap = new LruCache<>(maxMemory / 8);

    public enum State {IDLE, LOAD}

    private State mState = State.LOAD;

    public synchronized State getState() {
        return mState;
    }

    public synchronized void setState(State s) {
        mState = s;
    }

    /**
     * Constructor
     *
     * @param gamesList
     * @param context
     */
    public GamesAdapter(ArrayList<GameDataInfo> gamesList, Context context) {
        mGameList = gamesList;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Sets the games list to the given list.
     *
     * @param gamesList
     */
    public synchronized void setList(ArrayList<GameDataInfo> gamesList) {
        mGameList = gamesList;
    }


    @Override
    public int getCount() {
        return mGameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, null);
            Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "Unique.ttf");

            ImageView imageView = (ImageView) convertView.findViewById(R.id.item_imageview);
            TextView textView1 = (TextView) convertView.findViewById(R.id.item_textview1);
            TextView textView2 = (TextView) convertView.findViewById(R.id.item_textview2);
            TextView textView3 = (TextView) convertView.findViewById(R.id.item_textview3);

            holder.imageView = imageView;
            holder.textView1 = textView1;
            holder.textView2 = textView2;
            holder.textView3 = textView3;
            holder.textView1.setTypeface(typeFace);
            holder.textView2.setTypeface(typeFace);
            holder.textView3.setTypeface(typeFace);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.imageView.setTag(position);
        retreiveImage(holder.imageView, position);
        holder.textView1.setText(mGameList.get(position).getGame().GameTitle);
        holder.textView2.setText(mGameList.get(position).getGame().Platform);
        holder.textView3.setText(mGameList.get(position).getGame().ReleaseDate);

        return convertView;
    }

    /**
     * Retrieves the image, either from the LRU cache, or by downloading from the
     * API.
     *
     * @param imageView
     * @param pos
     */
    private void retreiveImage(ImageView imageView, int pos) {

        synchronized (imageView) {
            imageView.setImageDrawable(null);
            if (hasTag(imageView) && mDataMap.get(pos) != null) {
                imageView.setImageBitmap(mDataMap.get(pos));
            } else {
                if (getState() == State.IDLE) {
                    return;
                }
                new ArtApiQuery(imageView, pos, ApiUtils.ART_API_BASE + ApiUtils.API_ID_SEARCH + mGameList.get(pos).getGame().id).callAPI();
            }
        }
    }


    private boolean hasTag(ImageView v) {
        return v.getTag() != null;
    }


    private class ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }


    public Observable<String> query(String url) {
        List<String> s = new ArrayList<String>();
        s.add(new ApiController().callApi(url));

        return Observable.from(s);
    }

    /**
     * Runnable to retrieve Artwork of a game from API.
     */
    private class ArtApiQuery{

        private String mUrl;
        private int mId;
        private ImageView mImageView;
        private ImageHandler handler = null;

        public ArtApiQuery(ImageView view, int pos, String URL) {
            mUrl = URL;
            mId = pos;
            mImageView = view;
            mImageView.setImageDrawable(null); //set no image
            handler = new ImageHandler(pos, mImageView);
        }

        /**
         * Calls API to get the art images for the given game item.
         */
        public void callAPI() {
            new Thread(new ImageRunnable(mUrl, new TaskCallbackHandler<String>() {
                @Override
                public void onSuccess(ArrayList<String> list) {
                    if (list != null && list.isEmpty() == false)
                        handleResult(list.get(0), mImageView, mId, handler);
                }

                @Override
                public void onError() {
                    Log.e(TAG, "Error trying to get image.");
                }
            })).start();

        }
    }


    /**
     * Handles the result of the API call, which gives a url for image download.
     *
     * @param imageUrl
     * @param imageView
     * @param id
     * @param handler
     */
    private void handleResult(final String imageUrl, final ImageView imageView, final int id, final ImageHandler handler) {

        try {
            if (imageUrl != null) {
                final String url = imageUrl;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bmp = BitmapFactory.decodeStream(new ApiController().callApiStream(url));
                            if(bmp == null){
                                Log.e(TAG, "Bitmap failed to be downloaded or decoded.");
                            }
                            else {
                                //scale the bitmap
                                bmp = Bitmap.createScaledBitmap(bmp, 100, 100, false);

                                synchronized (imageView) {
                                    mDataMap.put(id, bmp);

                                    Bundle data = new Bundle();
                                    data.putParcelable("image", bmp);
                                    Message msg = new Message();
                                    msg.setData(data);
                                    handler.sendMessage(msg);
                                }
                            }

                        } catch (IOException ioe) {
                            if (imageView != null) {
                                imageView.setImageDrawable(null);
                            }
                            Log.e(TAG, "Exception (IO) : " + ioe.getMessage());
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (imageView) {
                        imageView.setImageDrawable(null);
                    }
                }
            });

            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Image Handler, for keeping the ImageView and Position while
     * data is downloaded asynchronously.
     */
    private class ImageHandler extends Handler {
        private int mPosition;
        private ImageView mView;

        public ImageHandler(int position, ImageView view) {
            mPosition = position;
            mView = view;

        }

        @Override
        public void handleMessage(Message msg) {
            synchronized (mView) {
                int pos = (Integer) mView.getTag();
                if (pos != mPosition) {
                    return;
                }

                Bitmap bmp = msg.getData().getParcelable("image");
                mView.setImageBitmap(bmp);
            }
        }
    }
}
