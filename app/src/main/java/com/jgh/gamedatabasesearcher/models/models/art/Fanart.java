package com.jgh.gamedatabasesearcher.models.models.art;

import org.simpleframework.xml.Element;

/**
 * Created by Jon Hough on 9/10/15.
 */
public class Fanart {
    @Element(required=false)
    public String original;

    @Element(required=false)
    public String thumb;


}
