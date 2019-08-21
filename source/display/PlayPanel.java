package source.display;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import source.CGPlayer;
import source.Card;
import source.file.CardImage;

public class PlayPanel extends CGSPanel{
    private static final long serialVersionUID = 1L;

    public void setup(CGPlayer player){
        
    }
    
    protected void drawCard(Graphics g, int x, int y, int w, int h, Card card){
        drawImage(g, x, y, w, h, CardImage.getImage(card.getSuitInt(), card.getNumber()));
    }

    /**
     * カードを描画する。
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param cards
     * @param selectedCards
     * @param type 0:マウスオーバーで選択しない, 1:マウスオーバーで選択する
     * @return 選択されているカード（未選択はnull）
     */
    protected Card drawCards(Graphics g, int x, int y, int w, int h, List<Card> cards, List<Card> selectedCards, int type){
        Point mousePos = getMousePosition();
        if(mousePos == null){
            mousePos = new Point(-1, -1);
        }

        // Card 位置決定
        int cardWidth = Math.min((int)(w / (0.2 * cards.size() + 0.8)), (int)(h / (1.4 * CardImage.getAspect())));
        int cardHeight = (int)(CardImage.getAspect() * cardWidth);
        int cardGap = 0;
        if(cards.size() > 1){
            cardGap = Math.min((int)((w - cardWidth) / (cards.size() - 1)), cardWidth);
        }
        int cardPopGap = (int)(0.2 *cardHeight);
        int cardXPos = (int)((w - (cardGap * (cards.size() - 1) + cardWidth)) / 2);
        int cardYPos = (int)((h - cardHeight - 2 * cardPopGap) / 2);

        Card selectedCard = null;
        for(int i = 0; i < cards.size(); i++){
            // 選択済み？
            int selected = selectedCards.contains(cards.get(i)) ? 1 : 0;
            
            // マウスオーバー？
            if((x + cardXPos + cardGap * i <= mousePos.x) && (mousePos.x < x + cardXPos + cardGap * i + cardWidth) && (y + cardYPos - selected * cardPopGap <= mousePos.y) && (mousePos.y < y + cardYPos + cardHeight) && (type == 1)){
                selectedCard = cards.get(i);
            }
        }
        for (int i = 0; i < cards.size(); i++) {
            int selected = selectedCards.contains(cards.get(i)) ? 1 : 0;
            if (type == 1 && cards.get(i).equals(selectedCard)) {
                selected = 2;
            }
            drawCard(g, x + cardXPos + cardGap * i, y + cardYPos - selected * cardPopGap, cardWidth, cardHeight, cards.get(i));
        }
        return selectedCard;
    }

}
