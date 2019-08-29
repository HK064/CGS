package gamedata.speed;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import source.CGServer;
import source.Card;

public class SpeedServer extends CGServer {
	private Map<String, List<Card>> playerCards = new HashMap<>();
	private Map<String, List<Card>> fourplayerCards = new HashMap<>();
	private Map<Integer, Card> fieldCard = new HashMap<>();
	static final int RIGHT_FIELD = 0; //右の場の識別子（Player１の出す場所）
	static final int LEFT_FIELD = 1; //右左の場の識別子（Player２の出す場所)
	private static final int STATE_COUNT = 1;
	private static final int STATE_GAME = 0;
	private int state = STATE_GAME;

	private int countdownCount;

	@Override
	public void startGame() {
		String str = "110";
		for (String name : playerNames) {
			str += " " + name;
		}
		sendAll(str);

		// カードを配る
		List<Card> redcards = Card.generateRedCards();
		List<Card> blackcards = Card.generateBlackCards();
		Collections.shuffle(redcards, random);
		Collections.shuffle(blackcards, random);
		// カードの送信
		playerCards.put(playerNames.get(0), redcards);
		playerCards.put(playerNames.get(1), blackcards);
		sendOne(playerNames.get(0), "111 " + Card.convertToCodes(redcards));
		sendOne(playerNames.get(1), "111 " + Card.convertToCodes(blackcards));

		//相手のカード4枚の周知
		List<Card> redfourcards = new LinkedList<>();
		List<Card> blackfourcards = new LinkedList<>();
		for (int i = 0; i < 4; i++) {
			redfourcards.add(playerCards.get(playerNames.get(0)).get(0));
			blackfourcards.add(playerCards.get(playerNames.get(1)).get(0));
			playerCards.get(playerNames.get(0)).remove(0);
			playerCards.get(playerNames.get(1)).remove(0);
		}
		fourplayerCards.put(playerNames.get(0), redfourcards);
		fourplayerCards.put(playerNames.get(1), blackfourcards);
		sendOne(playerNames.get(1), "113 " + Card.convertToCodes(redfourcards));
		sendOne(playerNames.get(0), "113 " + Card.convertToCodes(blackfourcards));

		// 各プレイヤーの残りカード枚数の送信
		sendPlayerCardSizes();

		// ゲーム開始
		sendAll("115");
		startCountdown(4);
		if (state == STATE_GAME) {

		}
	}



	@Override
	public void listener(String name, String data) {

		String[] str = data.split(" ");
		if (str[0].equals("121")) {
			// カードを出した
			List<Card> cards = Card.convertToList(str[1]);
			int field_number = Integer.parseInt(str[2]);
			takePlayerAction(name, cards, field_number);
			/*
			fieldCard.put(field_number,cards.get(0) );
			sendOne(name, "123");
			sendAll("120 "+ cards.get(0).getCode() + field_number);
			for(int i=0;i<fourplayerCards.get(name).size();i++) {
				if(fourplayerCards.get(name).get(i).equals(cards.get(0))) {
					fourplayerCards.get(name).remove(i);
					fourplayerCards.get(name).add(i,playerCards.get(name).get(0));
					playerCards.get(name).remove(0);
					if(playerNames.get(0).contains(name)) {
						sendOne(playerNames.get(1), "120 "+Card.convertToCodes(cards)+" "+field_number);
					}
				}
			}
			sendPlayerCardSizes();
			*/
		}

	}

	private void sendPlayerCardSizes() {
		String str = "126";
		for (String name : playerNames) {
			str += " " + name + " " + String.valueOf(playerCards.get(name).size()+fourplayerCards.get(name).size());
		}
		sendAll(str);
	}

