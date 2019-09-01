package gamedata.monopoly;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonopolyBoard {
    static final int LAND_MAX = 41; // 0: GO, ... , 40: JAIL
    private static final String[] landName = { "GO", "高知", "共同基金", "香川", "所得税", "JR九州", "鹿児島", "チャンス", "長崎", "福岡",
            "刑務所見学", "山口", "中国電力", "広島", "鳥取", "JR西日本", "兵庫", "チャンス", "大阪", "京都", "駐車場", "愛知", "チャンス", "名古屋", "静岡",
            "JR東海", "神奈川", "TOKYO", "東京都水道局", "さいたま", "刑務所行き", "福島", "宮城", "共同基金", "岩手", "JR東日本", "チャンス", "札幌", "物品税",
            "函館", "刑務所" };
    private static final int[] landPrice = { 0, 60, 0, 60, 0, 200, 100, 0, 100, 120, 0, 140, 150, 140, 160, 200, 180, 0,
            180, 200, 0, 220, 0, 220, 240, 200, 260, 260, 150, 280, 0, 300, 300, 0, 320, 200, 0, 350, 0, 400, 0 };
    private static final int[] landBuildCost = { 0, 50, 0, 50, 0, 0, 50, 0, 50, 50, 0, 100, 0, 100, 100, 0, 100, 0, 100,
            100, 0, 150, 0, 150, 150, 0, 150, 150, 0, 150, 0, 200, 200, 0, 200, 0, 0, 200, 0, 200, 0 };
    private String[] landOwner = new String[LAND_MAX]; // 未所有: null
    private boolean[] landMortgage = new boolean[LAND_MAX];
    private int[] landBuilding = new int[LAND_MAX];

    private List<String> playerNames;
    private Map<String, Integer> playerMoneys = new HashMap<>();
    private Map<String, Integer> playerPositions = new HashMap<>();

    /**
     * PROPERTY: 土地, COMPANY: 公共会社, INCOME_TAX: $200, LUXURY_TAX: $100
     */
    enum LandType {
        GO, JAIL, JAIL_VISIT, GO_JAIL, PARKING, PROPERTY, RAILROAD, COMPANY, CHANCE_CARD, COMMUNITY_CARD, INCOME_TAX,
        LUXURY_TAX;
    }

    enum ColorGroup {
        BROWN, LIGHT_BLUE, LIGHT_PURPLE, ORANGE, RED, YELLOW, GREEN, DARK_BLUE;

        Color getColor() {
            switch (this) {
            case BROWN:
                return new Color(0x96, 0x4b, 0x00);
            case LIGHT_BLUE:
                return new Color(0xad, 0xd8, 0xe6);
            case LIGHT_PURPLE:
                return new Color(0xc5, 0x8b, 0xe7);
            case ORANGE:
                return new Color(0xff, 0xa5, 0x00);
            case RED:
                return new Color(0xff, 0x00, 0x00);
            case YELLOW:
                return new Color(0xff, 0xff, 0x00);
            case GREEN:
                return new Color(0x00, 0xff, 0x00);
            case DARK_BLUE:
                return new Color(0x00, 0x00, 0x8b);
            }
            return Color.WHITE;
        }
    }

    MonopolyBoard() {
        Arrays.fill(landOwner, null);
        Arrays.fill(landMortgage, false);
        Arrays.fill(landBuilding, 0);
    }

    LandType getType(int land) {
        switch (land) {
        case 0:
            return LandType.GO;
        case 40:
            return LandType.JAIL;
        case 10:
            return LandType.JAIL_VISIT;
        case 30:
            return LandType.GO_JAIL;
        case 20:
            return LandType.PARKING;
        case 2:
        case 17:
        case 33:
            return LandType.COMMUNITY_CARD;
        case 7:
        case 22:
        case 36:
            return LandType.CHANCE_CARD;
        case 4:
            return LandType.INCOME_TAX;
        case 38:
            return LandType.LUXURY_TAX;
        case 5:
        case 15:
        case 25:
        case 35:
            return LandType.RAILROAD;
        case 12:
        case 28:
            return LandType.COMPANY;
        default:
            return LandType.PROPERTY;
        }
    }

    ColorGroup getColor(int land) {
        switch (land) {
        case 1:
        case 3:
            return ColorGroup.BROWN;
        case 6:
        case 8:
        case 9:
            return ColorGroup.LIGHT_BLUE;
        case 11:
        case 13:
        case 14:
            return ColorGroup.LIGHT_PURPLE;
        case 16:
        case 18:
        case 19:
            return ColorGroup.ORANGE;
        case 21:
        case 23:
        case 24:
            return ColorGroup.RED;
        case 26:
        case 27:
        case 29:
            return ColorGroup.YELLOW;
        case 31:
        case 32:
        case 34:
            return ColorGroup.GREEN;
        case 37:
        case 39:
            return ColorGroup.DARK_BLUE;
        default:
            return null;
        }
    }

    boolean isMonopoly(int land) {
        if (getType(land) == LandType.PROPERTY) {
            return isMonopoly(getColor(land));
        }
        return false;
    }

    boolean isMonopoly(ColorGroup color) {
        // TODO
        return false;
    }

    String getName(int land) {
        return landName[land];
    }

    int getPrice(int land) {
        return landPrice[land];
    }

    String getOwner(int land) {
        return landOwner[land];
    }

    void setOwner(int land, String owner) {
        landOwner[land] = owner;
    }

    int getRent(int land) {
        LandType type = getType(land);
        // TODO
        return 0;
    }

    boolean isMortgage(int land) {
        return landMortgage[land];
    }

    /**
     * 抵当に入れる。
     *
     * @param land
     */
    void mortgage(int land) {
        landMortgage[land] = true;
    }

    void unmortgage(int land) {
        landMortgage[land] = false;
    }

    int getBuildCost(int land) {
        return landBuildCost[land];
    }

    boolean canBuild(int land) {
        // TODO
        return false;
    }

    void build(int land) {
        landBuilding[land]++;
    }

    int getBuilding(int land){
        return landBuilding[land];
    }

    boolean canUnbuild(int land) {
        if (landBuilding[land] <= 4)
            return (landBuilding[land] > 0);
        // ホテル
        // TODO
        return false;
    }

    void unbuild(int land) {
        landBuilding[land]--;
    }

    int getRemainingHouse() {
        int h = 0;
        for (int i = 0; i < LAND_MAX; i++) {
            if (landBuilding[i] <= 4) {
                h += landBuilding[i];
            }
        }
        return 32 - h;
    }

    int getRemainingHotel() {
        int h = 0;
        for (int i = 0; i < LAND_MAX; i++) {
            if (landBuilding[i] == 5) {
                h++;
            }
        }
        return 12 - h;
    }

    void setPlayers(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    List<String> getPlayers(){
        return playerNames;
    }

    void setPlayerMoney(String name, int money) {
        playerMoneys.put(name, money);
    }

    void payPlayerMoney(String name, int money) {
        playerMoneys.put(name, playerMoneys.get(name)-money);
    }

    void addPlayerMoney(String name, int money) {
        playerMoneys.put(name, playerMoneys.get(name)+money);
    }

    int getPlayerMoney(String name) {
        return playerMoneys.get(name);
    }

    void setPlayerPosition(String name, int position) {
        playerPositions.put(name, position);
    }

    int getPlayerPosition(String name) {
        return playerPositions.get(name);
    }

}
