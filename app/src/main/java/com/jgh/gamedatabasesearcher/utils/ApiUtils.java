package com.jgh.gamedatabasesearcher.utils;

/**
 * Created by Jon Hough on 9/16/15.
 */
public class ApiUtils {

    public static final String BASE = "http://thegamesdb.net/";
    /**
     * Base URL for Game Database API
     */
    public static final String API_BASE = BASE+"api/GetGamesList.php";

    /**
     * Get list of platforms
     */
    public static final String PLATFORM_API = BASE+"api/GetPlatformsList.php";

    /**
     *Get art for a game
     */
    public static final String ART_API_BASE =BASE+"api/GetArt.php";
    /**
     * GET parameter for searching by name
     */
    public static final String API_NAME_SEARCH = "?name=";
    /**
     *  GET parameter for searching by game id
     */
    public static final String API_ID_SEARCH = "?id=";
}
