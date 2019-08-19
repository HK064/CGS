package gamedata.daifugo;

import java.util.LinkedList;
import java.util.List;

import source.CGPlayer;
import source.Card;

public class DaifugoPlayer extends CGPlayer{
    protected List<Card> myCards = new LinkedList<>();

    /**
     * プレイヤーが現在所持しているカードのリストを取得する。
     * @return
     */
    public List<Card> getCards(){
        return myCards;
    }
}
