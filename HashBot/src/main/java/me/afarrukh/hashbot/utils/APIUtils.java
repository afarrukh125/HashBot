package me.afarrukh.hashbot.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class APIUtils {

    public static String getResponseFromURL(String url) {
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String res = null;
            if (response.body() != null) {
                res = (response.code() == 200) ? response.body().string() : null;
            }
            return res;
        } catch(IOException e) { return null; }
    }
}
