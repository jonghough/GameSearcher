package com.jgh.gamedatabasesearcher.models.models.game;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Jon Hough on 9/7/15.
 */
@Root
public class Data{
    @ElementList(type=Game.class, entry="Game", inline=true, name="Game")
    public List<Game> games;

}