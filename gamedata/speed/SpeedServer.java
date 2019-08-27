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
		for(int i=0;i<4;i++) {
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
		fieldCard.put(RIGHT_FIELD, playerCards.get(playerNames.get(0)).get(0));
		fieldCard.put(LEFT_FIELD, playerCards.get(playerNames.get(1)).get(0));
		sendAll("120 " + playerCards.get(playerNames.get(0)).get(0) + " " + RIGHT_FIELD);
		sendAll("120 " + playerCards.get(playerNames.get(1)).get(0) + " " + LEFT_FIELD);
		playerCards.get(playerNames.get(0)).remove(0);
		playerCards.get(playerNames.get(1)).remove(0);
	}

	@Override
	public void listener(String name, String data) {

		String[] str = data.split(" ");
		if (str[0].equals("121")) {
			// カードを出した
			List<Card> cards = Card.convertToList(str[1]);
			int field_number = Integer.parseInt(str[2]);
			takePlayerAction(name, cards, field_number);
		}

	}

	@Override
	protected void nextPlayer() {
		do {
			super.nextPlayer();
		} while (playerCards.get(playerNameForTurn).size() <= 0);

		sendAll("120 " + playerNameForTurn);
	}

	private void sendPlayerCardSizes() {
		String str = "126";
		for (String name : playerNames) {
			str += " " + name + " " + String.valueOf(playerCards.get(name).size());
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
			boolean b = true;
			for (Card card : cards) {
				if (!playerCards.get(name).contains(card)) {
					b = false;
					return;
				}
			}

			if (b && ((fieldCard.get(field_number).getNumber() + 1) % 13 == cards.get(0).getNumber()
					|| fieldCard.get(field_number).getNumber() == (cards.get(0).getNumber() + 1) % 13)) {
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
					fourplayerCards.get(name).add(i, playerCards.get(name).get(0));
					playerCards.get(name).remove(0);
				}
			}

			sendAll("120 " + Card.convertToCodes(cards) + " " + field_number);
			sendPlayerCardSizes();

			// 上がりか
			if (playerCards.get(name).size() == 0) {
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
			boolean check = false;
			for (int i = 0; i < 2; i++) {
				for (Card x : fourplayerCards.get(playerNames.get(i))) {
					for (int j = 0; j < 2; j++) {
						if ((fieldCard.get(j).getNumber() + 1) % 13 == x.getNumber() ||
								fieldCard.get(j).getNumber() == (x.getNumber() + 1) % 13) {
							check = true;
						} else if (x.getSuitInt() == 4 || fieldCard.get(j).getSuitInt() == 4) {
							check = true;
						}
					}
				}
			}
			if (!check) {
				sendAll("125");
				(new Timer()).schedule(new TimerTask() {

					@Override
					public void run() {
						fieldCard.put(RIGHT_FIELD, playerCards.get(playerNames.get(0)).get(0));
						fieldCard.put(LEFT_FIELD, playerCards.get(playerNames.get(1)).get(0));
						sendAll("120 " + playerCards.get(playerNames.get(0)).get(0) + " " + RIGHT_FIELD);
						sendAll("120 " + playerCards.get(playerNames.get(1)).get(0) + " " + LEFT_FIELD);
						playerCards.get(playerNames.get(0)).remove(0);
						playerCards.get(playerNames.get(1)).remove(0);
					}
				}, 3000);
			}

		}
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

}
