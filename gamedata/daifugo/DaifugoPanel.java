package gamedata.daifugo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import source.CGPlayer;
import source.Card;
import source.display.PlayPanel;
import source.file.CardImage;

/**
 * 大富豪の GUI クラス
 */
public class DaifugoPanel extends PlayPanel{
    private static final long serialVersionUID = 1L;
    private DaifugoPlayer player;

    @Override
    public void setup(CGPlayer player){
        this.player = (DaifugoPlayer)player;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawFieldStatePanel(g, 0, (int)(0.4 * getHeight()), (int)(0.8 * getWidth()), (int)(0.1 * getHeight()));
        drawFieldCards(g, 0, 0, (int)(0.8 * getWidth()), (int)(0.4 * getHeight()));
        drawPlayerListPanel(g, (int)(0.8 * getWidth()), 0, (int)(0.2 * getWidth()), getHeight());
        drawMyCards(g, 0, (int)(0.5 * getHeight()), (int)(0.8 * getWidth()), (int)(0.4 * getHeight()));
        drawButtonPanel(g, (int)(0.4 * getWidth()), (int)(0.9 * getHeight()), (int)(0.4 * getWidth()), (int)(0.1 * getHeight()));
        if(player.getState() == DaifugoPlayer.STATE_END_GAME){
            drawGameEndPanel(g, (int)(0.2 * getWidth()), (int)(0.2 * getHeight()), (int)(0.6 * getWidth()), (int)(0.6 * getHeight()));
        }
    }

