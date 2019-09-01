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
    //建設
    if (str[0].equals("180")) {
      int land = Integer.parseInt(str[1]);
      if(state!=ServerState.READY && state!=ServerState.AUCTION && state!=ServerState.END_GAME) {
        if(name==board.getOwner(land)&&board.canBuild(land)) {
          int price = board.getBuildCost(land);
          if(board.getPlayerMoney(name)-price>=0) {
            board.build(land);
            board.payPlayerMoney(name,price);
            sendAll("130 "+name+" "+board.getPlayerMoney(name));
            sendAll("132 "+land+" "+board.getBuilding(land));
          }
        }
      }
    }
    //解体
    if(str[0].equals("181")) {
      int land = Integer.parseInt(str[1]);
      if(state!=ServerState.READY && state!=ServerState.AUCTION && state!=ServerState.END_GAME) {
        if(name==board.getOwner(land) && board.canUnbuild(land)) {
          int price = board.getBuildCost(land)/2;
          board.unbuild(land);
          board.addPlayerMoney(name,price);
          sendAll("130 "+name+" "+board.getPlayerMoney(name));
          sendAll("132 "+land+" "+board.getBuilding(land));
        }
      }
    }
    //抵当
    if(str[0].equals("160")) {
      int land = Integer.parseInt(str[1]);
      if(state!=ServerState.READY && state!=ServerState.AUCTION && state!=ServerState.END_GAME) {
        if(name==board.getOwner(land) && !board.isMortgage(land)) {
          int price = board.getPrice(land)/2;
          board.mortgage(land);
          board.addPlayerMoney(name,price);
          sendAll("133 "+land+" 1");
          sendAll("130 "+name+" "+board.getPlayerMoney(name));
        }
      }
    }
    //抵当解除
    if(str[0].equals("161")){
      int land = Integer.parseInt(str[1]);
      if(state!=ServerState.READY && state!=ServerState.AUCTION && state!=ServerState.END_GAME) {
        if(name==board.getOwner(land) && board.isMortgage(land)) {
          int price = board.getPrice(land)/2;
          price+=Math.ceil(0.1*board.getPrice(land));
          if(board.getPlayerMoney(name)-price>=0) {
            board.unmortgage(land);
            board.payPlayerMoney(name,price);
            sendAll("133 "+land+" 0");
            sendAll("130 "+name+" "+board.getPlayerMoney(name));
          }
        }
      }
    }
  }

}
