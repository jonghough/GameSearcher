package com.jgh.gamedatabasesearcher;

import com.jgh.gamedatabasesearcher.models.models.game.Game;


/**
 * Created by Jon Hough on 9/16/15.
 */
public class GameDataInfo implements Comparable<GameDataInfo> {

    private final Game mGame;

    public GameDataInfo(Game game){
        mGame = game;
    }

    public Game getGame(){
        return mGame;
    }

    @Override
    public int compareTo(GameDataInfo another) {
       return another.getGame().GameTitle.compareTo(mGame.GameTitle);
    }
}
