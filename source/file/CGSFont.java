package source.file;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class CGSFont {
    private static Font font;

    static {
        String filePath = CGSProperty.getValue(CGSProperty.FONT_FILE_KEY);
        if (filePath == null) {
            font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        } else {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(filePath));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Font getFont(int size) {
        return font.deriveFont((float) (size));
    }

    public static Font getFont(int size, int style){
        return getFont(size).deriveFont(style);
    }

}
