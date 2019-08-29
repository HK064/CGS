package gamedata.speed;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
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
	public void setup(CGPlayer player) {
		this.player = (SpeedPlayer) player;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (player.getState() == SpeedPlayer.STATE_END_GAME) {
			drawGameEndPanel(g, (int) (0.4 * getWidth()), (int) (0.2 * getHeight()), (int) (0.6 * getWidth()),
					(int) (0.6 * getHeight()));
		}else {
			drawFieldCards(g, 0, 0, (int) (0.8 * getWidth()), (int) (0.6 * getHeight()));
			drawPlayerListPanel(g, (int) (0.8 * getWidth()), 0, (int) (0.2 * getWidth()), getHeight());
			drawMyCards(g, 0, (int) (0.75 * getHeight()), (int) (0.8 * getWidth()), (int) (0.35 * getHeight()));
			drawEnemyCards(g, 0, (int) (0.01 * getHeight()), (int) (0.8 * getWidth()), (int) (0.35 * getHeight()),
					player.getenemyfourCards());
			drawButtonPanel(g, (int) (0.75 * getWidth()), (int) (0.7 * getHeight()), (int) (0.4 * getWidth()),
					(int) (0.1 * getHeight()));
			drawFieldPanel(g, 0, 0, (int) (0.8 * getWidth()), (int) (0.6 * getHeight()));
			drawCount(g, (int) (0.1 * getWidth()), (int) (0.4 * getHeight()), (int) (0.2 * getWidth()),
					(int) (0.1 * getHeight()));
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
	private void drawFieldCards(Graphics g, int x, int y, int w, int h) {

		int cardWidth = Math.min((int) (w), (int) (h / ((2) * CardImage.getAspect())));
		int cardHeight = (int) (CardImage.getAspect() * cardWidth);
		int cardXGap = 0;

		cardXGap = Math.min((int) ((w - cardWidth)), (int) (0.7 * cardWidth));

		int cardYGap = 0;

		cardYGap = (int) ((h - cardHeight));

		int cardXPos = (int) ((w - (cardXGap + cardWidth)));
		int cardYPos = (int) ((h - (cardYGap + cardHeight) / 2));
		if (player.getrightFieldCards().size() > 0) {
			drawCard(g, x + cardXPos / 2 + cardXGap * 0, y + cardYPos, cardWidth, cardHeight,
					player.getrightFieldCards().get(0).get(0));
			drawCard(g, x + cardXPos / 2 + cardXGap * 2, y + cardYPos, cardWidth, cardHeight,
					player.getleftFieldCards().get(0).get(0));

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
	private void drawMyCards(Graphics g, int x, int y, int w, int h) {
		int state = 1;
		List<Card> getselect = new LinkedList<>();
		if (player.getselectcard() != null) {
			getselect = Card.convertToList(player.getselectcard().getCode());
		}
		Card selectingCard = drawCards(g, x, y, w, h, player.getFourCards(), getselect, state);
		if (selectingCard != null && mouseClicked) {
			player.select(selectingCard);
		}
	}

	/**
	 * 敵の手札の描画をする。
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawEnemyCards(Graphics g, int x, int y, int w, int h, List<Card> cards) {
		Point mousePos = getMousePosition();
		if (mousePos == null) {
			mousePos = new Point(-1, -1);
		}

		// Card 位置決定
		int cardWidth = Math.min((int) (w / (0.2 * cards.size() + 0.8)), (int) (h / (1.4 * CardImage.getAspect())));
		int cardHeight = (int) (CardImage.getAspect() * cardWidth);
		int cardGap = 0;
		if (cards.size() > 1) {
			cardGap = Math.min((int) ((w - cardWidth) / (cards.size() - 1)), cardWidth);
		}
		int cardPopGap = (int) (0.2 * cardHeight);
		int cardXPos = (int) ((w - (cardGap * (cards.size() - 1) + cardWidth)) / 2);
		int cardYPos = (int) ((h - cardHeight - 2 * cardPopGap) / 2);

		for (int i = 0; i < cards.size(); i++) {
			drawCard(g, x + cardXPos + cardGap * i, y + cardYPos, cardWidth, cardHeight,
					cards.get(i));
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
	private void drawPlayerListPanel(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < player.getPlayerNames().size(); i++) {
			// プレイヤーの名前
			String name = player.getPlayerNames().get(i);
			drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i), name, (int) (0.05 * h));

			// プレイヤーの残りカード枚数
			Integer size = player.getPlayerCardSizes().get(name);
			if (size != null && size > 0) {
				drawString(g, x + (int) (0.1 * h), y + (int) (0.1 * h * i + 0.05 * h), "残り" + size + "枚",
						(int) (0.04 * h));
			}

			// ターンのプレイヤー
			if (name.equals(player.getPlayerNameForTurn())) {
				drawString(g, x + (int) (0.02 * h), y + (int) (0.1 * h * i + 0.01 * h), "→", (int) (0.07 * h),
						Font.BOLD);
			}
		}
	}

	/**
	 * 選択している場所を表示する。
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawFieldPanel(Graphics g, int x, int y, int w, int h) {
		int fieldState = (player.getFieldState() == 1) ? 2 : 0;
		int cardWidth = Math.min((int) (w), (int) (h / ((2) * CardImage.getAspect())));
		int cardHeight = (int) (CardImage.getAspect() * cardWidth);
		int cardXGap = 0;

		Point mousePos = getMousePosition();
		if (mousePos == null) {
			mousePos = new Point(-1, -1);
		}

		cardXGap = Math.min((int) ((w - cardWidth)), (int) (0.7 * cardWidth));

		int cardYGap = 0;

		cardYGap = (int) ((h - cardHeight));

		int cardXPos = (int) ((w - (cardXGap + cardWidth)));
		int cardYPos = (int) ((h - (cardYGap + cardHeight) / 2));
		int xf = x + cardXPos / 2;
		int yf = y + cardYPos;
		if ((xf <= mousePos.x) && (mousePos.x <= xf + cardWidth)
				&& (yf <= mousePos.y) && (mousePos.y < yf + cardHeight) && mouseClicked) {
			player.setFieldState(0);
		}
		if ((xf + cardXGap * 2 <= mousePos.x) && (mousePos.x < xf + cardXGap * 2 + cardWidth)
				&& (yf <= mousePos.y) && (mousePos.y < yf + cardHeight) && mouseClicked) {
			player.setFieldState(1);
		}
		g.setColor(Color.RED);
		g.drawRect(x + cardXPos / 2 + cardXGap * fieldState, y + cardYPos, cardWidth, cardHeight);
		g.drawRect(x + cardXPos / 2 + cardXGap * fieldState - 2, y + cardYPos - 2, cardWidth + 4, cardHeight + 4);

	}

	/**
	 * カードを出す・パスするボタンを表示する。
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawButtonPanel(Graphics g, int x, int y, int w, int h) {

		boolean putButton = drawButton(g, x, y, (int) (0.45 * w), h, "出す", 0);
		if (putButton && mouseClicked) {
			player.put();
		}

	}

	/**
	 * カウントダウンを描写する
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawCount(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.BLACK);
		int count = player.getCountdownCount();
		if (count == 4) {
			drawString(g, x, y, "い", (int) 1 * h);
		} else if (count == 3) {
			drawString(g, x, y, "いっせー", (int) 1 * h);
		} else if (count == 2) {
			drawString(g, x, y, "いっせーのー", (int) 1 * h);
		} else if (count == 1) {
			drawString(g, x, y, "いっせーのーで", (int) 1 * h);
		}
	}

	/**
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */

	private void drawGameEndPanel(Graphics g, int x, int y, int w, int h) {

		g.setColor(Color.WHITE);
		g.fillRect(x, y, w, h);
		g.setColor(Color.BLACK);
		// プレイヤーの名前
		String name = "";
		if(player.getWinplayer()==player.getPlayerNames().get(0)) {
			name = player.getPlayerNames().get(1);
		}else {
			name = player.getPlayerNames().get(0);
		}
		// 名前表示
		drawString(g, x + (int) (0.7 * w), y + (int) (0.1 * h), "勝者："+player.getWinplayer(), (int) (0.05 * h));
		drawString(g, x + (int) (0.7 * w), y + (int) (0.1 * h * 2), "敗者："+ name, (int) (0.05 * h));
	}
}
