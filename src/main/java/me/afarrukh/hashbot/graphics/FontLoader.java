package me.afarrukh.hashbot.graphics;

import java.awt.*;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class FontLoader {

    public static Font loadFont(String path, int size) {
        try {
            return Font.createFont(
                            Font.TRUETYPE_FONT,
                            requireNonNull(FontLoader.class.getClassLoader().getResourceAsStream(path)))
                    .deriveFont(Font.PLAIN, size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
