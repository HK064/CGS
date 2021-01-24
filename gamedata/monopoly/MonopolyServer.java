package gamedata.monopoly;

import source.CGServer;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonopolyServer extends CGServer {
    private MonopolyBoard board = new MonopolyBoard();
    private ServerState state = ServerState.READY;
    private int[] dice = { 0, 0 };
    private int count = 0;
    private boolean zorome = false;
    private HashMap<String, String> trade = new HashMap<>();// 取引を出した人、取引内容
    private HashMap<String, String> agree = new HashMap<>();// 合意した人、合意先
    private HashMap<String, Integer> playerJailTurn = new HashMap<>();
    private int auctionLand = -1;
    private List<Integer> communityCard = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,6));
    private List<Integer> chanceCard = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,9,10,11,12,1,13));

    enum ServerState {
        READY, TURN_START, DICE_ROLLED, ACTION_SELECTED, AUCTION, END_AUCTION, END_GAME, JAIL_START, MONEY_NEGATIVE;
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

        state = ServerState.TURN_START;
        sendAll("120 " + playerNameForTurn);

        //カードのシャッフル
        Collections.shuffle(communityCard);
        Collections.shuffle(chanceCard);
    }

    @Override
    protected void nextPlayer() {
        do {
            super.nextPlayer();
        } while (board.isPlayerBankrupt(playerNameForTurn));
    }

    @Override
    public synchronized void listener(String name, String data) {
        String[] str = data.split(" ");

        // leavePrison
        if (str[0].equals("170")) {
            state = ServerState.JAIL_START;
            board.payPlayerMoney(name, 50);
            sendAll("130 " + name + " " + board.getPlayerMoney(name));
            board.setPlayerPosition(name, 10);
            sendAll("111 " + name + " " + board.getPlayerPosition(name));
            state = ServerState.TURN_START;
            sendAll("120 " + name);
        }
        if (str[0].equals("171")) {
            state = ServerState.JAIL_START;
            sendOne(name, "172");
        }

        // prisonBreak
        if (str[0].equals("173")) {
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    state = ServerState.DICE_ROLLED;
                    dice[0] = random.nextInt(6) + 1;
                    dice[1] = random.nextInt(6) + 1;
                    int jailCount = 0;
                    if (playerJailTurn.containsKey(name)) {
                        jailCount = playerJailTurn.get(name);
                    }
                    sendAll("122 " + dice[0] + " " + dice[1]);
                    if (dice[0] == dice[1]) {
                        board.setPlayerPosition(name, 10);
                        sendAll("111 " + name + " " + board.getPlayerPosition(name));
                        board.setPlayerPosition(name, board.getPlayerPosition(name) + dice[0] + dice[1]);
                        sendAll("111 " + name + " " + board.getPlayerPosition(name));
                        playerJailTurn.remove(name);
                        doEvent(name, board.getPlayerPosition(name));
                    } else {
                        if (jailCount == 2) {
                            board.setPlayerPosition(name, 10);
                            sendAll("111 " + name + " " + board.getPlayerPosition(name));
                            board.payPlayerMoney(name, 50);
                            sendAll("130 " + name + " " + board.getPlayerMoney(name));
                            //TODO


                            board.setPlayerPosition(name, board.getPlayerPosition(name) + dice[0] + dice[1]);
                            sendAll("111 " + name + " " + board.getPlayerPosition(name));
                            playerJailTurn.remove(name);
                            doEvent(name, board.getPlayerPosition(name));
                        } else {
                            playerJailTurn.put(name, jailCount + 1);
                            endTurn();
                        }
                    }
                }
            }, 1000);
        }

        // rollDice
        if (str[0].equals("121") && name.equals(playerNameForTurn)) {
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    playTurn(name);
                }
            }, 1000);
            return;
        }

        // 土地を買う
        if (str[0].equals("123") && state == ServerState.DICE_ROLLED && name.equals(playerNameForTurn)) {
            if (board.getPlayerMoney(name) - board.getPrice(board.getPlayerPosition(name)) < 0) {
                return;
            }
            board.payPlayerMoney(name, board.getPrice(board.getPlayerPosition(name)));
            board.setOwner(board.getPlayerPosition(name), name);
            state = ServerState.ACTION_SELECTED;
            sendAll("130 " + name + " " + board.getPlayerMoney(name));
            sendAll("131 " + name + " " + board.getPlayerPosition(name));
            endTurn();
            return;
        }

        // 土地を買わない
        if (str[0].equals("124") && state == ServerState.DICE_ROLLED && name.equals(playerNameForTurn)) {
            state = ServerState.AUCTION;

            // TODO

            endTurn();
            return;
        }

        // 建設
        if (str[0].equals("180")) {
            int land = Integer.parseInt(str[1]);
            if (state != ServerState.READY && state != ServerState.AUCTION && state != ServerState.END_GAME) {
                if (name.equals(board.getOwner(land)) && board.canBuild(land)) {
                    int price = board.getBuildCost(land);
                    if (board.getPlayerMoney(name) - price >= 0) {
                        board.build(land);
                        board.payPlayerMoney(name, price);
                        sendAll("130 " + name + " " + board.getPlayerMoney(name));
                        sendAll("132 " + land + " " + board.getBuilding(land));
                    }
                }
            }
        }

        // 解体
        if (str[0].equals("181")) {
            int land = Integer.parseInt(str[1]);
            if (state != ServerState.READY && state != ServerState.AUCTION && state != ServerState.END_GAME) {
                if (name.equals(board.getOwner(land)) && board.canUnbuild(land)) {
                    int price = board.getBuildCost(land) / 2;
                    board.unbuild(land);
                    board.addPlayerMoney(name, price);
                    sendAll("130 " + name + " " + board.getPlayerMoney(name));
                    sendAll("132 " + land + " " + board.getBuilding(land));

                    checkLeaveMoneyNegative();
                }
            }
            if (state == ServerState.MONEY_NEGATIVE && board.getPlayerMoney(name) >= 0){
                endTurn();
            }
        }

        // 抵当
        if (str[0].equals("160")) {
            int land = Integer.parseInt(str[1]);
            if (state != ServerState.READY && state != ServerState.AUCTION && state != ServerState.END_GAME) {
                if (name.equals(board.getOwner(land)) && !board.isMortgage(land)) {
                    int price = board.getPrice(land) / 2;
                    board.mortgage(land);
                    board.addPlayerMoney(name, price);
                    sendAll("133 " + land + " 1");
                    sendAll("130 " + name + " " + board.getPlayerMoney(name));

                    checkLeaveMoneyNegative();
                }
            }
            if (state == ServerState.MONEY_NEGATIVE && board.getPlayerMoney(name) >= 0){
                endTurn();
            }
        }

        // 抵当解除
        if (str[0].equals("161")) {
            int land = Integer.parseInt(str[1]);
            if (state != ServerState.READY && state != ServerState.AUCTION && state != ServerState.END_GAME) {
                if (name.equals(board.getOwner(land)) && board.isMortgage(land)) {
                    int price = (int) Math.ceil(0.55 * board.getPrice(land));
                    price += Math.ceil(0.1 * board.getPrice(land));
                    if (board.getPlayerMoney(name) - price >= 0) {
                        board.unmortgage(land);
                        board.payPlayerMoney(name, price);
                        sendAll("133 " + land + " 0");
                        sendAll("130 " + name + " " + board.getPlayerMoney(name));
                    }
                }
            }
        }

        // 取引内容の設定
        if (str[0].equals("150")) {
            if (str.length == 1) {
                trade.remove(name);
                agree.remove(name);
                sendAll("151 " + name);
            } else {
                trade.put(name, data.substring(4));
                sendAll("151 " + name + data.substring(3));
            }
            for (String n : playerNames) {
                if (name.equals(agree.get(n))) {
                    agree.remove(n);
                    sendAll("154 " + n);
                }
            }
        }

        // 取引の同意
        if (str[0].equals("152")) {
            agree.put(name, str[1]);
            sendAll("153 " + name + " " + str[1]);
            // 取引の成立
            if (name.equals(agree.get(str[1]))) {
                String trade1 = trade.get(name);
                String trade2 = trade.get(str[1]);
                String[] str1 = trade1.split(" ");
                board.addPlayerMoney(str[1], Integer.parseInt(str1[0]));
                board.payPlayerMoney(name, Integer.parseInt(str1[0]));
                String str3 = "";
                for (int i = 1; i < str1.length; i++) {
                    board.setOwner(Integer.parseInt(str1[i]), str[1]);
                    str3 += " " + str1[i];
                }
                String[] str2 = trade2.split(" ");
                board.addPlayerMoney(name, Integer.parseInt(str2[0]));
                board.payPlayerMoney(str[1], Integer.parseInt(str2[0]));
                String str4 = "";
                for (int i = 1; i < str2.length; i++) {
                    board.setOwner(Integer.parseInt(str2[i]), name);
                    str4 += " " + str2[i];
                }
                sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + str[1] + " "
                        + board.getPlayerMoney(str[1]));
                sendAll("131 " + str[1] + str3);
                sendAll("131 " + name + str4);
                sendAll("151 " + name);
                sendAll("151 " + str[1]);
                for (String n : playerNames) {
                    if (name.equals(agree.get(n)) || str[1].equals(agree.get(n))) {
                        agree.remove(n);
                        sendAll("154 " + n);
                    }
                }

                checkLeaveMoneyNegative();
            }
        }

        // 破産
        if (str[0].equals("112")) {
            int price =  board.PlayerTotalassets(name);
            if (price < 0) {
                board.setPlayerBankrupt(name);
                sendAll("113 " + name);

                String name2 = board.getOwner(board.getPlayerPosition(name));
                if (name2 == null) {
                    // プレイヤー以外による破産(すべて初期化)
                    for (int land=0;land<board.LAND_MAX;land++){
                        if (board.getOwner(land) != null) {
                            if (name.equals(board.getOwner(land))){
                                board.setBuilding(land,0);
                                board.unmortgage(land);
                                board.setOwner(land,"null");
                                sendAll("132 " + land + " " + board.getBuilding(land));
                                sendAll("133 " + land + " " + 0);
                                sendAll("131 " + "null" + " " + land);
                            }
                        }
                    }
                } else {
                    // プレイヤーによる破産
                    for (int land=0;land<board.LAND_MAX;land++){
                        if (board.getOwner(land) != null) {
                            if (name.equals(board.getOwner(land))){
                                board.setBuilding(land,0);
                                board.mortgage(land);
                                board.setOwner(land,name2);
                                sendAll("132 " + land + " " + board.getBuilding(land));
                                sendAll("133 " + land + " " + 1);
                                sendAll("131 " + name2 + " " + land);
                            }
                        }
                    }
                    board.addPlayerMoney(name2,price);
                    sendAll("130 " + name2 + " " + board.getPlayerMoney(name2));
                }
                endTurn();
            }
        }
    }

    void playTurn(String name) {
        state = ServerState.DICE_ROLLED;
        dice[0] = random.nextInt(6) + 1;
        dice[1] = random.nextInt(6) + 1;
        if (dice[0] == dice[1]) {
            zorome = true;
            count++;
        } else {
            zorome = false;
            count = 0;
        }
        sendAll("122 " + dice[0] + " " + dice[1]);
        // 刑務所にいる
        if (board.getType(board.getPlayerPosition(name)) == MonopolyBoard.LandType.JAIL) {

        } else {
            // スピード違反
            if (dice[0] == dice[1] && count == 3) {
                goJail(name);
                return;
            } else {
                int position = board.getPlayerPosition(name) + dice[0] + dice[1];
                // 一周回ったとき
                if (position >= 40) {
                    position -= 40;
                    board.addPlayerMoney(name, 200);
                    sendAll("130 " + name + " " + board.getPlayerMoney(name));
                }
                board.setPlayerPosition(name, position);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // イベント
                doEvent(name, position);
            }
        }
    }

    void doEvent(String name, int position) {
        switch (board.getType(position)) {
        case GO_JAIL:
            goJail(name);
            return;
        case COMMUNITY_CARD:
            getComunityCard(name, position);
            return;
        case CHANCE_CARD:
            getChanceCard(name, position);
            return;
        case INCOME_TAX:
            board.payPlayerMoney(name, 200);
            sendAll("130 " + name + " " + board.getPlayerMoney(name));
            endTurn();
            return;
        case LUXURY_TAX:
            board.payPlayerMoney(name, 100);
            sendAll("130 " + name + " " + board.getPlayerMoney(name));
            endTurn();
            return;
        case RAILROAD:
        case COMPANY:
        case PROPERTY:
            goLand(name, position);
            return;
        default:
            endTurn();
            return;
        }
    }

    void goJail(String name) {
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                board.setPlayerPosition(name, MonopolyBoard.JAIL);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                zorome = false;
                count = 0;
                state = ServerState.TURN_START;
                nextPlayer();
                sendAll("120 " + playerNameForTurn);
            }
        }, 1000);
    }

    void getChanceCard(String name, int position) {
        int cardNum = chanceCard.remove(0);
        sendAll("190 " + name + " " + "chance" + " " + cardNum);
        switch(cardNum){
            case 0:
                board.addPlayerMoney(name, 150);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 1:
                board.addPlayerMoney(name, 50);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 2:
                board.setPlayerPosition(name, 0);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                board.addPlayerMoney(name, 200);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 3:
                for (String name2 : playerNames) {
                  if (!name2.equals(name)) {
                    board.addPlayerMoney(name2, 50);
                    sendAll("130 " + name2 + " " + board.getPlayerMoney(name2));
                  }
                }
                board.payPlayerMoney(name,50*(playerNames.size()-1));
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 4:
                board.payPlayerMoney(name,15);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 5:
                for (int i = 0;i < 41;i++) {
                  if (board.getType(i)==MonopolyBoard.LandType.PROPERTY) {
                    if (board.getOwner(i) != null && board.getOwner(i).equals(name)) {
                      board.payPlayerMoney(name,25*board.getBuilding(i));
                    }
                  }
                }
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 6:
                board.setPlayerPosition(name, 39);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // イベント
                doEvent(name,board.getPlayerPosition(name));
                break;
            case 7:
                if (24 < position) {
                  board.addPlayerMoney(name, 200);
                }
                board.setPlayerPosition(name, 24);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // イベント
                doEvent(name,board.getPlayerPosition(name));
                break;
            case 8:
                if (11 < position) {
                  board.addPlayerMoney(name, 200);
                }
                board.setPlayerPosition(name, 11);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // イベント
                doEvent(name,board.getPlayerPosition(name));
                break;
            case 9:
                if (5 < position) {
                  board.addPlayerMoney(name, 200);
                }
                board.setPlayerPosition(name, 5);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // イベント
                doEvent(name,board.getPlayerPosition(name));
                break;
            case 10:
                if (position == 7) {
                  board.setPlayerPosition(name, 15);
                } else if (position == 17) {
                  board.setPlayerPosition(name, 25);
                } else if (position == 22) {
                  board.setPlayerPosition(name, 25);
                } else if (position == 36) {
                  board.setPlayerPosition(name, 5);
                }
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                // 所有者がいないとき
                if (board.getOwner(board.getPlayerPosition(name)) == null) {
                  // イベント
                  doEvent(name,board.getPlayerPosition(name));
                } else if (board.getOwner(board.getPlayerPosition(name)).equals(name)) {
                    endTurn();
                } else {
                    String owner = board.getOwner(board.getPlayerPosition(name));
                    board.payPlayerMoney(name, board.getRent(board.getPlayerPosition(name))*2);
                    board.addPlayerMoney(owner, board.getRent(board.getPlayerPosition(name))*2);
                    sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + owner + " "
                            + board.getPlayerMoney(owner));
                    endTurn();
                }
                break;
            case 11:
                if (position == 7) {
                  board.setPlayerPosition(name, 12);
                } else if (position == 17) {
                  board.setPlayerPosition(name, 28);
                } else if (position == 22) {
                  board.setPlayerPosition(name, 28);
                } else if (position == 36) {
                  board.setPlayerPosition(name, 12);
                }
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                if (board.getOwner(board.getPlayerPosition(name)) != null && !board.getOwner(board.getPlayerPosition(name)).equals(name)) {
                  String owner = board.getOwner(board.getPlayerPosition(name));
                  board.payPlayerMoney(name, (dice[0] + dice[1]) * 10);
                  board.addPlayerMoney(owner, (dice[0] + dice[1]) * 10);
                  sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + owner + " "
                              + board.getPlayerMoney(owner));

                }
                endTurn();
                break;
            case 12:
                board.setPlayerPosition(name, 40);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                endTurn();
                break;
            case 13:
                board.setPlayerPosition(name, position - 3);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                  // イベント
                doEvent(name,board.getPlayerPosition(name));
                break;
        }
        chanceCard.add(cardNum);
        return;
    }

    void getComunityCard(String name, int position) {
        int cardNum = communityCard.remove(0);
        sendAll("190 " + name + " " + "community" + " " + cardNum);
        switch(cardNum){
            case 0:
                board.addPlayerMoney(name, 100);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 1:
                board.addPlayerMoney(name, 10);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 2:
                board.addPlayerMoney(name, 25);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 3:
                board.addPlayerMoney(name, 100);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 4:
                for (String name2 : playerNames) {
                  if (!name2.equals(name)) {
                    board.payPlayerMoney(name2, 10);
                    sendAll("130 " + name2 + " " + board.getPlayerMoney(name2));
                  }
                }
                board.addPlayerMoney(name,10*(playerNames.size()-1));
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 5:
                board.addPlayerMoney(name, 100);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 6:
                board.addPlayerMoney(name, 50);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 7:
                board.addPlayerMoney(name, 200);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 8:
                board.addPlayerMoney(name, 20);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 9:
                board.setPlayerPosition(name, 0);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                board.addPlayerMoney(name, 200);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 10:
                board.payPlayerMoney(name, 100);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 11:
                board.payPlayerMoney(name, 50);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 12:
                board.payPlayerMoney(name, 50);
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 13:
                for (int i = 0;i < 41;i++) {
                  if (board.getType(i)==MonopolyBoard.LandType.PROPERTY) {
                    if (board.getOwner(i) != null && board.getOwner(i).equals(name)) {
                      board.payPlayerMoney(name,25*board.getBuilding(i));
                    }
                  }
                }
                sendAll("130 " + name + " " + board.getPlayerMoney(name));
                endTurn();
                break;
            case 14:
                board.setPlayerPosition(name, 40);
                sendAll("111 " + name + " " + board.getPlayerPosition(name));
                zorome = false;
                endTurn();
                break;
        }
        communityCard.add(cardNum);
        return;
    }

    void goLand(String name, int position) {
        // 所有者がいないとき
        if (board.getOwner(position) == null) {
            return;
        } else if (board.getOwner(position).equals(name)) {
            endTurn();
        } else {
            String owner = board.getOwner(position);
            // 公共会社かそれ以外
            if (board.getType(position) == MonopolyBoard.LandType.COMPANY) {
                // 公共会社が占有されていた場合
                if (Objects.equals(board.getOwner(28), board.getOwner(12))) {
                    board.payPlayerMoney(name, (dice[0] + dice[1]) * 10);
                    board.addPlayerMoney(owner, (dice[0] + dice[1]) * 10);
                    sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + owner + " "
                            + board.getPlayerMoney(owner));
                } else {
                    board.payPlayerMoney(name, (dice[0] + dice[1]) * 4);
                    board.addPlayerMoney(owner, (dice[0] + dice[1]) * 4);
                    sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + owner + " "
                            + board.getPlayerMoney(owner));
                }
            } else {
                board.payPlayerMoney(name, board.getRent(position));
                board.addPlayerMoney(owner, board.getRent(position));
                sendAll("130 " + name + " " + board.getPlayerMoney(name) + " " + owner + " "
                        + board.getPlayerMoney(owner));
            }
            endTurn();
        }
    }

    void checkLeaveMoneyNegative() {
        if (state == ServerState.MONEY_NEGATIVE && board.getPlayerMoney(playerNameForTurn) >= 0) {
            endTurn();
        }
    }

    void endTurn() {
        for (String name : playerNames) {
            if (board.getPlayerMoney(name) < 0 && !board.isPlayerBankrupt(name)) {
                state = ServerState.MONEY_NEGATIVE;
                return;
            }
        }
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                if (zorome == false) {
                    count = 0;
                    state = ServerState.TURN_START;
                    nextPlayer();
                    sendAll("120 " + playerNameForTurn);
                } else {
                    state = ServerState.TURN_START;
                    sendAll("120 " + playerNameForTurn);
                }
            }
        }, 1000);
    }

    void startAuction(int land) {
        state = ServerState.AUCTION;
    }

}
