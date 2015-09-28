package com.jgh.gamedatabasesearcher;

import com.squareup.okhttp.OkHttpClient;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jon Hough on 15/09/06.
 */
public class ApiController {
    /**
     *
     */
    public ApiController(){

    }

    public String callApi(String url){
        try {
            return run(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
         return response.body().string();
    }


    public InputStream callApiStream(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response resp = client.newCall(request).execute();
        return resp.body().byteStream();
    }
}
