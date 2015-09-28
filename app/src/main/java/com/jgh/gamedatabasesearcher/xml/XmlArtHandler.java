package com.jgh.gamedatabasesearcher.xml;


import com.jgh.gamedatabasesearcher.DataHandler;
import com.jgh.gamedatabasesearcher.models.models.art.Data;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jon Hough on 9/28/15.
 */
public class XmlArtHandler implements DataHandler<List<String>> {
    private static final String TAG = "XmlArtHandler";

    @Override
    public List<String> handleData(String s) throws Exception {
        Reader reader = new StringReader(s);
        Serializer serializer = new Persister();

        List<String> imageUrlList = new ArrayList<String>();

        try {
            Data d = serializer.read(Data.class, reader, false);

            /*
             * First try to get a boxart thumbnail image, if one exists.
             * If not, try to get a fanart thumbnail.
             */
            String imageUrl = null;

            if (d.images.boxarts != null && d.images.boxarts.size() > 0) {
                imageUrl = d.images.boxarts.get(0).getThumb();

            } else if (d.images.fanarts != null && d.images.fanarts.size() > 0) {
                imageUrl = d.images.fanarts.get(0).thumb;
            }
            imageUrlList.add(d.baseImgUrl + imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return imageUrlList;
    }
}
