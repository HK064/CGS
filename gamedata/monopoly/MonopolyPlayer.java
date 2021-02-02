package gamedata.monopoly;

import source.CGPlayer;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

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
    private ArrayList<String> history = new ArrayList<String>();

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
                int prev = board.getPlayerMoney(str[i]);
                board.setPlayerMoney(str[i], Integer.parseInt(str[i + 1]));
                history.add(str[i]+"の所持金が"+prev+"から"+str[i + 1]+"に");
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

        //カード受信
        if (str[0].equals("190")) {
            if(str[2].equals("chance")){
                str[1] = "[chanceカード]"+str[1];
                switch(str[3]){
                    case "0":
                        history.add(str[1]+"建物ローンの満期で＄150受け取る");
                        break;
                    case "1":
                        history.add(str[1]+"銀行より利息＄50受け取る");
                        break;
                    case "2":
                        history.add(str[1]+"GOのマスに進み，＄200受け取る");
                        break;
                    case "3":
                        history.add(str[1]+"委員会の委員長に選任され，各プレイヤーに＄50支払う");
                        break;
                    case "4":
                        history.add(str[1]+"スピード違反で＄15支払う");
                        break;
                    case "5":
                        history.add(str[1]+"全財産の修理費，家1件当たり＄25，ホテル1件当たり＄125支払う");
                        break;
                    case "6":
                        history.add(str[1]+"函館へ進む");
                        break;
                    case "7":
                        history.add(str[1]+"静岡へ進む");
                        break;
                    case "8":
                        history.add(str[1]+"山口へ進む");
                        break;
                    case "9":
                        history.add(str[1]+"JR九州に進む");
                        break;
                    case "10":
                        history.add(str[1]+"次の鉄道会社まで進む．所有者がいた場合には，通常の2倍のレンタル料を支払う");
                        break;
                    case "11":
                        history.add(str[1]+"次の水道会社か電力会社に進む．所有者がいた場合には，サイコロの目の10倍を支払う");
                        break;
                    case "12":
                        history.add(str[1]+"刑務所へ行く（GOマスを通っても＄200は受け取れない）");
                        break;
                    case "13":
                        history.add(str[1]+"3マス戻る");
                        break;
                }
            }else if(str[2].equals("community")){
                str[1] = "[共同基金カード]"+str[1];
                switch(str[3]){
                    case "0":
                        history.add(str[1]+"生命保険満期により＄100受け取る");
                        break;
                    case "1":
                        history.add(str[1]+"ビューティーコンテスト準優勝．＄10受け取る");
                        break;
                    case "2":
                        history.add(str[1]+"コンサルタント料金として＄25受け取る");
                        break;
                    case "3":
                        history.add(str[1]+"遺産＄100受け取る");
                        break;
                    case "4":
                        history.add(str[1]+"誕生日祝いとして，全てのプレイヤーから＄10受け取る");
                        break;
                    case "5":
                        history.add(str[1]+"休日基金の満期により＄100受け取る");
                        break;
                    case "6":
                        history.add(str[1]+"株式売却により＄50受け取る");
                        break;
                    case "7":
                        history.add(str[1]+"銀行の手違いで＄200受け取る");
                        break;
                    case "8":
                        history.add(str[1]+"所得税の払戻金＄20受け取る");
                        break;
                    case "9":
                        history.add(str[1]+"GOのマスに進み，＄200受け取る");
                        break;
                    case "10":
                        history.add(str[1]+"入院費＄100支払う");
                        break;
                    case "11":
                        history.add(str[1]+"教育費として＄50支払う");
                        break;
                    case "12":
                        history.add(str[1]+"治療費として＄50支払う");
                        break;
                    case "13":
                        history.add(str[1]+"全財産の修理費，家1件当たり＄25，ホテル1件当たり＄125支払う");
                        break;
                    case "14":
                        history.add(str[1]+"刑務所へ行く（GOマスを通っても＄200は受け取れない");
                        break;

                }
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

    ArrayList<String> getHistory(){
        return history;
    }

}