    /**
     * 場に出されたカードを描画する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void drawFieldCards(Graphics g, int x, int y, int w, int h){
        int fieldCardsSize = player.getFieldCards().size();
        if(fieldCardsSize > 0){
            int cardsSize = player.getFieldCards().get(0).size();

            int cardWidth = Math.min((int)(w / (0.4 * fieldCardsSize + 0.6)), (int)(h / ((0.3 * cardsSize + 0.7) * CardImage.getAspect())));
            int cardHeight = (int)(CardImage.getAspect() * cardWidth);
            int cardXGap = 0;
            if(fieldCardsSize > 1){
                cardXGap = Math.min((int)((w - cardWidth) / (fieldCardsSize - 1)), (int)(0.7 * cardWidth));
            }
            int cardYGap = 0;
            if(cardsSize > 1){
                cardYGap = (int)((h - cardHeight) / (cardsSize - 1));
            }
            int cardXPos = (int)((w - (cardXGap * (fieldCardsSize - 1) + cardWidth)) / 2);
            int cardYPos = (int)((h - (cardYGap * (cardsSize - 1) + cardHeight)) / 2);
    
            for(int i = 0; i < fieldCardsSize; i++){
                for(int j = 0; j < cardsSize; j++){
                    drawCard(g, x + cardXPos + cardXGap * i, y + cardYPos + cardYGap * j, cardWidth, cardHeight, player.getFieldCards().get(i).get(j));
                }
            }
        }

    }

    /**
     * 手札の描画や選択時の処理をする。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void drawMyCards(Graphics g, int x, int y, int w, int h){
        int state = (player.getState() == DaifugoPlayer.STATE_GAME || player.getState() == DaifugoPlayer.STATE_TURN || player.getState() == DaifugoPlayer.STATE_CARD_CHANGE) ? 1 : 0;
        Card selectingCard = drawCards(g, x, y, w, h, player.getCards(), player.getSelecteCards(), state);
        if(state == 1 && selectingCard != null && mouseClicked){
            player.select(selectingCard);
        }
    }

    /**
     * プレイヤーのリストとその状態を表示する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void drawPlayerListPanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        for(int i = 0; i < player.getPlayerNames().size(); i++){
            // プレイヤーの名前
            String name = player.getPlayerNames().get(i);
            drawString(g, x + (int)(0.1 * h), y + (int)(0.1 * h * i), name, (int)(0.05 * h));

            // プレイヤーの残りカード枚数
            Integer size = player.getPlayerCardSizes().get(name);
            if(size != null && size > 0){
                drawString(g, x + (int)(0.1 * h), y + (int)(0.1 * h * i + 0.05 * h), "残り" + size + "枚", (int)(0.04 * h));
            }

            // プレイヤーの前職
            Integer formerRank = player.getPlayerFormerRanks().get(name);
            if(formerRank != null){
                drawString(g, x + (int)(0.5 * w), y + (int)(0.1 * h * i + 0.05 * h), "元" + DaifugoTool.getRankName(formerRank, player.getPlayerNames().size()), (int)(0.04 * h));
            }

            // プレイヤーの順位
            Integer rank = player.getPlayerRanks().get(name);
            if(rank != null){
                drawString(g, x, y + (int)(0.1 * h * i + 0.03 * h), DaifugoTool.getRankName(rank, player.getPlayerNames().size()), (int)(0.04 * h));
            }

            // ターンのプレイヤー
            if(name.equals(player.getPlayerNameForTurn())){
                drawString(g, x + (int)(0.02 * h), y + (int)(0.1 * h * i + 0.01 * h), "→", (int)(0.07 * h), Font.BOLD);
            }
        }
    }

    private void drawFieldStatePanel(Graphics g, int x, int y, int w, int h){
        g.setColor(Color.BLACK);
        drawString(g, x, y, DaifugoTool.getFieldStateDescription(player.getFieldState()), h);
    }

    /**
     * カードを出す・パスするボタンを表示する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private void drawButtonPanel(Graphics g, int x, int y, int w, int h){
        boolean b = ((player.getState() == DaifugoPlayer.STATE_TURN || player.getState() == DaifugoPlayer.STATE_CARD_CHANGE) && player.checkSelectedCards());
        int state = b ? 0 : 1;
        boolean putButton = drawButton(g, x, y, (int)(0.45 * w), h, "出す", state);
        if(state == 0 && putButton && mouseClicked){
            player.put();
        }
        state = (player.getState() == DaifugoPlayer.STATE_TURN) ? 0 : 1;
        boolean passButton = drawButton(g, x + (int)(0.5 * w), y, (int)(0.45 * w), h, "パス", state);
        if(state == 0 && passButton && mouseClicked){
            player.pass();
        }
    }

    private void drawGameEndPanel(Graphics g, int x, int y, int w, int h){

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);

        // 称号表示
        drawString(g, x + (int)(0.1 * w), y + (int)(0.3 * h), DaifugoTool.getRankName(player.getPlayerRanks().get(player.getName()), player.getPlayerNames().size()), (int)(0.2 * h));

        // 名前表示
        drawString(g, x + (int)(0.1 * w), y + (int)(0.5 * h), player.getName(), (int)(0.2 * h));

        // 続行ボタン表示
        int state = (player.getState() == DaifugoPlayer.STATE_END_GAME) ? 0 : 1;
        if(state == 0 && drawButton(g, x + (int)(0.2 * w), y + (int)(0.8 * h), (int)(0.3 * w), (int)(0.1 * h), "続行", state) && mouseClicked){
            player.continueGame();
        }

        // プレイヤー一覧
        for(int i = 1; i <= player.getPlayerNames().size(); i++){
            // プレイヤーの名前
            String name = "";
            for(String playerName : player.getPlayerNames()){
                if(i == player.getPlayerRanks().get(playerName)){
                    name = playerName;
                    break;
                }
            }

            // 称号表示
            drawString(g, x + (int)(0.5 * w), y + (int)(0.1 * h * i), DaifugoTool.getRankName(i, player.getPlayerNames().size()), (int)(0.05 * h));

            // 名前表示
            drawString(g, x + (int)(0.7 * w), y + (int)(0.1 * h * i), name, (int)(0.05 * h));
        }
    }

}
