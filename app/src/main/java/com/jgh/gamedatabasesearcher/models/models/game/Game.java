package com.jgh.gamedatabasesearcher.models.models.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jon Hough on 9/7/15.
 */
@Element
public class Game {

    @Element(required=false)
    public String GameTitle;

    @Element(required=false)
    public String id;

    @Element(required=false)
    public String ReleaseDate;

    @Element(required=false)
    public String Platform;

}
