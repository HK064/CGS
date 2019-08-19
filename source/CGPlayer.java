package source;

import java.util.List;

import source.display.PlayPanel;

public class CGPlayer {
    protected List<String> playerNames;
    private CGSetupPlayer player;
    protected String name;
    protected PlayPanel panel;

    public final void setup(CGSetupPlayer player, String name){
        this.player = player;
        this.name = name;
    }

    public void startGame(){

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
    void listener(String data){

    }

}
