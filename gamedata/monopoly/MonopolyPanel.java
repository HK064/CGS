package gamedata.monopoly;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Objects;
import java.util.Random;

import javax.swing.JTextField;

import gamedata.monopoly.MonopolyBoard.LandType;
import gamedata.monopoly.MonopolyPlayer.PlayerState;
import source.CGPlayer;
import source.display.PlayPanel;
import source.file.CGSFont;

public class MonopolyPanel extends PlayPanel {
    private static final long serialVersionUID = 1L;
    private Random random = new Random();
    private MonopolyPlayer player;
    private MonopolyBoard board;
    private int mouseOverLand = -1;
    private int selectedLand = -1;
    private int landOutlineX = -1;
    private int landOutlineY = -1;
    private String mouseOverPlayer = null;
    private int playerOutlineX = -1;
    private int playerOutlineY = -1;
    private boolean mouseOverTrade = false;
    private String mouseOverPlayerTrade = null;
    private JTextField tradeMoneyField;

    @Override
    public void setup(CGPlayer player) {
        this.player = (MonopolyPlayer) player;
        board = this.player.getBoard();

        // 取引金額入力フィールド
        tradeMoneyField = new JTextField("");
        add(tradeMoneyField);

        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawBoard(g, (int) (0.5 * (getWidth() - getHeight())), 0, getHeight(), getHeight());
        drawPlayerList(g, (int) (0.5 * (getWidth() + getHeight())), 0, (int) (0.5 * (getWidth() - getHeight())),
                getHeight());
        drawTradeSetting(g, 0, (int) (0.5 * getHeight()), (int) (0.5 * (getWidth() - getHeight())),
                (int) (0.5 * getHeight()));

        if (mouseOverLand != -1) {
            int dw = (int) (0.1 * getWidth());
            int dh = (int) (0.4 * getHeight());
            int dx = Math.min(landOutlineX, getWidth() - dw);
            int dy = Math.min(landOutlineY, getHeight() - dh);

            drawLandOutline(g, dx, dy, dw, dh, mouseOverLand);

            // 選択
            if (mouseClicked && player.getName().equals(board.getOwner(mouseOverLand))) {
                selectedLand = mouseOverLand;
            }
        }

        if (selectedLand != -1) {
            if (!player.getName().equals(board.getOwner(selectedLand))) {
                selectedLand = -1;
            }
            drawLandSetting(g, 0, 0, (int) (0.5 * (getWidth() - getHeight())), (int) (0.5 * getHeight()), selectedLand);
        }

        if (mouseOverPlayer != null) {
            int dw = (int) (0.1 * getWidth());
            int dh = (int) (0.2 * getHeight());
            int dx = Math.min(playerOutlineX, getWidth() - dw);
            int dy = Math.min(playerOutlineY, getHeight() - dh);

            drawPlayerOutline(g, dx, dy, dw, dh);
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

            // 土地強調
            int selectLevel = 0;
            for (String name : board.getPlayers()) {
                if (board.getPlayerPosition(name) == i) {
                    selectLevel = (name.equals(player.getPlayerNameForTurn())) ? 2 : 1;
                }
            }
            if (mouseOverPlayer != null) {
                selectLevel = (Objects.equals(mouseOverPlayer, board.getOwner(i))) ? 1 : 0;
            }
            if (mouseOverPlayerTrade != null) {
                String tradeContent = player.getTradeContents().get(mouseOverPlayerTrade);
                if (tradeContent != null) {
                    selectLevel = (" " + tradeContent.split(" ", 2)[1] + " ").contains(" " + i + " ") ? 1 : 0;
                }
            }
            if (mouseOverTrade) {
                selectLevel = (player.getTradeLands().contains(i)) ? 1 : 0;
            }

            // 土地背景
            float f;
            switch (selectLevel) {
            case 0:
                g.setColor(Color.WHITE);
                break;
            case 1:
                f = (float) (0.8 + 0.1 * Math.sin(2.0 * Math.PI * currentTime / 2000));
                g.setColor(new Color(f, f, f));
                break;
            case 2:
                f = (float) (0.8 + 0.1 * Math.sin(2.0 * Math.PI * currentTime / 1000));
                g.setColor(new Color(f, f, f));
                break;
            }
            g.fillRect(cx, cy, cw, ch);

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

            // 土地枠
            g.setColor(Color.BLACK);
            g.drawRect(cx, cy, cw, ch);

            // 抵当
            if (board.isMortgage(i)) {
                g.setColor(Color.BLACK);
                g.drawLine(cx, cy, cx + cw, cy + ch);
                g.drawLine(cx + cw, cy, cx, cy + ch);
            }

            // 土地名
            drawString(g, tx, ty, board.getName(i), fs);

            // 所有者
            String name1 = board.getOwner(i);
            if (name1 != null) {
                drawString(g, tx, ty + fs, "所有 " + name1, fs);
            }

            // 建物
            String str = "";
            switch (board.getBuilding(i)) {
            case 1:
                str = "家１軒";
                break;
            case 2:
                str = "家２軒";
                break;
            case 3:
                str = "家３軒";
                break;
            case 4:
                str = "家４軒";
                break;
            case 5:
                str = "ホテル";
                break;
            }
            drawString(g, tx, ty + 2 * fs, str, fs);

            // 停まっているプレイヤー
            int j = 1;
            for (int k = board.getPlayers().size() - 1; k >= 0; k--) {
                String name2 = board.getPlayers().get(k);
                if (board.getPlayerPosition(name2) == i) {
                    if (name2.equals(player.getPlayerNameForTurn())) {
                        // ターンのプレイヤー
                        f = (float) (0.5 + 0.5 * Math.sin(2.0 * Math.PI * currentTime / 1000));
                        g.setColor(new Color(f, f, f));
                    } else {
                        // ターンでないプレイヤー
                        g.setColor(Color.BLACK);
                    }
                    drawString(g, tx, ty + th - fs * j, name2, fs);
                    j++;
                }
            }

            // マウス
            if (cx <= mousePos.x && mousePos.x < cx + cw && cy <= mousePos.y && mousePos.y < cy + ch) {
                mouseOverLand = i;
                landOutlineX = cx + cw + fs;
                landOutlineY = cy + fs;
            }
        }

        int tw = (int) (0.5 * bs);
        int th = (int) (0.1 * bs);
        int dw = (int) (0.3 * bs);
        int dh = (int) (0.1 * bs);
        int bw = (int) (0.5 * bs);
        int bh = (int) (0.3 * bs);

        drawMessageForPlayer(g, (int) (bx + 0.5 * (bs - tw)), (int) (by + 0.5 * bs - dh - th), tw, th);
        drawDice(g, (int) (bx + 0.5 * (bs - dw)), (int) (by + 0.5 * bs - dh), dw, dh);
        drawButtons(g, (int) (bx + 0.5 * (bs - bw)), (int) (by + 0.5 * bs), bw, bh);

    }

