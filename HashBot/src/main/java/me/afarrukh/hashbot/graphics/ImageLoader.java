package me.afarrukh.hashbot.graphics;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); //We don't want to run our game if the image fails to load.
            //Exit code 1
        }
        return null;
    }

    public static BufferedImage loadUrl(String path) {
        try {
            URL url = new URL(path);

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            if (response.body() != null) {
                return ImageIO.read(response.body().byteStream());
            }
        } catch (MalformedURLException ignore) {
        } catch (IOException e) { e.getMessage(); }
        return null;
    }

}
