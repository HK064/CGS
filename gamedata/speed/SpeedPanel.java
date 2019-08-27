package gamedata.speed;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import source.CGPlayer;
import source.Card;
import source.display.PlayPanel;
import source.file.CardImage;

public class SpeedPanel extends PlayPanel {
	private static final long serialVersionUID = 1L;
    private SpeedPlayer player;

    @Override
    public void setup(CGPlayer player){
        this.player = (SpeedPlayer)player;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawFieldCards(g, 0, 0, (int)(0.8 * getWidth()), (int)(0.4 * getHeight()));
        drawPlayerListPanel(g, (int)(0.8 * getWidth()), 0, (int)(0.2 * getWidth()), getHeight());
        drawMyCards(g, 0, (int)(0.5 * getHeight()), (int)(0.8 * getWidth()), (int)(0.4 * getHeight()));
        drawButtonPanel(g, (int)(0.4 * getWidth()), (int)(0.9 * getHeight()), (int)(0.4 * getWidth()), (int)(0.1 * getHeight()));
        if(player.getState() == SpeedPlayer.STATE_END_GAME){
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

            int cardWidth = Math.min((int)(w / (0.6)), (int)(h / ((0.7) * CardImage.getAspect())));
            int cardHeight = (int)(CardImage.getAspect() * cardWidth);
            int cardXGap = 0;

            cardXGap = Math.min((int)((w - cardWidth)), (int)(0.7 * cardWidth));

            int cardYGap = 0;

            cardYGap = (int)((h - cardHeight));

            int cardXPos = (int)((w - (cardXGap + cardWidth)) / 2);
            int cardYPos = (int)((h - (cardYGap + cardHeight)) / 2);

            drawCard(g, x + cardXPos + cardXGap * 1, y + cardYPos + cardYGap * 1, cardWidth, cardHeight, player.getrightFieldCards().get(0).get(0));



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
        int state = (player.getState() == SpeedPlayer.STATE_GAME || player.getState() == SpeedPlayer.STATE_TURN) ? 1 : 0;
        List<Card> getselect = new LinkedList<>();
        getselect = Card.convertToList(player.getSelecteCards().getCode());
        Card selectingCard = drawCards(g, x, y, w, h, getselect, getselect, state);
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




            // ターンのプレイヤー
            if(name.equals(player.getPlayerNameForTurn())){
                drawString(g, x + (int)(0.02 * h), y + (int)(0.1 * h * i + 0.01 * h), "→", (int)(0.07 * h), Font.BOLD);
            }
        }
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
        boolean b = player.getState() == SpeedPlayer.STATE_TURN;
        int state = 1;
        if(b && player.checkSelectedCards()){
            state = 0;
        }
        boolean putButton = drawButton(g, x, y, (int)(0.45 * w), h, "出す", state);
        state = 1;
        if(b){
            state = 0;
        }
        boolean passButton = drawButton(g, x + (int)(0.5 * w), y, (int)(0.45 * w), h, "パス", state);
        if(putButton && mouseClicked){
            player.put();
        }

    }

    private void drawGameEndPanel(Graphics g, int x, int y, int w, int h){

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);

        // 称号表示
        //drawString(g, x, y + (int)(0.3 * h), DaifugoTool.getRankName(player.getPlayerRanks().get(player.getName()), player.getPlayerNames().size()), (int)(0.1 * h));

        drawString(g, x, y + (int)(0.5 * h), player.getName(), (int)(0.1 * h));

        // プレイヤー一覧
        for(int i = 1; i <= player.getPlayerNames().size(); i++){
            // プレイヤーの名前
            String name = "";
            for(String playerName : player.getPlayerNames()){

            }

            // 称号表示
           // drawString(g, x + (int)(0.5 * w), y + (int)(0.1 * h * i), DaifugoTool.getRankName(i, player.getPlayerNames().size()), (int)(0.05 * h));

            // 名前表示
            drawString(g, x + (int)(0.7 * w), y + (int)(0.1 * h * i), name, (int)(0.05 * h));
        }
    }
}
