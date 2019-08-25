package gamedata.daifugo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import source.CGServer;
import source.Card;

public class DaifugoServer extends CGServer {
    private Map<String, List<Card>> playerCards = new HashMap<>();
    private Map<String, Integer> playerRanks = new HashMap<>();
    private Map<String, Integer> playerFormerRanks = new HashMap<>();
    private List<List<Card>> fieldCards = new ArrayList<>();
    private String fieldState = "";
    private String lastPutPlayer = null;
    private static final int STATE_READY = 0;
    private static final int STATE_CARD_CHANGE = 1;
    private static final int STATE_GAME = 2;
    private static final int STATE_END = 3;
    private int state = STATE_READY;
    private boolean[] cardChanged = { false, false };

    @Override
    public void startGame() {
        // 初期化
        playerCards.clear();
        playerFormerRanks = playerRanks;
        playerRanks = new HashMap<>();
        fieldCards.clear();
        fieldState = "";
        lastPutPlayer = null;
        state = STATE_READY;

        // プレイヤー順番シャッフル
        shufflePlayers();
        String str = "110";
        for (String name : playerNames) {
            str += " " + name;
        }
        sendAll(str);

        // カードを配る
        List<Card> cards = Card.generateAllCards();
        Collections.shuffle(cards, random);
        int cardsPerPlayer = (int) (cards.size() / playerNames.size());
        int fromIndex = 0;
        int toIndex = 0;
        for (int i = 0; i < playerNames.size(); i++) {
            // リストの分割
            toIndex = fromIndex + cardsPerPlayer;
            if (i < cards.size() - cardsPerPlayer * playerNames.size()) {
                toIndex++;
            }
            List<Card> cards2 = new LinkedList<>(cards.subList(fromIndex, toIndex));
            fromIndex = toIndex;

            // カードの送信
            playerCards.put(playerNames.get(i), cards2);
            sendOne(playerNames.get(i), "111 " + Card.convertToCodes(cards2));

            // 順位の設定（初めは0）
            playerRanks.put(playerNames.get(i), 0);

            // ダイヤの3を持つ者
            if (cards2.contains(new Card(2, 3))) {
                playerNameForTurn = playerNames.get(i);
            }

        }
        // 場のリセット
        resetField();

        // 各プレイヤーの残りカード枚数の送信
        sendPlayerCardSizes();

        // カード交換
        if (playerFormerRanks.size() >= 2) {
            state = STATE_CARD_CHANGE;

            String name[] = { null, null };
            for (int i = 0; i < 2; i++) {
                name[i] = DaifugoTool.getRankPlayerName(playerFormerRanks, i + 1);
                cardChanged[i] = (name[i] == null);
            }
            for (String n : name) {
                if (n != null) {
                    sendOne(n, "113");
                }
            }
        } else {
            startGame2();
        }
    }

    /**
     * ゲーム開始を開始する。
     */
    private void startGame2() {
        state = STATE_GAME;
        sendAll("114");
        sendAll("120 " + playerNameForTurn);
    }

    @Override
    public void listener(String name, String data) {
        String[] str = data.split(" ");
        if (state == STATE_CARD_CHANGE) {
            if (str[0].equals("115")) {
                // 枚数確認
                int num = 0;
                if (name.equals(DaifugoTool.getRankPlayerName(playerFormerRanks, 1))) {
                    num = 2;
                } else if (name.equals(DaifugoTool.getRankPlayerName(playerFormerRanks, 2))) {
                    num = 1;
                }
                List<Card> cards = Card.convertToList(str[1]);
                if (num != 0 && str[1].length() == 2 * num) {
                    // カード削除
                    playerCards.get(name).removeAll(cards);

                    // 相手
                    String name2 = DaifugoTool.getRankPlayerName(playerFormerRanks, num + 2);

                    // 相手から贈られるカード
                    DaifugoTool.sort(playerCards.get(name2));
                    List<Card> cards2 = new LinkedList<>();
                    for (int i = 1; i <= num; i++) {
                        cards2.add(playerCards.get(name2).remove(playerCards.get(name2).size()));
                    }

                    // 相手に贈る
                    sendOne(name2, "117 " + str[1]);
                    playerCards.get(name2).addAll(cards);

                    // 相手から贈られる
                    sendOne(name, "117 " + Card.convertToCodes(cards2));
                    playerCards.get(name).addAll(cards2);

                    // 交換完了
                    cardChanged[2 - num] = true;

                    if (cardChanged[0] && cardChanged[1]) {
                        startGame2();
                    }
                }
            }
        } else if (state == STATE_GAME && name.equals(playerNameForTurn)) {
            if (str[0].equals("121")) {
                // カードを出した

                List<Card> cards = Card.convertToList(str[1]);
                takePlayerAction(name, cards);
            } else if (str[0].equals("122")) {
                // パス

                takePlayerAction(name, null);
            }
        } else if (state == STATE_END) {
            if (str[0].equals("141")) {
                playerFormerRanks.put(name, 0);

                if (playerFormerRanks.size() == playerNames.size()) {
                    sendAll("142");
                    startGame();
                }
            }
        }
    }

