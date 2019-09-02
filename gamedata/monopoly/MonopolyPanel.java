package gamedata.monopoly;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import gamedata.monopoly.MonopolyBoard.LandType;
import gamedata.monopoly.MonopolyPlayer.PlayerState;
import source.CGPlayer;
import source.display.PlayPanel;

public class MonopolyPanel extends PlayPanel {
    private static final long serialVersionUID = 1L;
    private Random random = new Random();
    private MonopolyPlayer player;
    private MonopolyBoard board;
    private int selectedLand = -1;
    private int mouseOverLand = -1;

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

        drawBoard(g, (int) (0.5 * (getWidth() - getHeight())), 0, getHeight(), getHeight());
        drawPlayerList(g, (int) (0.5 * (getWidth() + getHeight())), 0, (int) (0.5 * (getWidth() - getHeight())),
                getHeight());

        if (mouseOverLand != -1) {
            int dw = (int) (0.2 * getWidth());
            int dh = dw;
            int dx = Math.min(mousePos.x, getWidth() - dw);
            int dy = Math.min(mousePos.y, getHeight() - dh);

            drawLandOutline(g, dx, dy, dw, dh, mouseOverLand);

            // 選択
            if (mouseClicked && player.getName().equals(board.getOwner(mouseOverLand))) {
                selectedLand = mouseOverLand;
            }
        }

        if (selectedLand != -1) {
            if (!player.getName().equals(board.getOwner(mouseOverLand))) {
                selectedLand = -1;
            }
            drawLandDetail(g, 0, 0, (int) (0.5 * (getWidth() - getHeight())), (int) (0.5 * getHeight()), selectedLand);
        }
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
        mouseOverLand = -1;

        for (int i = 0; i < MonopolyBoard.LAND_MAX; i++) {
            int cx, cy, cw, ch, tx, ty, th;
            if (i == 0) {
                cx = bx + bs - cd;
                cy = by + bs - cd;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
                th = ch;
            } else if (i < 10) {
                cx = bx + cd + cf * (9 - i);
                cy = by + bs - cd;
                cw = cf;
                ch = cd;
                tx = cx;
                ty = cy + fs;
                th = ch - fs;
            } else if (i == 10) {
                cx = bx;
                cy = by + bs - cd;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy + cf;
                th = ch - cf;
            } else if (i < 20) {
                cx = bx;
                cy = by + cd + cf * (19 - i);
                cw = cd;
                ch = cf;
                tx = cx;
                ty = cy;
                th = ch;
            } else if (i == 20) {
                cx = bx;
                cy = by;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
                th = ch;
            } else if (i < 30) {
                cx = bx + cd + cf * (i - 21);
                cy = by;
                cw = cf;
                ch = cd;
                tx = cx;
                ty = cy;
                th = ch - fs;
            } else if (i == 30) {
                cx = bx + bs - cd;
                cy = by;
                cw = cd;
                ch = cd;
                tx = cx;
                ty = cy;
                th = ch;
            } else if (i < 40) {
                cx = bx + bs - cd;
                cy = by + cd + cf * (i - 31);
                cw = cd;
                ch = cf;
                tx = cx + fs;
                ty = cy;
                th = ch;
            } else {
                cx = bx + cd - cf;
                cy = by + bs - cd;
                cw = cf;
                ch = cf;
                tx = cx;
                ty = cy;
                th = ch;
            }
            g.setColor(Color.BLACK);
            g.drawRect(cx, cy, cw, ch);

            // 土地名
            drawString(g, tx, ty, board.getName(i), fs);

            // 所有者
            String name = board.getOwner(i);
            if (name != null) {
                drawString(g, tx, ty + fs, ": " + name, fs);
            }

            // 停まっているプレイヤー
            int j = 1;
            for (int k = board.getPlayers().size() - 1; k >= 0; k--) {
                if (board.getPlayerPosition(board.getPlayers().get(k)) == i) {
                    drawString(g, tx, ty + th - fs * j, board.getPlayers().get(k), fs);
                    j++;
                }
            }

            // カラーグループ
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

            // マウス
            if (cx <= mousePos.x && mousePos.x < cx + cw && cy <= mousePos.y && mousePos.y < cy + ch) {
                mouseOverLand = i;
            }
        }