	/**
	 * プレイヤーの行動に関する処理を行う。
	 *
	 * @param name
	 * @param cards null なら「パス」
	 */
	private void takePlayerAction(String name, List<Card> cards, int field_number) {
		if (cards != null) {
			// プレイヤーがカードを持っているか
			boolean b = fourplayerCards.get(name).contains(cards.get(0));
			if (!b) {
				sendOne(name, "124");
				return;
			}
			if ((((fieldCard.get(field_number).getNumber() + 1) % 13 == cards.get(0).getNumber() % 13)
					|| (fieldCard.get(field_number).getNumber() % 13 == (cards.get(0).getNumber() + 1) % 13)) ||
					(fieldCard.get(field_number).isJoker()) || cards.get(0).isJoker()) {
				// 承認
				sendOne(name, "123");
			} else {
				// 拒否
				sendOne(name, "124");
				return;
			}

			// 場の更新
			fieldCard.put(field_number, cards.get(0));

			// プレイヤーのカード削除、補充
			for (int i = 0; i < fourplayerCards.get(name).size(); i++) {
				if (fourplayerCards.get(name).get(i).equals(cards.get(0))) {
					fourplayerCards.get(name).remove(i);
					if(playerCards.get(name).size()>0) {
						fourplayerCards.get(name).add(i, playerCards.get(name).get(0));
						playerCards.get(name).remove(0);
					}
				}
			}

			sendAll("120 " + Card.convertToCodes(cards) + " " + field_number);
			sendPlayerCardSizes();
			if (playerNames.get(0).equals(name)) {
				sendOne(playerNames.get(1), "113 " + Card.convertToCodes(fourplayerCards.get(name)));
			} else {
				sendOne(playerNames.get(0), "113 " + Card.convertToCodes(fourplayerCards.get(playerNames.get(1))));
			}

			// 上がりか
			if (fourplayerCards.get(name).size() == 0) {
				endPlayer(name);

				// 終了か
				int remainingPlayersNumber = 0;
				for (String playerName : playerNames) {
					if (playerCards.get(playerName).size() > 0) {
						remainingPlayersNumber++;
					}
				}
				if (remainingPlayersNumber <= 1) {
					endGame();
				}
			}

			// 流れるか
			check();

		}
	}

	private boolean check() {
		boolean check = false;
		for (int i = 0; i < 2; i++) {
			for (Card x : fourplayerCards.get(playerNames.get(i))) {
				for (int j = 0; j < 2; j++) {
					if ((fieldCard.get(j).getNumber() + 1) % 13 == x.getNumber() % 13 ||
							fieldCard.get(j).getNumber() % 13 == (x.getNumber() + 1) % 13) {
						check = true;
					} else if (x.isJoker() || fieldCard.get(j).isJoker()) {
						check = true;
					}
				}
			}
		}
		if (!check) {
			startCountdown(4);
			/*
			if(state == STATE_GAME) {
			fieldCard.put(RIGHT_FIELD, playerCards.get(playerNames.get(0)).get(0));
			fieldCard.put(LEFT_FIELD, playerCards.get(playerNames.get(1)).get(0));
			sendAll("120 " + playerCards.get(playerNames.get(0)).get(0).getCode() + " " + RIGHT_FIELD);
			sendAll("120 " + playerCards.get(playerNames.get(1)).get(0).getCode() + " " + LEFT_FIELD);
			playerCards.get(playerNames.get(0)).remove(0);
			playerCards.get(playerNames.get(1)).remove(0);
			}
			*/
		}
		return check;
	}

	private void endPlayer(String name) {
		playerCards.get(name).clear();
		sendAll("130 " + name);
	}

	private void endGame() {
		for (String playerName : playerNames) {
			if (playerCards.get(playerName).size() > 0) {
				endPlayer(playerName);
			}
		}
		sendAll("140");

	}

	//カウントダウン
	private void startCountdown(int count) {
		state = STATE_COUNT;
		countdownCount = count;
		// タイマー起動
		//while (countdownCount >= 0) {
		Timer countdowner = new Timer();
		countdowner.schedule(new TimerTask() {
			@Override
			public void run() {
				sendAll("125 " + countdownCount);
				countdownCount--;
				if (countdownCount == -1) {
					state = STATE_GAME;
					fieldCard.put(RIGHT_FIELD, playerCards.get(playerNames.get(0)).get(0));
					fieldCard.put(LEFT_FIELD, playerCards.get(playerNames.get(1)).get(0));
					sendAll("120 " + playerCards.get(playerNames.get(0)).get(0).getCode() + " " + RIGHT_FIELD);
					sendAll("120 " + playerCards.get(playerNames.get(1)).get(0).getCode() + " " + LEFT_FIELD);
					playerCards.get(playerNames.get(0)).remove(0);
					playerCards.get(playerNames.get(1)).remove(0);
					while (!check()) {
					}
					countdowner.cancel();
				}
			}
		}, 0, 1000);
		//}
	}

}
