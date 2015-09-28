package com.jgh.gamedatabasesearcher.models.models.platform;

import org.simpleframework.xml.Element;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class Platform {

    @Element(required=false)
    public String id;

    @Element(required=false)
    public String name;

    @Element(required=false)
    public String alias;
}
