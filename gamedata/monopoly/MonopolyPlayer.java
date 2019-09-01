package gamedata.monopoly;

import source.CGPlayer;

public class MonopolyPlayer extends CGPlayer {
    private MonopolyBoard board = new MonopolyBoard();
    private PlayerState state = PlayerState.READY;
    private int[] dice = { 0, 0 };

    enum PlayerState {
        READY, GAME, MY_TURN_START, MY_DICE_ROLLED, MY_POSITION_MOVED, AUCTION, END_AUCTION, BANKRUPTCY, END_GAME;
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
            }
            return;
        }

        // プレイヤー所持金受信
        if (str[0].equals("130")) {
            for (int i = 1; i < str.length; i += 2) {
                board.setPlayerMoney(str[i], Integer.parseInt(str[i + 1]));
            }
            return;
        }

        // ターンのプレイヤー受信
        if (str[0].equals("120")) {
            playerNameForTurn = str[1];
            if (playerNameForTurn.equals(name)) {
                // 自分のターン
                state = PlayerState.MY_TURN_START;
            } else {
                // 他人のターン
                state = PlayerState.GAME;
            }
            return;
        }

        // サイコロを振った
        if (str[0].equals("122")) {
            dice[0] = Integer.parseInt(str[1]);
            dice[1] = Integer.parseInt(str[2]);
            if (playerNameForTurn.equals(name)) {
                state = PlayerState.MY_DICE_ROLLED;
            }
            return;
        }
    }

    void rollDice(){
      state = PlayerState.MY_DICE_ROLLED;
      send("121");
    }

    MonopolyBoard getBoard() {
        return board;
    }
}
