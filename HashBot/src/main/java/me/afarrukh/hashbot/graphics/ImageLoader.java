package me.afarrukh.hashbot.graphics;

import okhttp3.OkHttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
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
            return ImageIO.read(url.openStream());
        } catch (MalformedURLException ignore) {
        } catch (IOException e) {
            try {
                System.out.println("Exception occurred, check log.txt");
                PrintWriter pw = new PrintWriter(new FileWriter("log.txt"));
                e.printStackTrace(pw);
                pw.close();
            } catch (IOException ex) {
            }

        }
        return null;
    }

}
