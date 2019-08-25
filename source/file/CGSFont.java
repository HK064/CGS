package source.file;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class CGSFont {
    private static Font font;

    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("./font/ipaexm.ttf"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Font getFont(int size) {
        return font.deriveFont((float) (size));
    }

    public static Font getFont(int size, int style){
        return getFont(size).deriveFont(style);
    }

}