        int dw = (int) (0.3 * bs);
        int dh = (int) (0.1 * bs);
        drawDice(g, (int) (bx + 0.5 * (bs - dw)), (int) (by + 0.5 * bs - dh), dw, dh);

        drawButtons(g, (int) (bx + 0.5 * (bs - dw)), (int) (by + 0.5 * bs), dw, 3 * dh);

    }

    private void drawLandOutline(Graphics g, int x, int y, int w, int h, int land) {
        if (board.getType(land) == LandType.PROPERTY || board.getType(land) == LandType.RAILROAD
                || board.getType(land) == LandType.COMPANY) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, w, h);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, w, h);
            drawString(g, x, y, board.getName(land), (int) (0.15 * h));
            drawString(g, x, y + (int) (0.15 * h), "購入費 $" + board.getPrice(land), (int) (0.1 * h));

        }
        if (board.getType(land) == LandType.PROPERTY) {
            drawString(g, x, y + (int) (0.25 * h), "建設費 $" + board.getBuildCost(land), (int) (0.1 * h));
            return;
        }
        if (board.getType(land) == LandType.RAILROAD) {

            return;
        }
        if (board.getType(land) == LandType.COMPANY) {

            return;
        }

    }

    private void drawLandDetail(Graphics g, int x, int y, int w, int h, int land) {
        g.setColor(Color.BLACK);
        drawString(g, x, y, board.getName(land), (int) (0.15 * h));

    }

    private void drawPlayerList(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < player.getPlayerNames().size(); i++) {
            // プレイヤーの名前
            String name = player.getPlayerNames().get(i);
            drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i), name, (int) (0.05 * h));

            // ターンのプレイヤー
            if (name.equals(player.getPlayerNameForTurn())) {
                drawString(g, x + (int) (0.02 * h), y + (int) (0.1 * h * i + 0.01 * h), "→", (int) (0.07 * h),
                        Font.BOLD);
            }

            if (board.isPlayerBankrupt(name)) {
                drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.05 * h), "破産", (int) (0.04 * h));
            } else {
                // 所持金
                int money = board.getPlayerMoney(name);
                drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.05 * h), "$" + money, (int) (0.04 * h));
            }
        }
    }

    private void drawDice(Graphics g, int x, int y, int w, int h) {

        int ds = h;
        int[] dx = { x, x + w - ds };
        int[] dy = { y, y };
        int[] dn = { 0, 0 };

        PlayerState state = player.getState();
        if (state == PlayerState.GAME || state == PlayerState.MY_POSITION_MOVED || state == PlayerState.MY_ACTION_SELECTED || state == PlayerState.BANKRUPTCY) {
            dn[0] = player.getDice()[0];
            dn[1] = player.getDice()[1];
        } else if (state == PlayerState.MY_DICE_ROLLING) {
            dn[0] = random.nextInt(6) + 1;
            dn[1] = random.nextInt(6) + 1;
        }
        if (dn[0] != 0 && dn[1] != 0) {
            for (int i = 0; i < 2; i++) {
                drawDice(g, dx[i], dy[i], ds, ds, dn[i]);
            }
        }
    }

    private void drawButtons(Graphics g, int x, int y, int w, int h) {
        int state = (player.getState() == PlayerState.MY_TURN_START) ? 0 : 1;
        if(drawButton(g, x, y, w, (int) (0.2 * h), "振る", state) && mouseClicked && state == 0) {
            player.rollDice();
        }

        drawButton(g, x, y + (int) (0.2 * h), w, (int) (0.2 * h), "破産する", 1);

        LandType landType = board.getType(board.getPlayerPosition(player.getName()));
        state = (player.getState() == PlayerState.MY_POSITION_MOVED && (landType == LandType.PROPERTY || landType == LandType.RAILROAD || landType == LandType.COMPANY) && board.getOwner(board.getPlayerPosition(player.getName())) == null) ? 0 : 1;
        if(drawButton(g, x, y + (int) (0.4 * h), w, (int) (0.2 * h), "買う", state) && mouseClicked && state == 0) {
            player.buyLand(true);
        }
        if(drawButton(g, x, y + (int) (0.6 * h), w, (int) (0.2 * h), "買わない", state) && mouseClicked && state == 0) {
            player.buyLand(false);
        }


    }

}
