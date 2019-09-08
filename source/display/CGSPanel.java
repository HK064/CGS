package source.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import source.file.CGSFont;

class CGSPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    protected static MainWindow mainWindow;
    protected Point mousePos = new Point(-1, -1);
    protected boolean mouseClicked = false;
    private boolean mouseClicked2 = false;
    protected Set<Character> keyPushed = new HashSet<>();
    private Set<Character> keyPushed2 = new HashSet<>();
    private int[] windowSize = { -1, -1 };
    protected boolean resize = false;

    CGSPanel() {
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClicked2 = true;
                requestFocus();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                char key = e.getKeyChar();
                keyPushed2.add(key);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        mouseClicked = mouseClicked2;
        mouseClicked2 = false;

        Set<Character> s = keyPushed;
        keyPushed = keyPushed2;
        keyPushed2 = s;
        keyPushed2.clear();
        System.out.println(keyPushed);

        mousePos = getMousePosition();
        if (mousePos == null) {
            mousePos = new Point(-1, -1);
        }

        resize = false;
        if (windowSize[0] != getWidth() || windowSize[1] != getHeight()) {
            windowSize[0] = getWidth();
            windowSize[1] = getHeight();
            resize = true;
        }
    }

    /**
     * パネルから移動するときの処理
     */
    void end() {
    }

    /**
     * MainWindow を指定する。
     * 
     * @param mainWindow
     */
    static void setMainWindow(MainWindow mw) {
        mainWindow = mw;
    }

    /**
     * 文字を描画する。
     * 
     * @param g
     * @param x
     * @param y
     * @param str
     * @param fontSize
     * @param style
     */
    protected void drawString(Graphics g, int x, int y, String str, int fontSize, int style) {
        g.setFont(CGSFont.getFont(fontSize));
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(str, x, y + fontSize);
    }

    protected void drawString(Graphics g, int x, int y, String str, int fontSize) {
        drawString(g, x, y, str, fontSize, Font.PLAIN);
    }

    /**
     * ボタンを描画する。
     * 
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param str
     * @param state 0:普通, 1:押せない状態
     * @return マウスがボタン上にあるか。
     */
    protected boolean drawButton(Graphics g, int x, int y, int w, int h, String str, int state) {
        int f = Math.max(1, (int) (0.05 * Math.min(w, h)));
        boolean select = false;
        if ((x <= mousePos.x) && (mousePos.x < x + w) && (y <= mousePos.y) && (mousePos.y < y + h)) {
            select = true;
        }
        g.setColor(Color.BLACK);
        g.fillRect(x, y, w, h);
        if (state == 0 && select) {
            g.setColor(Color.LIGHT_GRAY);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(x + f, y + f, w - 2 * f, h - 2 * f);
        if (state == 0) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        drawString(g, x + f, y + f, str, (int) (0.7 * h));
        return select;
    }

    protected boolean drawButton(Graphics g, int x, int y, int w, int h, String str) {
        return drawButton(g, x, y, w, h, str, 0);
    }

    /**
     * 画像を描画する。
     * 
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param image
     */
    protected void drawImage(Graphics g, int x, int y, int w, int h, BufferedImage image) {
        ((Graphics2D) g).drawImage(image, x, y, x + w, y + h, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    /**
     * 画像を描画する。
     * 
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param image
     * @param rad   回転角度
     */
    protected void drawImage(Graphics g, int x, int y, int w, int h, BufferedImage image, double rad) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = g2.getTransform();
        at.translate(x, y);
        at.scale((double) w / image.getWidth(), (double) h / image.getHeight());
        at.rotate(rad, 0.5 * image.getWidth(), 0.5 * image.getHeight());
        g2.setTransform(at);
        g2.drawImage(image, 0, 0, null);
        at.setToIdentity();
        g2.setTransform(at);
    }

}
