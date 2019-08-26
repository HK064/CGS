package source.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * サイコロの画像を読み込むクラス。
 */
public class DiceImage {
    private static BufferedImage diceImage145;
    private static BufferedImage diceImage236;

    static {
        try {
            diceImage145 = ImageIO.read(new File("./image/dice/saikoro_145.png"));
            diceImage236 = ImageIO.read(new File("./image/dice/saikoro_236.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1, 4, 5 の描かれたサイコロの画像を返す。
     * 
     * @return
     */
    public static BufferedImage getImage145() {
        return diceImage145;
    }

    /**
     * 2, 3, 6 の描かれたサイコロの画像を返す。
     * 
     * @return
     */
    public static BufferedImage getImage236() {
        return diceImage236;
    }

}
