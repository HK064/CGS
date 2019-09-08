package gamedata.monopoly;

import source.CGPlayer;
import java.util.Set;
import java.util.HashSet;

public class MonopolyPlayer extends CGPlayer {
    private MonopolyBoard board = new MonopolyBoard();
    private PlayerState state = PlayerState.READY;
    private int[] dice = { 0, 0 };
    private Set<Integer> trade = new HashSet<>();
    private int tradeMoney = 0;

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
        }

        // サイコロを振った
        if (str[0].equals("122")) {
            dice[0] = Integer.parseInt(str[1]);
            dice[1] = Integer.parseInt(str[2]);
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
        return trade;
    }

    void setTrade() {
        String str = "";
        for (int land : trade) {
            str += " " + land;
        }
        send("150 " + tradeMoney + str);
    }

    void addTrade(int land) {
        trade.add(land);
    }

    void removeTrade(int land) {
        trade.remove(land);
    }

    boolean isTrade(int land) {
        return trade.contains(land);
    }

    void addTradeMoney(int tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    void agreeTrade(String name) {
        send("152 " + name);
    }

    void resetTrade() {
        send("150 ");
    }

    boolean canBuild(int land) {
        return name.equals(board.getOwner(land)) && board.canBuild(land) && board.getPlayerMoney(name) - board.getBuildCost(land) >= 0;
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
        return name.equals(board.getOwner(land)) && board.canUnmortgage(land) && board.getPlayerMoney(name) - (int) Math.ceil(0.55 * board.getPrice(land)) >= 0;
    }

    void unmortgage(int land) {
        send("161 " + land);
    }

}
