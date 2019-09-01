package gamedata.monopoly;

import source.CGServer;

public class MonopolyServer extends CGServer {
    private MonopolyBoard board = new MonopolyBoard();
    private ServerState state = ServerState.READY;
    private int[] dice = { 0, 0 };

    enum ServerState {
        READY, TURN_START, DICE_ROLLED, ACTION_SELECTED, AUCTION, END_AUCTION, END_GAME;
    }

    @Override
    public void startGame() {

        // プレイヤーの順番
        shufflePlayers();
        String str = "110";
        for (String name : playerNames) {
            str += " " + name;
        }
        sendAll(str);
        board.setPlayers(playerNames);

        // プレイヤーの初期位置
        str = "111";
        for (String name : playerNames) {
            str += " " + name + " 0";
            board.setPlayerPosition(name, 0);
        }
        sendAll(str);

        // プレイヤーの初期所持金
        str = "130";
        for (String name : playerNames) {
            str += " " + name + " 1500";
            board.setPlayerMoney(name, 1500);
        }
        sendAll(str);

    }

    @Override
    public void listener(String name, String data) {
        String[] str = data.split(" ");
        if (str[0].equals("180")) {
          int land = Integer.parseInt(str[1]);
          if(state!=ServerState.READY&&state!=ServerState.AUCTION&&state!=ServerState.END_GAME){
            if(board.canBuild(land)){
              int price = board.getBuildCost(land);
              if(board.getPlayerMoney(name)-price>0){
                board.build(land);
                board.payPlayerMoney(name,price);
                sendAll("130 "+name+" "+board.getPlayerMoney(name));
                sendAll("132 "+land+" "+board.getBuilding(land));
              }
            }
          }
        }
    }

}
