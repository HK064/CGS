package source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CGServer {
    protected List<String> playerNames;
    private CGSetupServer server;
    protected String playerNameForTurn;
    protected Random random = new Random();

    final void setup(List<String> playerNames, CGSetupServer server){
        this.playerNames = new ArrayList<>(playerNames);
        this.server = server;
    }

    public void startGame(){
        
    }

    protected void shufflePlayers(){
        Collections.shuffle(playerNames, random);
        playerNameForTurn = playerNames.get(0);
    }

    protected void nextPlayer(){
        int i = 1 + playerNames.indexOf(playerNameForTurn);
        if(i >= playerNames.size()){
            i = 0;
        }
        playerNameForTurn = playerNames.get(i);
    }

    /**
     * 参加者全員にメッセージを送る。
     * @param data
     */
    protected void sendAll(String data){
        server.sendAll(data);
    }

    /**
     * １人にメッセージを送る。
     * @param name 送りたいプレイヤーの名前
     * @param data
     */
    protected void sendOne(String name, String data){
        server.sendOne(name, data);
    }

    /**
     * メッセージを受信したときに呼ばれる。
     * @param name 送信元の名前
     * @param data 内容
     */
    public void listener(String name, String data){

    }

}
