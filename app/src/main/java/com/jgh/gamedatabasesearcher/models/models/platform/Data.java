package com.jgh.gamedatabasesearcher.models.models.platform;

import org.simpleframework.xml.Element;

/**
 * Created by Jon Hough on 9/25/15.
 */
public class Data {

    @Element(required=false)
    public String basePlatformUrl;

    @Element(required=false, name="Platforms")
    public Platforms platforms;
}
