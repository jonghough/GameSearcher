package com.jgh.gamedatabasesearcher.models.models.platform;

import com.jgh.gamedatabasesearcher.models.models.art.Fanart;

import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class Platforms {

    @ElementList(entry="Platform",inline=true, name="Platform", required=false)
    public List<Platform> platforms;
}
