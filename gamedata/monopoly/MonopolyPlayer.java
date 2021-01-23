package gamedata.monopoly;

import source.CGPlayer;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MonopolyPlayer extends CGPlayer {
    private MonopolyBoard board = new MonopolyBoard();
    private PlayerState state = PlayerState.READY;
    private int[] dice = { 0, 0 };
    private boolean tradeOffer = false;
    private boolean tradeAgreement = false;
    private int tradeMoney = 0;
    private Set<Integer> tradeLands = new HashSet<>();
    private Map<String, String> tradeContents = new HashMap<>(); // 取引を出した人、取引内容
    private Map<String, String> tradeAgreements = new HashMap<>(); // 合意した人、合意先

    enum PlayerState {
        READY, GAME, MY_TURN_START, MY_DICE_ROLLING, MY_POSITION_MOVED, MY_JAIL_START, MY_JAIL_ACTION_SELECTED,
        MY_JAIL_BEFORE_DICE_ROLL, MY_ACTION_SELECTED, AUCTION, END_AUCTION, BANKRUPTCY, END_GAME;
    }

    @Override
    public synchronized void listener(String data) {
        String[] str = data.split(" ");

        // プレイヤーリスト受信
        if (str[0].equals("110")) {
            for (int i = 1; i < str.length; i++) {
                playerNames.add(str[i]);
            }
            board.setPlayers(playerNames);
            return;
        }

        // プレイヤーの場所受信
        if (str[0].equals("111")) {
            for (int i = 1; i < str.length; i += 2) {
                board.setPlayerPosition(str[i], Integer.parseInt(str[i + 1]));
                if (str[i].equals(name) && state == PlayerState.MY_DICE_ROLLING) {
                    state = PlayerState.MY_POSITION_MOVED;
                }
            }
            return;
        }

        // プレイヤーの破産受信
        if (str[0].equals("113")) {
            board.setPlayerBankrupt(str[1]);
            return;
        }

        // プレイヤー所持金受信
        if (str[0].equals("130")) {
            for (int i = 1; i < str.length; i += 2) {
                board.setPlayerMoney(str[i], Integer.parseInt(str[i + 1]));
            }
            return;
        }

        // プレイヤーの土地受信
        if (str[0].equals("131")) {
            for (int i = 2; i < str.length; i++) {
                board.setOwner(Integer.parseInt(str[i]), str[1]);
            }
            return;
        }

        // 土地の建物受信
        if (str[0].equals("132")) {
            board.setBuilding(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
            return;
        }

        // 土地の抵当受信
        if (str[0].equals("133")) {
            if (Integer.parseInt(str[2]) == 1) {
                board.mortgage(Integer.parseInt(str[1]));
            } else if (Integer.parseInt(str[2]) == 0) {
                board.unmortgage(Integer.parseInt(str[1]));
            }
            return;
        }

        // ターンのプレイヤー受信
        if (str[0].equals("120")) {
            playerNameForTurn = str[1];
            if (playerNameForTurn.equals(name)) {
                // 自分のターン
                if (state != PlayerState.MY_JAIL_ACTION_SELECTED
                        && board.getPlayerPosition(name) == MonopolyBoard.JAIL) {
                    state = PlayerState.MY_JAIL_START;
                } else {
                    state = PlayerState.MY_TURN_START;
                }
            } else {
                // 他人のターン
                state = PlayerState.GAME;
            }
            return;
        }

        // 刑務所でサイコロ
        if (str[0].equals("172")) {
            state = PlayerState.MY_JAIL_BEFORE_DICE_ROLL;
            return;
        }

        // サイコロを振った
        if (str[0].equals("122")) {
            dice[0] = Integer.parseInt(str[1]);
            dice[1] = Integer.parseInt(str[2]);
            if (state == PlayerState.MY_JAIL_BEFORE_DICE_ROLL) {
                state = PlayerState.MY_POSITION_MOVED;
            }
            return;
        }

        // 取引設定
        if (str[0].equals("151")) {
            if (str.length == 2) {
                tradeContents.remove(str[1]);
                tradeAgreements.remove(str[1]);
                if (str[1].equals(name)) {
                    if (tradeOffer) {
                        tradeMoney = 0;
                        tradeLands.clear();
                        tradeAgreement = false;
                        tradeOffer = false;
                    }
                }
            } else {
                String[] str1 = data.split(" ", 3);
                tradeContents.put(str[1], str1[2]);
            }
            return;
        }

        // 取引同意
        if (str[0].equals("153")) {
            tradeAgreements.put(str[1], str[2]);
            return;
        }

        // 取引同意解除
        if (str[0].equals("154")) {
            tradeAgreements.remove(str[1]);
            if (str[1].equals(name)) {
                tradeAgreement = false;
            }
            return;
        }

    }

    void rollDice() {
        PlayerState preState = state;
        state = PlayerState.MY_DICE_ROLLING;
        if (preState == PlayerState.MY_JAIL_BEFORE_DICE_ROLL) {
            send("173");
        } else {
            send("121");
        }
    }

    void leavePrison(boolean b) {
        state = PlayerState.MY_JAIL_ACTION_SELECTED;
        if (b) {
            send("170");
        } else {
            send("171");
        }
    }

    void buyLand(boolean b) {
        state = PlayerState.MY_ACTION_SELECTED;
        if (b) {
            send("123");
        } else {
            send("124");
        }
    }

    MonopolyBoard getBoard() {
        return board;
    }

    PlayerState getState() {
        return state;
    }

    int[] getDice() {
        return dice;
    }

    Set<Integer> getTradeLands() {
        return tradeLands;
    }

    boolean isOfferTrade() {
        return tradeOffer;
    }

    boolean isAgreeTrade() {
        return tradeAgreement;
    }

    void setTrade() {
        String str = "";
        for (int land : tradeLands) {
            str += " " + land;
        }
        send("150 " + tradeMoney + str);
        tradeOffer = true;
    }

    void addTradeLand(int land) {
        tradeLands.add(land);
    }

    void removeTradeLand(int land) {
        tradeLands.remove(land);
    }

    boolean isLandTrade(int land) {
        return tradeLands.contains(land);
    }

    int getTradeMoney() {
        return tradeMoney;
    }

    void setTradeMoney(int money) {
        tradeMoney = money;
    }

    void addTradeMoney(int tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    void agreeTrade(String name) {
        tradeAgreement = true;
        send("152 " + name);
    }

    void resetTrade() {
        send("150");
        tradeOffer = false;
        tradeAgreement = false;
    }

    Map<String, String> getTradeContents() {
        return tradeContents;
    }

    Map<String, String> getTradeAgreements() {
        return tradeAgreements;
    }

    boolean canBuild(int land) {
        return name.equals(board.getOwner(land)) && board.canBuild(land)
                && board.getPlayerMoney(name) - board.getBuildCost(land) >= 0;
    }

    void build(int land) {
        send("180 " + land);
    }

    boolean canUnbuild(int land) {
        return name.equals(board.getOwner(land)) && board.canUnbuild(land);
    }

    void unbuild(int land) {
        send("181 " + land);
    }

    boolean canMortgage(int land) {
        return name.equals(board.getOwner(land)) && board.canMortgage(land);
    }

    void mortgage(int land) {
        send("160 " + land);
    }

    boolean canUnmortgage(int land) {
        return name.equals(board.getOwner(land)) && board.canUnmortgage(land)
                && board.getPlayerMoney(name) - (int) Math.ceil(0.55 * board.getPrice(land)) >= 0;
    }

    void unmortgage(int land) {
        send("161 " + land);
    }

    void goBankrupt() {
        state = PlayerState.BANKRUPTCY;
        send("112");
    }

}
