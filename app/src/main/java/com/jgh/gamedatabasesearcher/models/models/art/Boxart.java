package com.jgh.gamedatabasesearcher.models.models.art;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Created by Jon Hough on 9/24/15.
 */
public class Boxart {

    @Attribute
    private String side;

    @Attribute
    private String width;

    @Attribute
    private String height;

    @Attribute
    private String thumb;

    public String getSide(){
        return side;
    }

    public String getWidth(){
        return width;
    }

    public String getHeight(){
        return height;
    }

    public String getThumb(){
        return thumb;
    }

    @Text
    public String value;
}
