package gamedata.monopoly;

import java.awt.Color;
import java.awt.Graphics;

import gamedata.monopoly.MonopolyBoard.LandType;
import source.CGPlayer;
import source.display.PlayPanel;

public class MonopolyPanel extends PlayPanel {
    private static final long serialVersionUID = 1L;
    private MonopolyPlayer player;
    private MonopolyBoard board;

    @Override
    public void setup(CGPlayer player) {
        this.player = (MonopolyPlayer) player;
        board = this.player.getBoard();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawDice(g, 100, 100, 100, 100, 1);
        drawDice(g, 300, 100, 100, 100, 2);
        drawDice(g, 500, 100, 100, 100, 3);
        drawDice(g, 100, 300, 100, 100, 4);
        drawDice(g, 300, 300, 100, 100, 5);
        drawDice(g, 500, 300, 100, 100, 6);

        drawBoard(g, 0, 0, (int) (0.6 * getWidth()), getHeight());
    }

    private void drawBoard(Graphics g, int x, int y, int w, int h) {
        // b: Board, c: cell
        int cf = (int) ((double) Math.min(w, h) / 12); // 間口
        int cd = (int) (1.5 * cf); // 奥行き
        int bs = cf * 9 + cd * 2;
        int bx = x + (int) (0.5 * (w - bs));
        int by = y + (int) (0.5 * (h - bs));
        int ft = 1; // frame thickness
        int fs = (int) (0.2 * cf);
        for (int i = 0; i < MonopolyBoard.LAND_MAX; i++) {
            int cx, cy, cw, ch, tx, ty;
            if (i == 0) {
                cx = bx + bs - cd;
                cy = by + bs - cd;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
            } else if (i < 10) {
                cx = bx + cd + cf * (9 - i);
                cy = by + bs - cd;
                cw = cf;
                ch = cd;
                tx = cx;
                ty = cy + fs;
            } else if (i == 10) {
                cx = bx;
                cy = by + bs - cd;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy + cf;
            } else if (i < 20) {
                cx = bx;
                cy = by + cd + cf * (19 - i);
                cw = cd;
                ch = cf;
                tx = cx;
                ty = cy;
            } else if (i == 20) {
                cx = bx;
                cy = by;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
            } else if (i < 30) {
                cx = bx + cd + cf * (i - 21);
                cy = by;
                cw = cf;
                ch = cd;
                tx = cx;
                ty = cy;
            } else if (i == 30) {
                cx = bx + bs - cd;
                cy = by;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
            } else if (i < 40) {
                cx = bx + bs - cd;
                cy = by + cd + cf * (i - 31);
                cw = cd;
                ch = cf;
                tx = cx + fs;
                ty = cy;
            } else {
                cx = bx + cd - cf;
                cy = by + bs - cd;
                cw = cf;
                ch = cf;
                tx = cx;
                ty = cy;
            }
            g.setColor(Color.BLACK);
            g.drawRect(cx, cy, cw, ch);
            drawString(g, tx, ty, board.getName(i), fs);
            if (board.getType(i) == LandType.PROPERTY) {
                Color c = board.getColor(i).getColor();
                g.setColor(c);
                if (i < 10) {
                    g.fillRect(cx + ft, cy + ft, cw - ft, fs - ft);
                } else if (i < 20) {
                    g.fillRect(cx + cw - fs + ft, cy + ft, fs - ft, ch - ft);
                } else if (i < 30) {
                    g.fillRect(cx + ft, cy + ch - fs + ft, cw - ft, fs - ft);
                } else if (i < 40) {
                    g.fillRect(cx + ft, cy + ft, fs - ft, ch - ft);
                }
            }

        }
    }

    private void drawPlayerList(Graphics g, int x, int y, int w, int h) {
    }

}
