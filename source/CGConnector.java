package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class CGConnector implements Runnable {
    private static final int TYPE_LOCAL = 0;
    private static final int TYPE_SERVER = 1;
    private static final int TYPE_CLIENT = 2;
    private int type;
    private CGSetupServer server;
    private CGSetupPlayer player;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private boolean valid = true;

    // ローカル用
    CGConnector(CGSetupServer server, CGSetupPlayer player) {
        type = TYPE_LOCAL;

        this.server = server;
        this.player = player;

    }

    // サーバ側通信中継用
    CGConnector(CGSetupServer server, Socket socket) {
        type = TYPE_SERVER;

        this.server = server;
        this.socket = socket;
        setReaderAndWriter();

    }

    // クライアント側通信中継用
    CGConnector(CGSetupPlayer player, String address) {
        type = TYPE_CLIENT;

        this.player = player;

        String[] str = address.split(":", 2);
        try {
            socket = new Socket(str[0], Integer.parseInt(str[1]));
            setReaderAndWriter();
        } catch (UnknownHostException e) {
            // TODO 接続先が見つからなかった場合
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setReaderAndWriter() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * メッセージを送信する。
     * 
     * ※ ローカルではスタックトレースから呼び出し元を判別しているので、CGSetupServer と CGSetupPlayer
     * でないと困る。継承やクラス名の変更の禁止。
     * 
     * @param str メッセージ
     */
    synchronized void send(String str) {
        long milliSec = System.currentTimeMillis();
        if (type == TYPE_LOCAL) {
            CGConnector me = this;
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            new Thread(new Runnable(){
                @Override
                public void run() {
                    // スタックトレースの取得
                    if (stackTraceElements[2].getClassName().contains("CGSetupServer")) {
                        System.out.println("" + milliSec + " SERVER > (STA) " + str);
                        player.listener(str);
                        System.out.println("" + milliSec + " SERVER > (END) " + str);
                    } else if (stackTraceElements[2].getClassName().contains("CGSetupPlayer")) {
                        System.out.println("" + milliSec + " CLIENT > (STA) " + str);
                        server.listener(me, str);
                        System.out.println("" + milliSec + " CLIENT > (END) " + str);
                    }
                }
            }).start();
        } else {
            if (type == TYPE_SERVER) {
                System.out.println("" + milliSec + " SERVER > (STA) " + str);
            } else {
                System.out.println("" + milliSec + " CLIENT > (STA) " + str);
            }
            writer.println(str);
            if (type == TYPE_SERVER) {
                System.out.println("" + milliSec + " SERVER > (END) " + str);
            } else {
                System.out.println("" + milliSec + " CLIENT > (END) " + str);
            }
        }
    }

    /**
     * メッセージの受信を待つ。 ※ LOCAL での処理は未実装（現時点では実装の必要は無いとの判断）。
     * 
     * @return メッセージ
     */
    String listen() {
        String str = "";
        if (type == TYPE_LOCAL) {

        } else {
            try {
                str = reader.readLine();
                if (str == null) {
                    valid = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * 切断する。
     */
    void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (writer != null) {
            writer.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (valid) {
            // メッセージ受信
            String str = listen();
            if (type == TYPE_SERVER) {
                server.listener(this, str);
            } else if (type == TYPE_CLIENT) {
                player.listener(str);
            }

        }
    }
}