    private void drawLandOutline(Graphics g, int x, int y, int w, int h, int land) {
        LandType landType = board.getType(land);
        String name = board.getOwner(land);

        if (landType == LandType.PROPERTY || landType == LandType.RAILROAD || landType == LandType.COMPANY) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, w, h);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, w, h);

            g.setColor(Color.BLACK);
            drawString(g, x, y, board.getName(land), (int) (0.1 * h));

            String name2 = (name == null) ? "なし" : name;
            drawString(g, x, y + (int) (0.15 * h), "所有者 " + name2, (int) (0.05 * h));

            drawString(g, x, y + (int) (0.4 * h), "購入費 $" + board.getPrice(land), (int) (0.05 * h));

        }
        if (landType == LandType.PROPERTY) {
            drawString(g, x, y + (int) (0.2 * h), "レンタル料 $" + board.getRent(land), (int) (0.05 * h));
            if (board.isMonopoly(land)) {
                drawString(g, x, y + (int) (0.25 * h), "独占中", (int) (0.05 * h));
            }
            String str = "";
            switch (board.getBuilding(land)) {
            case 0:
                str = "なし";
                break;
            case 1:
                str = "家１軒";
                break;
            case 2:
                str = "家２軒";
                break;
            case 3:
                str = "家３軒";
                break;
            case 4:
                str = "家４軒";
                break;
            case 5:
                str = "ホテル";
                break;
            }
            drawString(g, x, y + (int) (0.3 * h), "建物 " + str, (int) (0.05 * h));

            drawString(g, x, y + (int) (0.45 * h), "建設費 $" + board.getBuildCost(land), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　更地　 $" + board.getRent(land, 0), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　家１軒 $" + board.getRent(land, 1), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.65 * h), "　家２軒 $" + board.getRent(land, 2), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.7 * h), "　家３軒 $" + board.getRent(land, 3), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.75 * h), "　家４軒 $" + board.getRent(land, 4), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.8 * h), "　ホテル $" + board.getRent(land, 5), (int) (0.05 * h));
            return;
        }
        if (landType == LandType.RAILROAD) {
            drawString(g, x, y + (int) (0.2 * h), "レンタル料 $" + board.getRent(land), (int) (0.05 * h));

            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　$" + board.getRent(land, 1), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　$" + board.getRent(land, 2), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.65 * h), "　$" + board.getRent(land, 3), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.7 * h), "　$" + board.getRent(land, 4), (int) (0.05 * h));
            return;
        }
        if (landType == LandType.COMPANY) {
            int i = (name == null) ? 0 : ((Objects.equals(board.getOwner(12), board.getOwner(28))) ? 2 : 1);
            String[] str = { "なし", "4×サイコロ", "10×サイコロ" };
            drawString(g, x, y + (int) (0.2 * h), "レンタル料 " + str[i], (int) (0.05 * h));

            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　" + str[1], (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　" + str[2], (int) (0.05 * h));
            return;
        }

    }

    private void drawLandSetting(Graphics g, int x, int y, int w, int h, int land) {
        LandType landType = board.getType(land);

        PlayerState state = player.getState();
        int s = (state == PlayerState.GAME || state == PlayerState.MY_TURN_START || state == PlayerState.MY_DICE_ROLLING
                || state == PlayerState.MY_POSITION_MOVED || state == PlayerState.MY_ACTION_SELECTED
                || state == PlayerState.MY_JAIL_START || state == PlayerState.MY_JAIL_BEFORE_DICE_ROLL
                || state == PlayerState.MY_JAIL_ACTION_SELECTED) ? 0 : 1;
        int t;

        g.setColor(Color.BLACK);
        drawString(g, x, y, board.getName(land), (int) (0.1 * h));

        drawString(g, x, y + (int) (0.4 * h), "購入費 $" + board.getPrice(land), (int) (0.05 * h));
        if (landType == LandType.PROPERTY) {
            g.setColor(board.getColor(land).getColor());
            g.fillRect(x + (int) (0.5 * w), y, (int) (0.5 * w), (int) (0.1 * h));

            g.setColor(Color.BLACK);

            drawString(g, x, y + (int) (0.15 * h), "レンタル料 $" + board.getRent(land), (int) (0.05 * h));
            if (board.isMonopoly(land)) {
                drawString(g, x, y + (int) (0.2 * h), "独占中", (int) (0.05 * h));
            }
            String str = "";
            switch (board.getBuilding(land)) {
            case 0:
                str = "なし";
                break;
            case 1:
                str = "家１軒";
                break;
            case 2:
                str = "家２軒";
                break;
            case 3:
                str = "家３軒";
                break;
            case 4:
                str = "家４軒";
                break;
            case 5:
                str = "ホテル";
                break;
            }
            drawString(g, x, y + (int) (0.25 * h), "建物 " + str, (int) (0.05 * h));

            drawString(g, x, y + (int) (0.45 * h), "建設費 $" + board.getBuildCost(land), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　更地　 $" + board.getRent(land, 0), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　家１軒 $" + board.getRent(land, 1), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.65 * h), "　家２軒 $" + board.getRent(land, 2), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.7 * h), "　家３軒 $" + board.getRent(land, 3), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.75 * h), "　家４軒 $" + board.getRent(land, 4), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.8 * h), "　ホテル $" + board.getRent(land, 5), (int) (0.05 * h));

            t = player.canBuild(selectedLand) ? s : 1;
            if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.4 * h), (int) (0.5 * w), (int) (0.1 * h), "建設する(B)",
                    t) && mouseClicked) || keyPushed.contains('b')) && t == 0) {
                player.build(selectedLand);
            }

            t = player.canUnbuild(selectedLand) ? s : 1;
            if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.5 * h), (int) (0.5 * w), (int) (0.1 * h), "解体する(D)",
                    t) && mouseClicked) || keyPushed.contains('d')) && t == 0) {
                player.unbuild(selectedLand);
            }
        }

        if (landType == LandType.RAILROAD) {
            drawString(g, x, y + (int) (0.2 * h), "レンタル料 $" + board.getRent(land), (int) (0.05 * h));

            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　$" + board.getRent(land, 1), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　$" + board.getRent(land, 2), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.65 * h), "　$" + board.getRent(land, 3), (int) (0.05 * h));
            drawString(g, x, y + (int) (0.7 * h), "　$" + board.getRent(land, 4), (int) (0.05 * h));
        }

        if (landType == LandType.COMPANY) {
            int i = (board.getOwner(land) == null) ? 0
                    : ((Objects.equals(board.getOwner(12), board.getOwner(28))) ? 2 : 1);
            String[] str = { "なし", "4×サイコロ", "10×サイコロ" };
            drawString(g, x, y + (int) (0.2 * h), "レンタル料 " + str[i], (int) (0.05 * h));

            drawString(g, x, y + (int) (0.5 * h), "レンタル料", (int) (0.05 * h));
            drawString(g, x, y + (int) (0.55 * h), "　" + str[1], (int) (0.05 * h));
            drawString(g, x, y + (int) (0.6 * h), "　" + str[2], (int) (0.05 * h));
        }

        t = player.canMortgage(selectedLand) ? s : 1;
        if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.6 * h), (int) (0.5 * w), (int) (0.1 * h), "抵当(M)", t)
                && mouseClicked) || keyPushed.contains('m')) && t == 0) {
            player.mortgage(selectedLand);
        }

        t = player.canUnmortgage(selectedLand) ? s : 1;
        if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.7 * h), (int) (0.5 * w), (int) (0.1 * h), "抵当解除(U)", t)
                && mouseClicked) || keyPushed.contains('u')) && t == 0) {
            player.unmortgage(selectedLand);
        }

        t = (!player.isOfferTrade() && !player.isLandTrade(selectedLand)) ? s : 1;
        if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.8 * h), (int) (0.5 * w), (int) (0.1 * h), "取引に追加(T)", t)
                && mouseClicked) || keyPushed.contains('t')) && t == 0) {
            player.addTradeLand(selectedLand);
        }

        t = (!player.isOfferTrade() && player.isLandTrade(selectedLand)) ? s : 1;
        if (((drawButton(g, x + (int) (0.5 * w), y + (int) (0.9 * h), (int) (0.5 * w), (int) (0.1 * h), "取引ｶﾗ除外(E)", t))
                && mouseClicked) || keyPushed.contains('e') && t == 0) {
            player.removeTradeLand(selectedLand);
        }

    }

    private void drawPlayerList(Graphics g, int x, int y, int w, int h) {
        mouseOverPlayer = null;
        mouseOverPlayerTrade = null;
        for (int i = 0; i < player.getPlayerNames().size(); i++) {
            String name = player.getPlayerNames().get(i);

            int bx = x + (int) (0.5 * w);
            int by = y + (int) (0.1 * h * i + 0.04 * h);
            int bw = (int) (0.5 * w);
            int bh = (int) (0.03 * h);

            // マウス
            if (x <= mousePos.x && mousePos.x < x + w && y + (int) (0.1 * h * i) <= mousePos.y
                    && mousePos.y < y + (int) (0.1 * h * (i + 1))
                    && !(bx <= mousePos.x && mousePos.x < bx + bw && by <= mousePos.y && mousePos.y < by + bh)) {
                mouseOverPlayer = name;
                playerOutlineX = x + (int) (0.1 * h);
                playerOutlineY = y + (int) (0.1 * h * (i + 1));

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y + (int) (0.1 * h * i), w, (int) (0.1 * h));
            }

            // ターンのプレイヤー
            if (name.equals(player.getPlayerNameForTurn())) {
                float f = (float) (0.5 + 0.5 * Math.sin(2.0 * Math.PI * currentTime / 1000));
                g.setColor(new Color(f, f, f));
                drawString(g, x + (int) (0.02 * h), y + (int) (0.1 * h * i + 0.01 * h), "→", (int) (0.07 * h),
                        Font.BOLD);
            }

            g.setColor(Color.BLACK);

            // 境界線
            g.drawLine(x, y + (int) (0.1 * h * (i + 1)) - 1, x + w, y + (int) (0.1 * h * (i + 1)) - 1);

            // プレイヤーの名前
            drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i), name, (int) (0.05 * h));

            if (board.isPlayerBankrupt(name)) {
                drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.05 * h), "破産", (int) (0.04 * h));
            } else {
                // 所持金
                int money = board.getPlayerMoney(name);
                drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.05 * h), "$" + money, (int) (0.04 * h));
            }

            String tradeContent = player.getTradeContents().get(name);
            String str = "";
            int t = (tradeContent != null) ? 0 : 1;
            if (tradeContent != null) {
                String[] str2 = tradeContent.split(" ");
                str = " $" + str2[0] + " 土地" + (str2.length - 1) + "個";
            }
            if (drawButton(g, bx, by, bw, bh, "取引提案中" + str, t) && t == 0) {
                mouseOverPlayerTrade = name;
            }

            if (player.getTradeAgreements().containsKey(name)) {
                drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.07 * h),
                        player.getTradeAgreements().get(name) + "との取引を希望", (int) (0.02 * h));
            }

        }
    }

    private void drawPlayerOutline(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);

        drawString(g, x, y, mouseOverPlayer, (int) (0.2 * h));
        drawString(g, x, y + (int) (0.2 * h), "所持金 $" + board.getPlayerMoney(mouseOverPlayer), (int) (0.1 * h));

        int totalAsset = board.getPlayerMoney(mouseOverPlayer);
        for (int land = 0; land < MonopolyBoard.LAND_MAX; land++) {
            if (mouseOverPlayer.equals(board.getOwner(land))) {
                int c = (board.isMortgage(land)) ? (int) Math.ceil(0.5 * board.getPrice(land)) : board.getPrice(land);
                totalAsset += c + board.getBuildCost(land) * board.getBuilding(land);
            }
        }
        drawString(g, x, y + (int) (0.3 * h), "総資産 $" + totalAsset, (int) (0.1 * h));

    }

    private void drawTradeSetting(Graphics g, int x, int y, int w, int h) {
        mouseOverTrade = false;

        g.setColor(Color.BLACK);
        drawString(g, x, y, "取引", (int) (0.1 * h));

        String str = (player.isOfferTrade()) ? "取引提案中" : "取引未提案";
        drawString(g, x, y + (int) (0.15 * h), str, (int) (0.05 * h));
        String name2 = player.getTradeAgreements().get(player.getName());
        if (name2 != null) {
            drawString(g, x, y + (int) (0.2 * h), name2 + "との取引に同意", (int) (0.05 * h));
        }

        str = "";
        for (String n : player.getPlayerNames()) {
            if (player.getName() != null && Objects.equals(player.getName(), player.getTradeAgreements().get(n))) {
                if (str.length() > 0) {
                    str += ", ";
                }
                str += n;
            }
        }
        if (str.length() > 0) {
            drawString(g, x, y + (int) (0.25 * h), str + "が取引に同意", (int) (0.05 * h));
        }

        drawString(g, x, y + (int) (0.4 * h), "取引内容", (int) (0.05 * h));

        drawString(g, x, y + (int) (0.45 * h), "$" + player.getTradeMoney(), (int) (0.05 * h));

        int i = 0;
        for (int land2 : player.getTradeLands()) {
            drawString(g, x, y + (int) (0.5 * h + 0.05 * h * i), board.getName(land2), (int) (0.05 * h));
            i++;
        }

        PlayerState state = player.getState();
        int s = (state == PlayerState.GAME || state == PlayerState.MY_TURN_START || state == PlayerState.MY_DICE_ROLLING
                || state == PlayerState.MY_POSITION_MOVED || state == PlayerState.MY_ACTION_SELECTED
                || state == PlayerState.MY_JAIL_START || state == PlayerState.MY_JAIL_BEFORE_DICE_ROLL
                || state == PlayerState.MY_JAIL_ACTION_SELECTED) ? 0 : 1;
        int t;

        // 取引相手
        drawString(g, x + (int) (0.3 * w), y + (int) (0.4 * h), "取引相手", (int) (0.05 * h));
        t = (player.isOfferTrade() && !player.isAgreeTrade()) ? s : 1;
        i = 0;
        for (String name : player.getPlayerNames()) {
            if (!name.equals(player.getName()) && player.getTradeContents().containsKey(name)) {
                boolean b = drawButton(g, x + (int) (0.3 * w), y + (int) (0.47 * h + 0.07 * h * i), (int) (0.3 * w), (int) (0.07 * h), name, t);
                if (b) {
                    mouseOverPlayerTrade = name;
                }
                if (b && mouseClicked && t == 0) {
                    player.agreeTrade(name);
                }
                i++;
            }
        }

        // 取引表示ボタン
        if (drawButton(g, x + (int) (0.6 * w), y + (int) (0.3 * h), (int) (0.4 * w), (int) (0.1 * h), "取引表示", s)
                && s == 0) {
            mouseOverTrade = true;
        } else {
            mouseOverTrade = false;
        }

        // 金額設定フィールド
        g.setColor(Color.BLACK);
        drawString(g, x + (int) (0.6 * w), y + (int) (0.4 * h), "金額", (int) (0.07 * h));
        if (tradeMoneyField.isEditable() ^ !player.isOfferTrade() && s == 0) {
            if (!tradeMoneyField.isEditable()) {
                tradeMoneyField.setText(String.valueOf(player.getTradeMoney()));
            }
            tradeMoneyField.setEditable(!player.isOfferTrade());
        }
        if (resize) {
            tradeMoneyField.setFont(CGSFont.getFont((int) (0.07 * h)));
            tradeMoneyField.setBounds(x + (int) (0.8 * w), y + (int) (0.4 * h), (int) (0.2 * w), (int) (0.1 * h));
        }
        try {
            player.setTradeMoney(Integer.parseInt(tradeMoneyField.getText()));
        } catch (NumberFormatException e) {
        }

        // 取引提案ボタン
        t = (!player.isOfferTrade() && (player.getTradeMoney() > 0 || player.getTradeLands().size() > 0)) ? s : 1;
        if (drawButton(g, x + (int) (0.6 * w), y + (int) (0.5 * h), (int) (0.4 * w), (int) (0.1 * h), "取引提案", t)
                && mouseClicked && t == 0) {
            player.setTrade();
            tradeMoneyField.setEditable(false);
        }

        // 取引取消ボタン
        t = (player.isOfferTrade()) ? s : 1;
        if (drawButton(g, x + (int) (0.6 * w), y + (int) (0.6 * h), (int) (0.4 * w), (int) (0.1 * h), "取引取消", t)
                && mouseClicked && t == 0) {
            player.resetTrade();
            tradeMoneyField.setEditable(true);
        }

    }

    private void drawMessageForPlayer(Graphics g, int x, int y, int w, int h) {
        String str = "";
        LandType landType = board.getType(board.getPlayerPosition(player.getName()));
        String landOwner = board.getOwner(board.getPlayerPosition(player.getName()));
        switch (player.getState()) {
        case MY_TURN_START:
            str = "あなたのターンです。";
            break;
        case MY_POSITION_MOVED:
            if (landOwner == null && (landType == LandType.PROPERTY || landType == LandType.RAILROAD
                    || landType == LandType.COMPANY)) {
                str = "土地を買いますか？";
            }
            break;
        case MY_JAIL_START:
            str = "刑務所から出ますか？";
            break;
        case MY_JAIL_BEFORE_DICE_ROLL:
            str = "サイコロを振りましょう。";
            break;
        default:
            break;
        }
        g.setColor(Color.BLACK);
        drawString(g, x, y, str, (int) (0.5 * h));
    }

    private void drawDice(Graphics g, int x, int y, int w, int h) {

        int ds = h;
        int[] dx = { x, x + w - ds };
        int[] dy = { y, y };
        int[] dn = { 0, 0 };

        PlayerState state = player.getState();
        if (state == PlayerState.GAME || state == PlayerState.MY_POSITION_MOVED
                || state == PlayerState.MY_ACTION_SELECTED || state == PlayerState.BANKRUPTCY) {
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
        PlayerState state = player.getState();

        int s = (state == PlayerState.MY_TURN_START || state == PlayerState.MY_JAIL_BEFORE_DICE_ROLL) ? 0 : 1;
        if (drawButton(g, x, y, (int) (0.5 * w), (int) (0.2 * h), "振る", s) && mouseClicked && s == 0) {
            player.rollDice();
        }

        drawButton(g, x, y + (int) (0.2 * h), (int) (0.5 * w), (int) (0.2 * h), "破産する", 1);

        LandType landType = board.getType(board.getPlayerPosition(player.getName()));
        s = (player.getState() == PlayerState.MY_JAIL_START) ? 0 : 1;
        if (drawButton(g, x, y + (int) (0.4 * h), (int) (0.5 * w), (int) (0.2 * h), "出所する", s) && mouseClicked
                && s == 0) {
            player.leavePrison(true);
        }
        if (drawButton(g, x, y + (int) (0.6 * h), (int) (0.5 * w), (int) (0.2 * h), "出所しない", s) && mouseClicked
                && s == 0) {
            player.leavePrison(false);
        }

        s = (state == PlayerState.MY_POSITION_MOVED
                && (landType == LandType.PROPERTY || landType == LandType.RAILROAD || landType == LandType.COMPANY)
                && board.getOwner(board.getPlayerPosition(player.getName())) == null) ? 0 : 1;
        int t = (s == 0 && board.getPlayerMoney(player.getName())
                - board.getPrice(board.getPlayerPosition(player.getName())) >= 0) ? 0 : 1;
        if (drawButton(g, x + (int) (0.5 * w), y, (int) (0.5 * w), (int) (0.2 * h), "買う", t) && mouseClicked
                && t == 0) {
            player.buyLand(true);
        }
        if (drawButton(g, x + (int) (0.5 * w), y + (int) (0.2 * h), (int) (0.5 * w), (int) (0.2 * h), "買わない", s)
                && mouseClicked && s == 0) {
            player.buyLand(false);
        }

    }

}
