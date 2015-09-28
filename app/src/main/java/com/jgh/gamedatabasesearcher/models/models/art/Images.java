package com.jgh.gamedatabasesearcher.models.models.art;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by Jon Hough on 9/10/15.
 */
public class Images {

    @ElementList(entry="fanart",inline=true, name="fanart", required=false)
    public List<Fanart> fanarts;

    @ElementList(entry="boxart", required=false,inline=true, name="boxart")
    public List<Boxart> boxarts;

    @ElementList(entry="banner", name="banner",inline=true, required=false)
    public List<String> banners;

}
