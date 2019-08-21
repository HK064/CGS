package source;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * トランプのカードのクラス
 * 
 * ※ 2枚のジョーカーは別物として扱われます。
 */
public class Card {
    protected static String[] suitCodes = { "S", "H", "D", "C", "J" };
    private static List<String> suitCodesList = Arrays.asList(suitCodes);
    protected static String[] numberCodes = { "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "J", "Q", "K" };
    private static List<String> numberCodesList = Arrays.asList(numberCodes);
    public static final int JOKER = 4;
    protected int suit;
    protected int number;

    /**
     * 
     * @param suit   0:スペード, 1:ハート, 2:ダイヤ, 3:クラブ, 4:ジョーカー
     * @param number 1-13, ジョーカーは 1,2（2枚分）
     */
    public Card(int suit, int number) {
        this.suit = suit;
        this.number = number;
    }

    /**
     * 
     * ※ Ace でなく 1, 10 でなく 0 に注意すること。
     * 
     * @param code スート:SHDCJ, 番号:1234567890JQK
     */
    public Card(String code) {
        suit = suitCodesList.indexOf(code.substring(0, 1));
        number = numberCodesList.indexOf(code.substring(1, 2));
    }

    /**
     * スートを返す。
     * 
     * @return 整数で返す。
     */
    public int getSuitInt() {
        return suit;
    }

    /**
     * 数字を返す。
     * 
     * @return ジョーカーは 1 or 2
     */
    public int getNumber() {
        return number;
    }

    /**
     * コードを返す。
     * 
     * @return
     */
    public String getCode() {
        return suitCodes[suit] + numberCodes[number];
    }

    /**
     * ジョーカーかどうかを返す。
     * @return
     */
    public boolean isJoker(){
        return suit == JOKER;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (!(o instanceof Card))
            return false;
        Card c = (Card) o;
        return suit == c.getSuitInt() && number == c.getNumber();
    }

    @Override
    public int hashCode() {
        return 13 * suit + number;
    }

    /**
     * 一組のカードを生成する。
     * 
     * @return
     */
    public static List<Card> generateAllCards() {
        List<Card> cards = new LinkedList<>();
        for (int suit = 0; suit < JOKER; suit++) {
            for (int number = 1; number < 14; number++) {
                cards.add(new Card(suit, number));
            }
        }
        cards.add(new Card(JOKER, 1));
        cards.add(new Card(JOKER, 2));
        return cards;
    }

    /**
     * リストからコードに変換する。
     * 
     * @param cards
     * @return
     */
    public static String convertToCodes(List<Card> cards) {
        String str = "";
        for (Card card : cards) {
            str += card.getCode();
        }
        return str;
    }

    /**
     * コードからリストに変換する。
     * 
     * @param codes
     * @return
     */
    public static List<Card> convertToList(String codes) {
        List<Card> cards = new LinkedList<>();
        for (int i = 0; i < codes.length(); i += 2) {
            cards.add(new Card(codes.substring(i, i + 2)));
        }
        return cards;
    }

}
