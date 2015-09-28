package com.jgh.gamedatabasesearcher.xml;

import android.util.Log;

import com.jgh.gamedatabasesearcher.DataHandler;
import com.jgh.gamedatabasesearcher.models.models.platform.Data;
import com.jgh.gamedatabasesearcher.models.models.platform.Platform;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class XmlPlatformHandler implements DataHandler<List<Platform>> {
    private static final String TAG = "XmlPlatformHandler";
    @Override
    public List<Platform> handleData(String s) throws Exception {
        Reader reader = new StringReader(s);
        Serializer serializer = new Persister();
        try {
            Data d = serializer.read(Data.class, reader, false);
            return d.platforms.platforms;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
