package source.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import source.file.CGSFont;

class CGSPanel extends JPanel{
    private static final long serialVersionUID = 1L;
    protected static MainWindow mainWindow;
    protected Point mousePos = new Point(-1, -1);
    protected boolean mouseClicked = false;
    private boolean mouseClicked2 = false;

    CGSPanel(){
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                mouseClicked = true;
                mouseClicked2 = true;
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        mouseClicked = mouseClicked2;
        mouseClicked2 = false;

        mousePos = getMousePosition();
        if(mousePos == null){
            mousePos = new Point(-1, -1);
        }
    }

    /**
     * パネルから移動するときの処理
     */
    void end(){
    }

    /**
     * MainWindow を指定する。
     * @param mainWindow
     */
    static void setMainWindow(MainWindow mw){
        mainWindow = mw;
    }

    /**
     * 文字を描画する。
     * @param g
     * @param x
     * @param y
     * @param str
     * @param fontSize
     * @param style
     */
    protected void drawString(Graphics g, int x, int y, String str, int fontSize, int style){
        g.setFont(CGSFont.getFont(fontSize));
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(str, x, y + fontSize);
    }

    protected void drawString(Graphics g, int x, int y, String str, int fontSize){
        drawString(g, x, y, str, fontSize, Font.PLAIN);
    }

    /**
     * ボタンを描画する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param str
     * @param state 0:普通, 1:押せない状態
     * @return マウスがボタン上にあるか。
     */
    protected boolean drawButton(Graphics g, int x, int y, int w, int h, String str, int state){
        int f = Math.max(1, (int)(0.05 * Math.min(w, h)));
        boolean select = false;
        if((x <= mousePos.x) && (mousePos.x < x + w) && (y <= mousePos.y) && (mousePos.y < y + h)){
            select = true;
        }
        g.setColor(Color.BLACK);
        g.fillRect(x, y, w, h);
        if(state == 0 && select){
            g.setColor(Color.LIGHT_GRAY);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(x + f, y + f, w - 2 * f, h - 2 * f);
        if(state == 0){
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        drawString(g, x + f, y + f, str, (int)(0.7 * h));
        return select;
    }

    protected boolean drawButton(Graphics g, int x, int y, int w, int h, String str){
        return drawButton(g, x, y, w, h, str, 0);
    }

    /**
     * 画像を描画する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param image
     */
    protected void drawImage(Graphics g, int x, int y, int w, int h, BufferedImage image){
        ((Graphics2D)g).drawImage(image, x, y, x + w, y + h, 0, 0, image.getWidth(), image.getHeight(), null);
    }

}
