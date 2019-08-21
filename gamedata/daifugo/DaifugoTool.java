package gamedata.daifugo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import source.Card;

class DaifugoTool {
    private static String[] rankName = { "大富豪", "富豪", "平民", "貧民", "大貧民" };
    static final String FIELD_STATE_REVOLUTION = "R";
    static final String FIELD_STATE_8RESET = "8";
    static final String FIELD_STATE_SPADE3 = "3";
    static final String FIELD_STATE_BIND_SPADE = "S";
    static final String FIELD_STATE_BIND_HEART = "H";
    static final String FIELD_STATE_BIND_DIAMOND = "D";
    static final String FIELD_STATE_BIND_CLUB = "C";
    static final String[] FIELD_STATE_BIND = { FIELD_STATE_BIND_SPADE, FIELD_STATE_BIND_HEART, FIELD_STATE_BIND_DIAMOND,
            FIELD_STATE_BIND_CLUB };
    static final String FIELD_STATE_JBACK = "J";

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
     * @return 出せない場合:null, 出せる場合:追加の FIELD_STATE を返す。
     */
    static String checkPutField(List<List<Card>> fieldCards, String fieldState, List<Card> selectCards) {
        if (selectCards.size() > 0) {
            String str = "";
            int[] number = { -1, -1 };
            boolean[] suitExist = { false, false, false, false };
            int joker = 0;
            // 数字が揃っているか
            for (Card card : selectCards) {
                if (card.isJoker()) {
                    joker++;
                } else {
                    if (number[0] == -1) {
                        number[0] = card.getNumber();
                    } else if (number[0] != card.getNumber()) {
                        return null;
                    }
                    suitExist[card.getSuitInt()] = true;
                }
            }
            if (fieldCards.size() > 0) {
                if (fieldCards.get(fieldCards.size() - 1).size() != selectCards.size()) {
                    // 枚数が異なる
                    return null;
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
                    str += FIELD_STATE_SPADE3;
                } else {
                    // 番号確認
                    if (fieldState.contains(FIELD_STATE_REVOLUTION) ^ fieldState.contains(FIELD_STATE_JBACK)) {
                        // 革命状態
                        for (int i = 0; i < 2; i++) {
                            if (number[i] == 1 || number[i] == 2) {
                                number[i] += 13;
                            }
                        }
                        if (number[0] >= number[1]) {
                            return null;
                        }
                    } else {
                        // 通常状態
                        for (int i = 0; i < 2; i++) {
                            if (number[i] == -1) {
                                number[i] = 16;
                            } else if (number[i] == 1 || number[i] == 2) {
                                number[i] += 13;
                            }
                        }
                        if (number[0] <= number[1]) {
                            return null;
                        }
                    }
                    if(selectCards.size() < 4){
                        // スート縛りの確認
                        boolean noBind = true;
                        int copyJoker = joker;
                        for (int i = 0; i < 4; i++) {
                            if (fieldState.contains(FIELD_STATE_BIND[i])) {
                                if (!suitExist[i]) {
                                    if (copyJoker == 0) {
                                        return null;
                                    } else {
                                        copyJoker--;
                                    }
                                }
                                noBind = false;
                            }
                        }
                        // 新たな縛りの確認
                        boolean newBind = true;
                        if (noBind && joker == 0 && fieldCards.size() >= 2) {
                            for (Card card : fieldCards.get(fieldCards.size() - 2)) {
                                if (card.isJoker() || !suitExist[card.getSuitInt()]) {
                                    newBind = false;
                                    break;
                                }
                            }
                        }
                        // 新たな縛りの設定
                        if (newBind) {
                            for (int i = 0; i < 4; i++) {
                                if(suitExist[i]){
                                    str += FIELD_STATE_BIND[i];
                                }
                            }
                        }
                    }
                }
            }
            if (number[0] == 8) {
                str += FIELD_STATE_8RESET;
            }
            if (number[0] == 11) {
                str += FIELD_STATE_JBACK;
            }
            if(selectCards.size() >= 4){
                str += FIELD_STATE_REVOLUTION;
            }
            return str;
        }
        return null;
    }

    /**
     * 場を流すか判定する。
     * 
     * @param fieldCards
     * @return 流れない場合:null, 流れる場合追加の:FIELD_STATE を返す。
     */
    static String checkResetField(List<List<Card>> fieldCards) {
        if (fieldCards.size() > 0) {
            // ８切り
            if (fieldCards.get(fieldCards.size() - 1).get(0).getNumber() == 8) {
                return FIELD_STATE_8RESET;
            }
            // スペ３返し
            if (fieldCards.size() >= 2 && fieldCards.get(0).size() == 1) {
                Card card = fieldCards.get(fieldCards.size() - 1).get(0);
                if (card.getSuitInt() == 0 && card.getNumber() == 3
                        && fieldCards.get(fieldCards.size() - 2).get(0).isJoker()) {
                    return FIELD_STATE_SPADE3;
                }
            }

        }
        return null;
    }

    /**
     * フィールドの状態の説明を返す。
     * 
     * @param fieldState
     * @return
     */
    static String getFieldStateDescription(String fieldState) {
        String description = "";
        if (fieldState.contains(FIELD_STATE_REVOLUTION)) {
            description += "革命 ";
        }
        if (fieldState.contains(FIELD_STATE_JBACK)) {
            description += "Ｊバック ";
        }
        boolean bind = false;
        if (fieldState.contains(FIELD_STATE_BIND_SPADE)) {
            description += "♠";
            bind = true;
        }
        if (fieldState.contains(FIELD_STATE_BIND_HEART)) {
            description += "♥";
            bind = true;
        }
        if (fieldState.contains(FIELD_STATE_BIND_DIAMOND)) {
            description += "♦";
            bind = true;
        }
        if (fieldState.contains(FIELD_STATE_BIND_CLUB)) {
            description += "♣";
            bind = true;
        }
        if (bind) {
            description += "縛り ";
        }
        if (fieldState.contains(FIELD_STATE_8RESET)) {
            description += "８切り ";
        }
        if (fieldState.contains(FIELD_STATE_SPADE3)) {
            description += "スペ３返し ";
        }
        return description;
    }

    /**
     * 順位による称号を返す。
     * 
     * @param rank 順位(1-)
     * @param all  参加人数
     */
    static String getRankName(int rank, int all) {
        if (0 < rank && rank <= all) {
            if (all >= 4) {
                if (rank <= 2) {
                    return rankName[rank - 1];
                }
                if (rank > all - 2) {
                    return rankName[rank - all + 4];
                }
                return rankName[2];
            }
            if (all == 3) {
                return rankName[rank];
            }
            if (all == 2) {
                return rankName[2 * rank - 1];
            }
        }
        return rankName[2];
    }

}
