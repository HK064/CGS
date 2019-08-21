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
    private List<List<Card>> fieldCards = new ArrayList<>();
    private String lastPutPlayer = null;

    @Override
    public void startGame() {
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
        sendAll("127");

        // 各プレイヤーの残りカード枚数の送信
        sendPlayerCardSizes();

        // ゲーム開始
        sendAll("114");
        sendAll("120 " + playerNameForTurn);
    }

    @Override
    public void listener(String name, String data) {
        if (name.equals(playerNameForTurn)) {
            String[] str = data.split(" ");
            if (str[0].equals("121")) {
                // カードを出した

                List<Card> cards = Card.convertToList(str[1]);
                takePlayerAction(name, cards);
            } else if (str[0].equals("122")) {
                // パス

                takePlayerAction(name, null);
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

            if (b && DaifugoTool.checkPutField(fieldCards, cards)) {
                // 承認
                sendOne(name, "123");
            } else {
                // 拒否
                sendOne(name, "124");
                return;
            }

            // 場の更新
            fieldCards.add(cards);

            // プレイヤーのカード削除
            for (Card card : cards) {
                playerCards.get(name).remove(card);
            }

            // パスのリセット
            lastPutPlayer = name;

            sendAll("125 " + Card.convertToCodes(cards));
            sendPlayerCardSizes();

            // 上がりか
            if (playerCards.get(name).size() == 0) {
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
            if (DaifugoTool.checkResetField(fieldCards)) {
                (new Timer()).schedule(new TimerTask() {

                    @Override
                    public void run() {
                        resetField();

                        sendAll("120 " + playerNameForTurn);
                    }
                }, 1000);
            } else {
                nextPlayer();
            }

        } else {
            sendOne(name, "123");

            nextPlayer();
        }
    }

    private void resetField() {
        // 場が流れる
        fieldCards.clear();
        sendAll("127");
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
        sendAll("140");

    }
}