    @Override
    protected void nextPlayer() {
        do {
            super.nextPlayer();
        } while (playerCards.get(playerNameForTurn).size() <= 0);
        if (playerNameForTurn.equals(lastPutPlayer)) {
            // パスで一周した
            resetField();
        }

        sendAll("120 " + playerNameForTurn);
    }

    private void sendPlayerCardSizes() {
        String str = "126";
        for (String name : playerNames) {
            str += " " + name + " " + String.valueOf(playerCards.get(name).size());
        }
        sendAll(str);
    }

    /**
     * プレイヤーの行動に関する処理を行う。
     * 
     * @param name
     * @param cards null なら「パス」
     */
    private void takePlayerAction(String name, List<Card> cards) {
        if (cards != null) {
            // プレイヤーがカードを持っているか
            boolean b = true;
            for (Card card : cards) {
                if (!playerCards.get(name).contains(card)) {
                    b = false;
                    return;
                }
            }

            String newFieldState = DaifugoTool.checkPutField(fieldCards, fieldState, cards);
            if (b && newFieldState != null) {
                // 承認
                sendOne(name, "123");
            } else {
                // 拒否
                sendOne(name, "124");
                return;
            }

            // 場の更新
            DaifugoTool.sort(cards);
            fieldCards.add(cards);

            // プレイヤーのカード削除
            playerCards.get(name).removeAll(cards);

            // パスのリセット
            lastPutPlayer = name;

            sendAll("125 " + Card.convertToCodes(cards));
            updateFieldState(newFieldState);
            sendPlayerCardSizes();

            // 上がりか
            boolean b2 = playerCards.get(name).size() == 0;
            if (b2) {
                lastPutPlayer = null;
                endPlayer(name);

                // 終了か
                int remainingPlayersNumber = 0;
                for (String playerName : playerNames) {
                    if (playerCards.get(playerName).size() > 0) {
                        remainingPlayersNumber++;
                    }
                }
                if (remainingPlayersNumber <= 1) {
                    endGame();
                }
            }

            // 流れるか
            String str2 = DaifugoTool.checkResetField(fieldCards);
            if (str2 != null) {
                updateFieldState(str2);

                (new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        resetField();

                        if (b2) {
                            nextPlayer();
                            lastPutPlayer = playerNameForTurn;
                        } else {
                            sendAll("120 " + playerNameForTurn);
                        }
                    }
                }, 1000);
            } else {
                nextPlayer();
                if (b2) {
                    lastPutPlayer = playerNameForTurn;
                }
            }

        } else {
            sendOne(name, "123");

            nextPlayer();
        }
    }

    private void resetField() {
        // 場が流れる
        fieldCards.clear();
        if (fieldState.contains(DaifugoTool.FIELD_STATE_REVOLUTION)) {
            fieldState = DaifugoTool.FIELD_STATE_REVOLUTION;
        } else {
            fieldState = "";
        }
        updateFieldState("");
        sendAll("127");
    }

    private void updateFieldState(String addState) {
        boolean reverseRevolution = fieldState.contains(DaifugoTool.FIELD_STATE_REVOLUTION)
                && addState.contains(DaifugoTool.FIELD_STATE_REVOLUTION);
        fieldState += addState;
        if (reverseRevolution) {
            fieldState = fieldState.replace(DaifugoTool.FIELD_STATE_REVOLUTION, "");
        }
        sendAll("128 " + fieldState);
    }

    private void endPlayer(String name) {
        playerCards.get(name).clear();

        // 順位の決定
        int rank = 0;
        for (String playerName : playerNames) {
            rank = Math.max(rank, playerRanks.get(playerName));
        }
        rank++;
        playerRanks.put(name, rank);
        sendAll("130 " + name + " " + rank);
    }

    private void endGame() {
        for (String playerName : playerNames) {
            if (playerCards.get(playerName).size() > 0) {
                endPlayer(playerName);
            }
        }
        state = STATE_END;
        playerFormerRanks.clear();
        sendAll("140");

    }
}
