package gamedata.monopoly;

import source.CGPlayer;

public class MonopolyPlayer extends CGPlayer {
    private MonopolyBoard board = new MonopolyBoard();

    @Override
    public synchronized void listener(String data) {
        String[] str = data.split(" ");
    }

    MonopolyBoard getBoard(){
        return board;
    }
}
