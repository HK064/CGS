package source.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * トランプのカード画像を読み込むクラス。
 */
public class CardImage {
    private static BufferedImage[][] cardImages = new BufferedImage[4][14];
    private static BufferedImage jokerImage;
    private static BufferedImage cardBackImage;
    private static double aspect;

    static {
        try {
            String[] suitString = { "spade", "heart", "diamond", "club" };
            for (int suit = 0; suit < 4; suit++) {
                for (int number = 1; number < 14; number++) {
                    cardImages[suit][number] = ImageIO.read(new File(
                            "./image/card/card_" + suitString[suit] + "_" + String.format("%02d", number) + ".png"));
                }
            }
            jokerImage = ImageIO.read(new File("./image/card/card_joker.png"));
            cardBackImage = ImageIO.read(new File("./image/card/card_back.png"));
            aspect = (double) cardBackImage.getHeight() / cardBackImage.getWidth();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * カードの縦横比を返す。
     * 
     * @return 縦／横 (>1)
     */
    public static double getAspect() {
        return aspect;
    }

    /**
     * カードの画像を返す。
     * 
     * @param suit
     * @param number
     * @return
     */
    public static BufferedImage getImage(int suit, int number) {
        if (0 <= suit && suit < 4 && 1 <= number && number <= 13) {
            return cardImages[suit][number];
        }
        if (suit == 4) {
            return jokerImage;
        }
        return cardBackImage;
    }
}
