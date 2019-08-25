package source.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import source.file.CGSProperty;

/**
 * メインウィンドウを作り、ループ描画する。
 */
public class MainWindow extends JFrame implements Runnable{
    private static final long serialVersionUID = 1L;
    private CGSPanel panel = null;

    public MainWindow(){
        // Window 設定
        setTitle("CGS");
        setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        // Window 位置設定
        String xStr = CGSProperty.getValue(CGSProperty.MAIN_WINDOW_X_POSITION_KEY);
        String yStr = CGSProperty.getValue(CGSProperty.MAIN_WINDOW_Y_POSITION_KEY);
        if((xStr != null) && (yStr != null)){
            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);
            setLocation(x, y);
        }

        // Window サイズ設定
        String wStr = CGSProperty.getValue(CGSProperty.MAIN_WINDOW_WIDTH);
        String hStr = CGSProperty.getValue(CGSProperty.MAIN_WINDOW_HEIGHT);
        if((wStr != null) && (hStr != null)){
            int w = Integer.parseInt(wStr);
            int h = Integer.parseInt(hStr);
            setSize(new Dimension(w, h));
        }else{
            getContentPane().setPreferredSize(new Dimension(1600, 900));
            pack();
        }

        // 終了設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                panel.end();

                Point p = getLocation();
                CGSProperty.setProperty(CGSProperty.MAIN_WINDOW_X_POSITION_KEY, String.valueOf(p.x));
                CGSProperty.setProperty(CGSProperty.MAIN_WINDOW_Y_POSITION_KEY, String.valueOf(p.y));
                Dimension d = getSize();
                CGSProperty.setProperty(CGSProperty.MAIN_WINDOW_WIDTH, String.valueOf((int)(d.getWidth())));
                CGSProperty.setProperty(CGSProperty.MAIN_WINDOW_HEIGHT, String.valueOf((int)(d.getHeight())));

                CGSProperty.storeProperty();
            }
        });

        // Panel 生成
        CGSPanel.setMainWindow(this);
        changePanel(new MenuPanel());
        
        setVisible(true);
    }

    /**
     * メインウィンドウに表示されるパネルを変更する。
     * @param panel
     */
    void changePanel(CGSPanel panel){
        if(this.panel != null){
            this.panel.end();
        }
        getContentPane().removeAll();
        this.panel = panel;
        getContentPane().add(panel);
        repaint();
        validate();
    }

    @Override
    public void run(){
        while(true){


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            repaint();
        }
    }
}
