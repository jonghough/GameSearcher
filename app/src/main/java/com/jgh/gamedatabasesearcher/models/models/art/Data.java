package com.jgh.gamedatabasesearcher.models.models.art;

import org.simpleframework.xml.Element;

/**
 * Created by Jon Hough on 9/10/15.
 */
public class Data {

    @Element(required=false)
    public String baseImgUrl;

    @Element(required=false, name="Images")
    public Images images;
}
