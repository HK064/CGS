package source.display;

import java.awt.Color;
import java.awt.Graphics;

import gamedata.daifugo.DaifugoPanel;
import source.CGPlayer;
import source.CGSetupPlayer;
import source.CGSetupServer;

/**
 * 部屋の設定画面
 */
public class SetupPanel extends CGSPanel{
    private static final long serialVersionUID = 1L;
    private static final int TYPE_SERVER = 0;
    private static final int TYPE_CLIENT = 1;
    private int type;
    private CGSetupPlayer player;

    SetupPanel(CGSetupServer server, CGSetupPlayer player){
        type = TYPE_SERVER;
        this.player = player;
    }

    SetupPanel(CGSetupPlayer player){
        type = TYPE_CLIENT;
        this.player = player;
    }

    public void startGame(CGPlayer player){
        DaifugoPanel panel = new DaifugoPanel();
        panel.setup(player);
        mainWindow.changePanel(panel);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawServerSetting(g, 0, 0, (int)(0.6 * getWidth()), (int)(0.9 * getHeight()));
        drawReadyButtonPanel(g, (int)(0.4 * getWidth()), (int)(0.9 * getHeight()), (int)(0.2 * getWidth()), (int)(0.1 * getHeight()));
        drawPlayerListPanel(g, (int)(0.6 * getWidth()), 0, (int)(0.4 * getWidth()), getHeight());
        if(player.getServerState() == CGSetupServer.STATE_COUNTDOWN){
            drawCountdownPanel(g, (int)(0.3 * getWidth()), (int)(0.4 * getHeight()), (int)(0.4 * getWidth()), (int)(0.2 * getHeight()));
        }
    }

    private void drawServerSetting(Graphics g, int x, int y, int w, int h){

    }

    private void drawReadyButtonPanel(Graphics g, int x, int y, int w, int h){
        String str = "";
        int state = player.getPlayerStates().get(player.getName());
        if(state == CGSetupPlayer.STATE_NOT_READY){
            str = "準備完了";
        } else if(state == CGSetupPlayer.STATE_READY){
            str = "取消";
        }
        if(drawButton(g, x, y, w, h, str) && mouseClicked){
            if(state == CGSetupPlayer.STATE_NOT_READY){
                player.sendReady();
            } else if(state == CGSetupPlayer.STATE_READY){
                player.sendNotReady();
            }
        }

    }

    private void drawPlayerListPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        for(int i = 0; i < player.getPlayerNames().size(); i++){
            // プレイヤーの名前
            String name = player.getPlayerNames().get(i);
            drawString(g, x, y + (int)(0.1 * h * i), name, (int)(0.05 * h));

            // プレイヤーの状態
            String str = "";
            int state = player.getPlayerStates().get(name);
            if(state == CGSetupPlayer.STATE_NOT_READY){
                str = "準備中";
            } else if(state == CGSetupPlayer.STATE_READY){
                str = "準備完了";
            }
            drawString(g, x + (int)(0.1 * w), y + (int)(0.1 * h * i + 0.05 * h), str, (int)(0.04 * h));
        }
    }

    private void drawCountdownPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        g.setColor(Color.BLACK);
        drawString(g, x, y, "開始まで：" + player.getCountdownCount(), (int)(0.5 * h));
    }
}
