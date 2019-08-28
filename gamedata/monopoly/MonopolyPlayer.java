package gamedata.monopoly;

import source.CGPlayer;

public class MonopolyPlayer extends CGPlayer {
    private MonopolyBoard board = new MonopolyBoard();
    private int[] dice = { 0, 0 };

    @Override
    public synchronized void listener(String data) {
        String[] str = data.split(" ");
        if (str[0].equals("110")) {
            for (int i = 1; i < str.length; i++) {
                playerNames.add(str[i]);
            }
            board.setPlayers(playerNames);
            return;
        }
        if (str[0].equals("111")) {
            for (int i = 1; i < str.length; i += 2) {
                board.setPlayerPosition(str[i], Integer.parseInt(str[i + 1]));
            }
            return;
        }
        if(str[0].equals("130")) {
            for (int i = 1; i < str.length; i += 2) {
                board.setPlayerMoney(str[i], Integer.parseInt(str[i + 1]));
            }
            return;
        }
        if(str[0].equals("120")) {
            playerNameForTurn = str[1];
            if (playerNameForTurn.equals(name)) {

            }
            return;
        }
    }

    MonopolyBoard getBoard() {
        return board;
    }
}
