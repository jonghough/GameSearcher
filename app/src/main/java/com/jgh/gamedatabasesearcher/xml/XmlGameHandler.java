package com.jgh.gamedatabasesearcher.xml;

import android.util.Log;

import com.jgh.gamedatabasesearcher.DataHandler;
import com.jgh.gamedatabasesearcher.models.models.game.Data;
import com.jgh.gamedatabasesearcher.models.models.game.Game;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class XmlGameHandler implements DataHandler<List<Game>> {
    private static final String TAG = "XmlGameHandler";
    @Override
    public List<Game> handleData(String s) throws Exception {
        Reader reader = new StringReader(s);
        Serializer serializer = new Persister();
        try {
            Data d = serializer.read(Data.class, reader, false);
            Log.v(TAG, d.games.size() + " <- size of games list");
            return d.games;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
