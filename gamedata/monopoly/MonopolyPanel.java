package gamedata.monopoly;

import java.awt.Color;
import java.awt.Graphics;

import source.CGPlayer;
import source.display.PlayPanel;

public class MonopolyPanel extends PlayPanel {
    private static final long serialVersionUID = 1L;
    private MonopolyPlayer player;

    @Override
    public void setup(CGPlayer player){
        this.player = (MonopolyPlayer)player;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());


        drawDice(g, 100, 100, 100, 100, 1);
        drawDice(g, 300, 100, 100, 100, 2);
        drawDice(g, 500, 100, 100, 100, 3);
        drawDice(g, 100, 300, 100, 100, 4);
        drawDice(g, 300, 300, 100, 100, 5);
        drawDice(g, 500, 300, 100, 100, 6);
    }

    private void drawBoard(Graphics g, int x, int y, int w, int h){
        int s = Math.min(w, h);
    }

    private void drawPlayerList(Graphics g, int x, int y, int w, int h){
    }

}
