package gamedata.speed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import source.CGPlayer;
import source.Card;

public class SpeedPlayer extends CGPlayer{
  private List<Card> cards = new LinkedList<>();
  private List<Card> enemyfourCards = new LinkedList<>(); //敵の4枚のカード
  private List<Card> fourCards = new LinkedList<>(); //自分の４枚のカード
  private Map<String, Integer> playerCardSizes = new HashMap<>();
  private Card selectcard;
  private List<List<Card>> rightfieldCards = new ArrayList<>();
  private List<List<Card>> leftfieldCards = new ArrayList<>();
  static final int STATE_READY = 0; // 準備中
  static final int STATE_GAME = 1; // ゲーム中
  static final int STATE_TURN = 2; // 自分のターン（行動できる）
  static final int STATE_END_TURN = 3; // 自分のターン（行動終了）
  static final int STATE_WAIT_REPLY = 4;
  static final int STATE_END_GAME = 5;
  static final int STATE_COUNT = 6;
  private int state = STATE_READY;
  static final int RIGHT_FIELD = 0; //右の場の識別子（Player１の出す場所）
  static final int LEFT_FIELD = 1; //右左の場の識別子（Player２の出す場所）
  private int fieldState = 0;
  private int countdownCount =0;



@Override
  public void listener(String date){
    String[] str = date.split(" ");
    if(str[0].equals("110")){
      //プレイヤーリスト受信
      for(int i = 1;i<str.length;i++){
        playerNames.add(str[i]);
      }

    }else if(str[0].equals("111")){
    	//カードが送られる

      cards = Card.convertToList(str[1]);
      for(int i=0;i<4;i++) {
    	  fourCards.add(cards.get(0));
    	  cards.remove(0);
      }


    }else if(str[0].equals("113")){
    	//相手の場にある４枚のカードの情報
    		enemyfourCards = Card.convertToList(str[1]);


    }else if(str[0].equals("115")){
    	//ゲーム開始

    	state = STATE_GAME;

    }else if(str[0].equals("120")){
    	//右に出されたカード情報

    	if(Integer.parseInt(str[2])==RIGHT_FIELD) {
    		rightfieldCards.add(0,Card.convertToList(str[1]));

    	//左に出されたカード情報

    	}else {
    		leftfieldCards.add(0,Card.convertToList(str[1]));
    	}

    }else if (str[0].equals("123")) {
        // 行動が承認されました。
        if (state == STATE_WAIT_REPLY) {
            // プレイヤーのカード削除
        	for(int i=0;i<fourCards.size();i++) {
				if(fourCards.get(i).equals(selectcard)) {
					fourCards.remove(i);
					fourCards.add(i,cards.get(0));
					cards.remove(0);
				}
			}
            // 選択しているカードのリセット
            selectcard = null;
        }
    } else if (str[0].equals("124")) {
        // 行動が拒否されました。

        if (state == STATE_WAIT_REPLY) {

            state = STATE_TURN;
        }
    }else if(str[0].equals("125")) {
    	state = STATE_COUNT;
    	countdownCount = Integer.parseInt(str[1]);
    	if(Integer.parseInt(str[1])==0) {
    		cards.remove(0);
    		state = STATE_GAME;
    	}
    }else if(str[0].equals("126")) {
    	//プレイヤーの残り枚数
    	for (int i = 1; i < str.length; i += 2) {
            playerCardSizes.put(str[i], Integer.parseInt(str[i + 1]));
        }

    }else if(str[0].equals("130")){
    	//プレイヤーが上がりました。

    	state = STATE_END_GAME;

    }
  }

/**
   * プレイヤーが現在所持しているカードのリストを取得する。
   *
   * @return
   */

  public Card getSelecteCards() {
      return selectcard;
  }

  public List<List<Card>> getrightFieldCards() {
      return rightfieldCards;
  }

  public List<List<Card>> getleftFieldCards() {
      return leftfieldCards;
  }

  public Card getselectcard() {
	  return selectcard;
  }
  public Map<String, Integer> getPlayerCardSizes() {
      return playerCardSizes;
  }


  public int getState() {
      return state;
  }

  public List<Card> getenemyfourCards() {
	return enemyfourCards;
  }

public List<Card> getFourCards() {
		return fourCards;
  }
public int getFieldState() {
	return fieldState;
}

public void setFieldState(int fieldState) {
	this.fieldState = fieldState;
}

public int getCountdownCount() {
	return countdownCount;
}

/**
   * カードを選択もしくは選択解除します。
   *
   * @param card
   */
  public synchronized void select(Card card) {
      if (fourCards.contains(card)) {
              selectcard = card;

      }
  }

  /**
   * カードを出します。
   */
  public synchronized void put() {
      if (checkSelectedCards()) {
          state = STATE_WAIT_REPLY;
          send("121 " + selectcard.getCode() +" "+fieldState);
      }
  }


  /**
   * 選択しているカードを場に出せるか調べます。
   */
  public boolean checkSelectedCards() {
	  List<List<Card>> fieldCards = new ArrayList<>();
	  if(fieldState==RIGHT_FIELD) {
		  fieldCards = rightfieldCards;
	  }else {
		  fieldCards = leftfieldCards;
	  }
	  if((fieldCards.get(0).get(0).getNumber()+1)%13==selectcard.getNumber()%13 ||
			  fieldCards.get(0).get(0).getNumber()%13==(selectcard.getNumber()+1)%13||
			  fieldCards.get(0).get(0).isJoker()||selectcard.isJoker()) {
		  return true;
	  }
      return false;
  }
}
