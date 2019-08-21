package gamedata.daifugo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import source.Card;

class DaifugoTool {
    private static String[] rankName = { "大富豪", "富豪", "平民", "貧民", "大貧民" };

    /**
     * 
     * @param cards
     */
    static void sort(List<Card> cards) {
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2) {
                if (!card1.isJoker() && !card2.isJoker() && card1.getNumber() != card2.getNumber()) {
                    if ((2 * card1.getNumber() - 5) * (2 * card2.getNumber() - 5) < 0) {
                        return 2 * card2.getNumber() - 5;
                    }
                    return card1.getNumber() - card2.getNumber();
                }
                return card1.getSuitInt() - card2.getSuitInt();
            }
        });
    }

    /**
     * 選択したカードを場に出せるかを調べる。
     * 
     * @param fieldCards
     * @param selectCards
     * @return
     */
    static boolean checkPutField(List<List<Card>> fieldCards, List<Card> selectCards) {
        if (selectCards.size() > 0) {
            int[] number = { -1, -1 };
            // 数字が揃っているか
            for (Card card : selectCards) {
                if (!card.isJoker()) {
                    if (number[0] == -1) {
                        number[0] = card.getNumber();
                    } else if (number[0] != card.getNumber()) {
                        return false;
                    }
                }
            }
            if (fieldCards.size() > 0) {
                if (fieldCards.get(fieldCards.size() - 1).size() != selectCards.size()) {
                    // 枚数が異なる
                    return false;
                }
                // 場のカードの数字は
                for (Card fieldCard : fieldCards.get(fieldCards.size() - 1)) {
                    if (!fieldCard.isJoker()) {
                        number[1] = fieldCard.getNumber();
                        break;
                    }
                }
                // スペ３返し？
                if (selectCards.size() == 1 && number[1] == -1 && selectCards.get(0).getSuitInt() == 0
                        && selectCards.get(0).getNumber() == 3) {
                    return true;
                }

                for (int i = 0; i < 2; i++) {
                    if (number[i] == -1) {
                        number[i] = 16;
                    } else if (number[i] == 1 || number[i] == 2) {
                        number[i] += 13;
                    }
                }

                if (number[0] <= number[1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static boolean checkResetField(List<List<Card>> fieldCards) {
        if(fieldCards.size() > 0){
            // ８切り
            if (fieldCards.get(fieldCards.size() - 1).get(0).getNumber() == 8) {
                return true;
            }
            // スペ３返し
            if (fieldCards.size() >= 2 && fieldCards.get(0).size() == 1) {
                Card card = fieldCards.get(fieldCards.size() - 1).get(0);
                if (card.getSuitInt() == 0 && card.getNumber() == 3 && fieldCards.get(fieldCards.size() - 2).get(0).isJoker()) {
                    return true;
                }
            }
            
        }
        return false;
    }

    /**
     * 順位による称号を返す。
     * @param rank 順位(1-)
     * @param all 参加人数
     */
    static String getRankName(int rank, int all) {
        if(0 < rank && rank <= all){
            if (all >= 4) {
                if(rank <= 2){
                    return rankName[rank - 1];
                }
                if(rank > all - 2){
                    return rankName[rank - all + 4];
                }
                return rankName[2];
            }
            if(all == 3){
                return rankName[rank];
            }
            if(all == 2){
                return rankName[2 * rank - 1];
            }
        }
        return rankName[2];
    }

}
