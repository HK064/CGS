package source;

import java.util.ArrayList;
import java.util.List;

public class CGPlayer {
    protected List<String> playerNames = new ArrayList<>();
    private CGSetupPlayer player;
    protected String name;
    protected String playerNameForTurn = null;

    public final void setup(CGSetupPlayer player, String name){
        this.player = player;
        this.name = name;
    }

    public void startGame(){

    }

    public String getName(){
        return name;
    }

    public List<String> getPlayerNames(){
        return playerNames;
    }

    public String getPlayerNameForTurn(){
        return playerNameForTurn;
    }

    /**
     * メッセージを送る。
     * @param data
     */
    protected void send(String data){
        player.send(data);
    }

    /**
     * メッセージを受信したときに呼ばれる。
     * @param data 内容
     */
    public void listener(String data){

    }

}
