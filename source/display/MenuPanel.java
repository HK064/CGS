package source.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JTextField;

import source.CGSetupPlayer;
import source.CGSetupServer;
import source.file.CGSFont;
import source.file.CGSProperty;

/**
 * 初めの画面
 */
class MenuPanel extends CGSPanel{
    private static final long serialVersionUID = 1L;
    private JTextField nameField;
    private JTextField portField;
    private JButton buildButton;
    private JTextField addressField;
    //private JButton connectButton;

    MenuPanel() {

        // 名前入力フィールド
        nameField = new JTextField(CGSProperty.getValue(CGSProperty.USERNAME_KEY));
        nameField.setCaretPosition(nameField.getText().length());
        add(nameField);

        // サーバポート入力フィールド
        portField = new JTextField(CGSProperty.getValue(CGSProperty.SERVER_PORT_KEY));
        add(portField);

        // サーバ建てるボタン
        buildButton = new JButton("建てる");
        buildButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event) {
                try{
                    // サーバ作成
                    int port = Integer.parseInt(portField.getText());
                    CGSetupServer server = new CGSetupServer(port);

                    // 自分のクライアント作成
                    CGSetupPlayer player = new CGSetupPlayer(nameField.getText(), server);

                    // 画面作成
                    SetupPanel panel = new SetupPanel(server, player);
                    player.setSetupPanel(panel);

                    // サーバ起動
                    Thread thread = new Thread(server);
                    thread.start();

                    // 画面遷移
                    mainWindow.changePanel(panel);
                }catch(IOException exception){
                    // サーバ起動失敗
                    exception.printStackTrace();
                }catch(IllegalArgumentException exception){
                    // 
                    exception.printStackTrace();
                }
            }
        });
        add(buildButton);

        // 接続先IPアドレス入力フィールド
        addressField = new JTextField(CGSProperty.getValue(CGSProperty.SERVER_ADDRESS_KEY));
        add(addressField);

        requestFocus();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawTitlePanel(g, 0, 0, (int)(0.5 * getWidth()), (int)(0.5 * getHeight()));
        drawSettingPanel(g, (int)(0.5 * getWidth()), 0, (int)(0.5 * getWidth()), (int)(0.5 * getHeight()));
        drawServerPanel(g, 0, (int)(0.5 * getHeight()), (int)(0.5 * getWidth()), (int)(0.5 * getHeight()));
        drawClientPanel(g, (int)(0.5 * getWidth()), (int)(0.5 * getHeight()), (int)(0.5 * getWidth()), (int)(0.5 * getHeight()));

    }

    private void drawTitlePanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        drawString(g, x, y, "CGS", (int)(0.4 * h));

    }

    private void drawSettingPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        drawString(g, x, y, "設定", (int)(0.08 * h));

        drawString(g, x, y + (int)(0.2 * h), "名前", (int)(0.07 * h));

        if(resize){
            nameField.setFont(CGSFont.getFont((int)(0.07 * h)));
            nameField.setBounds(x, y + (int)(0.3 * h), w, (int)(0.1 * h));
        }

    }

    private void drawServerPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        drawString(g, x, y, "部屋を建てる", (int)(0.08 * h));

        drawString(g, x, y + (int)(0.2 * h), "ポート", (int)(0.07 * h));

        if(resize){
            portField.setFont(CGSFont.getFont((int)(0.07 * h)));
            portField.setBounds(x, y + (int)(0.3 * h), w, (int)(0.1 * h));

            buildButton.setFont(CGSFont.getFont((int)(0.07 * h)));
            buildButton.setBounds(x, y + (int)(0.5 * h), w, (int)(0.1 * h));
        }

    }

    private void drawClientPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        drawString(g, x, y, "部屋に参加する", (int)(0.08 * h));

        drawString(g, x, y + (int)(0.2 * h), "IPアドレス", (int)(0.07 * h));

        if(resize){
            addressField.setFont(CGSFont.getFont((int)(0.07 * h)));
            addressField.setBounds(x, y + (int)(0.3 * h), w, (int)(0.1 * h));
        }

        if(drawButton(g, x, y + (int)(0.5 * h), w, (int)(0.1 * h), "参加する") && mouseClicked){

            // プレイヤー作成
            CGSetupPlayer player = new CGSetupPlayer(nameField.getText(), addressField.getText());

            // 画面作成
            SetupPanel panel = new SetupPanel(player);
            player.setSetupPanel(panel);

            // 画面遷移
            mainWindow.changePanel(panel);
        }

    }

    @Override
    void end(){
        super.end();

        CGSProperty.setProperty(CGSProperty.USERNAME_KEY, nameField.getText());
        CGSProperty.setProperty(CGSProperty.SERVER_PORT_KEY, portField.getText());
        CGSProperty.setProperty(CGSProperty.SERVER_ADDRESS_KEY, addressField.getText());
        
    }
}
