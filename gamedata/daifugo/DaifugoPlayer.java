package gamedata.daifugo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import source.CGPlayer;
import source.Card;

public class DaifugoPlayer extends CGPlayer {
    private List<Card> cards = new LinkedList<>();
    private List<Card> selectedCards = new LinkedList<>();
    private List<List<Card>> fieldCards = new ArrayList<>();
    private Map<String, Integer> playerCardSizes = new HashMap<>();
    private Map<String, Integer> playerRanks = new HashMap<>();
    static final int STATE_READY = 0; // 準備中
    static final int STATE_GAME = 1; // ゲーム中
    static final int STATE_TURN = 2; // 自分のターン（行動できる）
    static final int STATE_END_TURN = 3; // 自分のターン（行動終了）
    static final int STATE_WAIT_REPLY = 4; // 自分のターン（サーバからの返事待ち）
    static final int STATE_END_GAME = 5;
    private int state = STATE_READY;
    private String fieldState = "";

    @Override
    public synchronized void listener(String data) {
        String[] str = data.split(" ");
        if (str[0].equals("110")) {
            // プレイヤーリスト受信

            for (int i = 1; i < str.length; i++) {
                playerNames.add(str[i]);
            }
        } else if (str[0].equals("111")) {
            // カードが配られる

            cards = Card.convertToList(str[1]);
            DaifugoTool.sort(cards);
        } else if (str[0].equals("114")) {
            // ゲーム開始

            state = STATE_GAME;
        } else if (str[0].equals("120")) {
            // ターンのプレイヤーが変わりました

            if(!str[1].equals(playerNameForTurn)){
                playerNameForTurn = str[1];
            }
            if (playerNameForTurn.equals(name)) {
                state = STATE_TURN;
            } else {
                state = STATE_GAME;
            }
        } else if (str[0].equals("123")) {
            // 行動が承認されました。

            if (state == STATE_WAIT_REPLY) {
                // プレイヤーのカード削除
                for (Card card : selectedCards) {
                    cards.remove(card);
                }

                // 選択しているカードのリセット
                selectedCards.clear();

                state = STATE_END_TURN;
            }
        } else if (str[0].equals("124")) {
            // 行動が拒否されました。

            if (state == STATE_WAIT_REPLY) {

                state = STATE_END_TURN;
            }
        } else if (str[0].equals("125")) {
            // 場にカードが出されました

            fieldCards.add(Card.convertToList(str[1]));
        } else if (str[0].equals("126")) {
            // プレイヤーの残りカード枚数が通知されました

            for (int i = 1; i < str.length; i += 2) {
                playerCardSizes.put(str[i], Integer.parseInt(str[i + 1]));
            }
        } else if (str[0].equals("127")) {
            // 場が流れました。

            fieldCards.clear();
        } else if (str[0].equals("128")) {
            // 場の状態が更新されました。

            fieldState = "";
            if(str.length >= 2){
                fieldState = str[1];
            }
        } else if (str[0].equals("130")) {
            // プレイヤーが上がりました。

            playerRanks.put(str[1], Integer.parseInt(str[2]));
        } else if (str[0].equals("140")) {
            // ゲームが終了しました。

            state = STATE_END_GAME;
            playerNameForTurn = "";
        }
    }

    /**
     * プレイヤーが現在所持しているカードのリストを取得する。
     * 
     * @return
     */
    public List<Card> getCards() {
        return cards;
    }

    public List<Card> getSelecteCards() {
        return selectedCards;
    }

    public List<List<Card>> getFieldCards() {
        return fieldCards;
    }

    public String getFieldState(){
        return fieldState;
    }

    public Map<String, Integer> getPlayerCardSizes() {
        return playerCardSizes;
    }

    public Map<String, Integer> getPlayerRanks() {
        return playerRanks;
    }

    public int getState() {
        return state;
    }

    /**
     * カードを選択もしくは選択解除します。
     * 
     * @param card
     */
    public synchronized void select(Card card) {
        if ((state == STATE_GAME || state == STATE_TURN)  && cards.contains(card)) {
            if (selectedCards.contains(card)) {
                selectedCards.remove(card);
            } else {
                selectedCards.add(card);
            }
        }
    }

    /**
     * カードを出します。
     */
    public synchronized void put() {
        if (state == STATE_TURN && checkSelectedCards()) {
            state = STATE_WAIT_REPLY;
            send("121 " + Card.convertToCodes(selectedCards));
        }
    }

    /**
     * このターンをパスします。
     */
    public synchronized void pass() {
        if (state == STATE_TURN) {
            selectedCards.clear();
            state = STATE_WAIT_REPLY;
            send("122");
        }
    }

    /**
     * 選択しているカードを場に出せるか調べます。
     */
    public boolean checkSelectedCards() {
        return DaifugoTool.checkPutField(fieldCards, fieldState, selectedCards) != null;
    }
}
