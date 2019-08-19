package source.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import source.CGPlayer;
import source.Card;

public class PlayPanel extends CGSPanel{
    private static final long serialVersionUID = 1L;
    protected CGPlayer player;

    public void setup(CGPlayer player){
        this.player = player;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());



    }

    /**
     * カードを描画する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param cards
     * @param type 0:マウスオーバーで選択しない, 1:マウスオーバーで選択する
     * @return 選択されているカードの要素番号（未選択は -1）
     */
    protected int drawCards(Graphics g, int x, int y, int w, int h, List<Card> cards, int type){
        Point mousePos = getMousePosition();
        if(mousePos == null){
            mousePos = new Point(-1, -1);
        }

        // Card 描画
        int cardWidth = Math.min((int)(0.9 * w / (0.2 * cards.size() + 0.8)), (int)(0.9 * h / ((Math.sqrt(5) + 1) / 2)));
        int cardHeight = (int)(cardWidth * ((Math.sqrt(5) + 1) / 2));
        int cardGap = (int)((0.9 * w - cardWidth) / (cards.size() - 1));
        int cardXPos = (int)((w - (cardGap * (cards.size() - 1) + cardWidth)) / 2);
        int cardYPos = (int)((h - cardHeight) / 2);
        int cardFrame = Math.max(1, (int)(0.02 * cardWidth));

        int selectedCardNumber = -1;
        for(int i = 0; i < cards.size(); i++){
            // マウスオーバー？
            if((cardXPos + cardGap * i <= mousePos.x) && (mousePos.x < cardXPos + cardGap * i + cardWidth) && (cardYPos <= mousePos.y) && (mousePos.y < cardYPos + cardHeight)){
                if(!((i != cards.size() - 1) && (mousePos.x >= cardXPos + cardGap * (i + 1)))){
                    selectedCardNumber = i;
                }
            }

            g.setColor(Color.BLACK);
            g.fillRect(cardXPos + cardGap * i, cardYPos, cardWidth, cardHeight);
            if((type == 1) && (i == selectedCardNumber)){
                g.setColor(Color.CYAN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.fillRect(cardXPos + cardGap * i + cardFrame, cardYPos + cardFrame, cardWidth - 2 * cardFrame, cardHeight - 2 * cardFrame);
            g.setColor(Color.BLACK);
            g.drawString(cards.get(i).getString(), cardXPos + cardGap * i + cardFrame, cardYPos + cardFrame + 20);
        }
        return selectedCardNumber;
    }

}
