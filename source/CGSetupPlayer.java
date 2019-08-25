package source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import source.display.SetupPanel;
import source.file.Gamedata;

public class CGSetupPlayer{
    private String name;
    private CGConnector connector;
    private List<String> playerNames = new ArrayList<>();
    private Map<String, Integer> playerStates = new HashMap<>();
    private CGPlayer player;
    private int serverState = 0;
    public static final int STATE_NOT_READY = 1;
    public static final int STATE_READY = 2;
    private int countdownCount;
    private SetupPanel panel;
    private Gamedata gamedata = null;

    /**
     * ローカルプレイヤー
     * @param name
     * @param server
     */
    public CGSetupPlayer(String name, CGSetupServer server){
        this(name);

        connector = new CGConnector(server, this);
        server.addLocalPlayer(connector, name);

    }

    /**
     * オンラインプレイヤー
     * @param name
     * @param address
     */
    public CGSetupPlayer(String name, String address){
        this(name);

        connector = new CGConnector(this, address);

        String str = connector.listen();
        if(str.equals("010")){
            connector.send(name);
            str = connector.listen();
            if(str.equals("020")){

            } else {
                connector.close();
            }
        } else {
            connector.close();
        }

        // 受信を待機するスレッド
        Thread thread = new Thread(connector);
        thread.start();
    }

    private CGSetupPlayer(String name){
        // TODO checkName(name)
        this.name = name;
        addPlayer(name);
        serverState = CGSetupServer.STATE_OPEN;
    }

    public void setSetupPanel(SetupPanel panel){
        this.panel = panel;
    }

    /**
     * 名前を取得する。
     * @return
     */
    public String getName(){
        return name;
    }

    /**
     * 状態を返す。
     * ※ CGSetupServer.STATE_* を参照してください。
     * @return
     */
    public int getServerState(){
        return serverState;
    }

    public int getCountdownCount(){
        return countdownCount;
    }

    public Gamedata getGamedata(){
        return gamedata;
    }

    /**
     * プレイヤーのリストを返す。
     * ※ 自分も含みます。
     * @return
     */
    public List<String> getPlayerNames(){
        return playerNames;
    }

    /**
     * プレイヤー達の状態を返す。
     * ※ 自分も含みます。
     * ※ CGSetupPlayer.STATE_* を参照してください。
     * @return プレイヤーの名前をキーとするマップ
     */
    public Map<String, Integer> getPlayerStates(){
        return playerStates;
    }

    /**
     * プレイヤーを追加する。
     * ※ 未追加のプレイヤーに限る。
     * @param name
     */
    private void addPlayer(String name){
        playerNames.add(name);
        playerStates.put(name, STATE_NOT_READY);
    }

    /*
    public void setGamedata(Gamedata gamedata){
        send("050 " + gamedata.getFolderName());
    }
    */

    /**
     * ゲームを開始する。
     */
    private void startGame(){
        player = gamedata.newPlayerInstance();
        player.setup(this, getName());
        panel.startGame(player);
    }

    public void sendReady(){
        connector.send("040");
    }

    public void sendNotReady(){
        connector.send("041");
    }

    /**
     * メッセージを送る。
     * @param data
     */
    protected void send(String data){
        connector.send(data);
    }

    /**
     * メッセージを受信したときに呼ばれる。
     */
    void listener(String data){
        String[] str = data.split(" ");
        if((serverState == CGSetupServer.STATE_OPEN) || (serverState == CGSetupServer.STATE_COUNTDOWN)){
            if(str[0].equals("031")){
                // プレイヤーリスト受信

                for(int i = 1; i < str.length; i++){
                    if(!playerNames.contains(str[i])){
                        addPlayer(str[i]);
                    }
                }
            } else if(str[0].equals("032")){
                // プレイヤー状態受信

                for(int i = 1; i < str.length; i += 2){
                    if(playerNames.contains(str[i])){                
                        playerStates.put(str[i], Integer.parseInt(str[i + 1]));
                    }
                }
            } else if(str[0].equals("033")){
                // ゲーム開始までのカウントダウン

                countdownCount = Integer.parseInt(str[1]);
                if(countdownCount == -1){
                    serverState = CGSetupServer.STATE_OPEN;
                } else {
                    serverState = CGSetupServer.STATE_COUNTDOWN;
                }
            } else if(str[0].equals("037")){
                serverState = CGSetupServer.STATE_GAME;
                startGame();
            } else if(str[0].equals("050")){
                gamedata = Gamedata.getGamedata(str[1]);
            }
        } else if(serverState == CGSetupServer.STATE_GAME){
            player.listener(data);
        }
    }

    /**
     * 名前を使ってもよいかチェックする。
     * 1文字以上・スペースの禁止
     * @return
     */
    public static boolean checkName(String name){
        return ((name.length() >= 1) && (!name.contains(" ")));
    }

}
