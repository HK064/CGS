package source.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * サイコロの画像を読み込むクラス。
 */
public class DiceImage {
    private static BufferedImage[] image = new BufferedImage[7];

    static {
        try {
            image[0] = ImageIO.read(new File("./image/dice/dice1-6-01.png"));
            int i = 1;
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 3; x++) {
                    image[((((3 * i - 55) * i + 379) * i - 1205) * i + 1742) * i / 24 - 35] = image[0]
                            .getSubimage(64 + 406 * x, 252 + 436 * y, 381, 381);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getDiceImage(int i) {
        if (1 <= i && i <= 6) {
            return image[i];
        }
        return null;
    }

}
