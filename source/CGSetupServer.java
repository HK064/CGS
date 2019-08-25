package source;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import source.file.Gamedata;

/**
 * ゲームの進行役と、クライアントの受付を行う。
 */
public class CGSetupServer implements Runnable {
    private List<String> playerNames = new ArrayList<>();
    private Map<String, CGConnector> connectors = new HashMap<>();
    private Map<String, Integer> playerStates = new HashMap<>();
    private ServerSocket serverSocket;
    private CGServer server;
    private Gamedata gamedata = null;
    private int serverState = 0;
    public static final int STATE_OPEN = 1;
    public static final int STATE_COUNTDOWN = 2;
    public static final int STATE_GAME = 3;
    private int countdownCount;
    private static final int INITIAL_COUNT = 3;
    private Timer countdowner = null;

    public CGSetupServer(int port) throws IOException, IllegalArgumentException {

        // ポートチェック
        if (!((1024 <= port) && (port <= 65535))) {
            throw new IllegalArgumentException("ポート番号が間違っています。");
        }
        // ソケット
        serverSocket = new ServerSocket(port);

    }

    @Override
    public void run() {
        serverState = STATE_OPEN;
        while (true) {
            // クライアント受付
            try {
                Socket socket = serverSocket.accept();
                addPlayer(new CGConnector(this, socket));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setGame(Gamedata gamedata) {
        this.gamedata = gamedata;
        sendGamedata();
    }

    /**
     * ローカルプレイヤーを追加する。 ※ １人目のみ使用可
     * 
     * @param connector
     * @param name
     */
    void addLocalPlayer(CGConnector connector, String name) {
        // プレイヤー情報の追加
        playerNames.add(name);
        connectors.put(name, connector);
        playerStates.put(name, CGSetupPlayer.STATE_NOT_READY);

        // プレイヤーリストなどの送信
        sendAll(getPlayersInProtocol());
        sendGamedata();
    }

    /**
     * 新しいプレイヤーを追加する。
     * 
     * @param connector
     */
    private synchronized void addPlayer(CGConnector connector) {
        if (serverState == STATE_OPEN) {
            // 参加承認メッセージ送信
            connector.send("010");

            // 名前
            String name = connector.listen();
            // 名前重複確認
            boolean b = true;
            if (name == null) {
                b = false;
            } else if (CGSetupPlayer.checkName(name)) {
                for (String playerName : playerNames) {
                    if (name.equals(playerName)) {
                        b = false;
                        break;
                    }
                }
            } else {
                b = false;
            }
            // 名前登録
            if (b) {
                // 名前承認メッセージ送信
                connector.send("020");

                // プレイヤー情報の追加
                playerNames.add(name);
                connectors.put(name, connector);
                playerStates.put(name, CGSetupPlayer.STATE_NOT_READY);

                // プレイヤーリストなどの送信
                sendAll(getPlayersInProtocol());
                sendGamedata();

                // 受信を待機するスレッド
                Thread thread = new Thread(connector);
                thread.start();
            } else {
                // 名前拒否メッセージ送信
                connector.send("021");
                connector.close();
            }
        } else {
            // 参加拒否メッセージ送信
            connector.send("011");
            connector.close();
        }
    }

    /**
     * 参加者全員にメッセージを送る。
     * 
     * @param data
     */
    void sendAll(String data) {
        for (String name : playerNames) {
            connectors.get(name).send(data);
        }
    }

    /**
     * １人にメッセージを送る。
     * 
     * @param name 送りたいプレイヤーの名前
     * @param data
     */
    void sendOne(String name, String data) {
        connectors.get(name).send(data);
    }

    private void sendGamedata(){
        if(gamedata != null){
            sendAll("050 " + gamedata.getFolderName());
        }
    }

    /**
     * プロトコルに則ったプレイヤーの名前のリストを返す。
     * 
     * @return
     */
    private String getPlayersInProtocol() {
        String str = "031";
        for (String name : playerNames) {
            str += " " + name;
        }
        return str;
    }

    private String getPlayerStatesInProtocol() {
        String str = "032";
        for (String name : playerNames) {
            str += " " + name + " " + String.valueOf(playerStates.get(name));
        }
        return str;
    }

    /**
     * プレイヤーの名前のリストを返す。
     * 
     * @return
     */
    public List<String> getPlayers() {
        return playerNames;
    }

    /**
     * 全員が準備完了かを返す。
     * 
     * @return
     */
    private boolean isAllReady() {
        for (String playerName : playerNames) {
            if (playerStates.get(playerName) != CGSetupPlayer.STATE_READY) {
                return false;
            }
        }
        return true;
    }

    /**
     * カウントダウンを開始する。
     */
    private void startCountdown() {
        countdownCount = INITIAL_COUNT;
        serverState = STATE_COUNTDOWN;

        // タイマー起動
        countdowner = new Timer();
        countdowner.schedule(new TimerTask() {
            @Override
            public void run() {
                if (countdownCount < 0) {
                    countdowner.cancel();
                    startGame();
                } else {
                    sendAll("033 " + countdownCount);
                }
                countdownCount--;
            }
        }, 0, 1000);
    }

    /**
     * カウントダウンを中止する。
     */
    private void cancelCountdown() {
        serverState = STATE_OPEN;
        if (countdowner != null) {
            countdowner.cancel();
        }
        sendAll("033 -1");
    }

    /**
     * ゲームを開始する。
     */
    private void startGame() {
        server = gamedata.newServerInstance();
        server.setup(playerNames, this);
        serverState = STATE_GAME;
        sendAll("037");

        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                server.startGame();
            }
        }, 1000);
    }

    /**
     * メッセージを受信したときに呼ばれる。
     * 
     * @param connector
     */
    synchronized void listener(CGConnector connector, String data) {
        String name = null;
        // 名前の取得
        for (String playerName : playerNames) {
            if (connectors.get(playerName) == connector) {
                name = playerName;
            }
        }
        if (name == null) {
            return;
        }

        if ((serverState == STATE_OPEN) || (serverState == STATE_COUNTDOWN)) {
            if (data.equals("040")) {
                // 準備完了受信

                playerStates.put(name, CGSetupPlayer.STATE_READY);

                sendAll(getPlayerStatesInProtocol());

                if (isAllReady() && (serverState != STATE_COUNTDOWN)) {
                    startCountdown();
                }
            } else if (data.equals("041")) {
                // 準備完了取消受信

                playerStates.put(name, CGSetupPlayer.STATE_NOT_READY);

                cancelCountdown();

                sendAll(getPlayerStatesInProtocol());
            } else if (data.startsWith("050")) {
                String[] str = data.split(" ");
                if (str.length > 1) {
                    gamedata = Gamedata.getGamedata(str[1]);
                    sendGamedata();
                }
            }
        }

        if (serverState == STATE_GAME) {
            server.listener(name, data);
        }

    }
}
