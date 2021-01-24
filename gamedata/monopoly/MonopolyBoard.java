package gamedata.monopoly;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MonopolyBoard {
    static final int LAND_MAX = 41; // 0: GO, ... , 40: JAIL
    static final int JAIL = 40;// 刑務所の場所
    private static final String[] landName = { "GO", "高知", "共同基金", "香川", "所得税", "JR九州", "鹿児島", "チャンス", "長崎", "福岡",
            "刑務所見学", "山口", "中国電力", "広島", "鳥取", "JR西日本", "兵庫", "チャンス", "大阪", "京都", "駐車場", "愛知", "チャンス", "名古屋", "静岡",
            "JR東海", "神奈川", "TOKYO", "東京都水道局", "さいたま", "刑務所行き", "福島", "宮城", "共同基金", "岩手", "JR東日本", "チャンス", "札幌", "物品税",
            "函館", "刑務所" };
    private static final int[] landPrice = { 0, 60, 0, 60, 0, 200, 100, 0, 100, 120, 0, 140, 150, 140, 160, 200, 180, 0,
            180, 200, 0, 220, 0, 220, 240, 200, 260, 260, 150, 280, 0, 300, 300, 0, 320, 200, 0, 350, 0, 400, 0 };
    private static final int[] landBuildCost = { 0, 50, 0, 50, 0, 0, 50, 0, 50, 50, 0, 100, 0, 100, 100, 0, 100, 0, 100,
            100, 0, 150, 0, 150, 150, 0, 150, 150, 0, 150, 0, 200, 200, 0, 200, 0, 0, 200, 0, 200, 0 };
    private static final int[][] landRent = { { 0, 0, 0, 0, 0, 0 }, { 2, 10, 30, 90, 160, 250 }, { 0, 0, 0, 0, 0, 0 },
            { 4, 20, 60, 180, 320, 450 }, { 0, 0, 0, 0, 0, 0 }, { 0, 25, 50, 100, 200, 0 },
            { 6, 30, 90, 270, 400, 550 }, { 0, 0, 0, 0, 0, 0 }, { 6, 30, 90, 270, 400, 550 },
            { 8, 40, 100, 300, 450, 600 }, { 0, 0, 0, 0, 0, 0 }, { 10, 50, 150, 450, 625, 750 }, { 0, 0, 0, 0, 0, 0 },
            { 10, 50, 150, 450, 625, 750 }, { 12, 60, 180, 500, 700, 900 }, { 0, 25, 50, 100, 200, 0 },
            { 14, 70, 200, 550, 750, 950 }, { 0, 0, 0, 0, 0, 0 }, { 14, 70, 200, 550, 750, 950 },
            { 16, 80, 220, 600, 800, 1000 }, { 0, 0, 0, 0, 0, 0 }, { 18, 90, 250, 700, 875, 1050 },
            { 0, 0, 0, 0, 0, 0 }, { 18, 90, 250, 700, 875, 1050 }, { 20, 100, 300, 750, 925, 1100 },
            { 0, 25, 50, 100, 200, 0 }, { 22, 110, 330, 800, 975, 1150 }, { 22, 110, 330, 800, 975, 1150 },
            { 0, 0, 0, 0, 0, 0 }, { 24, 120, 360, 850, 1025, 1200 }, { 0, 0, 0, 0, 0, 0 },
            { 26, 130, 390, 900, 1100, 1275 }, { 26, 130, 390, 900, 1100, 1275 }, { 0, 0, 0, 0, 0, 0 },
            { 28, 150, 450, 1000, 1200, 1400 }, { 0, 25, 50, 100, 200, 0 }, { 0, 0, 0, 0, 0, 0 },
            { 35, 175, 500, 1100, 1300, 1500 }, { 0, 0, 0, 0, 0, 0 }, { 50, 200, 600, 1400, 1700, 2000 },
            { 0, 0, 0, 0, 0, 0 } };
    private static final int[] railLand = { 5, 15, 25, 35 };
    private String[] landOwner = new String[LAND_MAX]; // 未所有: null
    private boolean[] landMortgage = new boolean[LAND_MAX];
    private int[] landBuilding = new int[LAND_MAX];

    private List<String> playerNames = new ArrayList<>();
    private Map<String, Integer> playerMoneys = new HashMap<>();
    private Map<String, Integer> playerPositions = new HashMap<>();
    private List<String> bankruptPlayers = new ArrayList<>();

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

    int[] getSameColor(ColorGroup color) {
        switch (color) {
        case BROWN:
            return new int[] { 1, 3 };
        case LIGHT_BLUE:
            return new int[] { 6, 8, 9 };
        case LIGHT_PURPLE:
            return new int[] { 11, 13, 14 };
        case ORANGE:
            return new int[] { 16, 18, 19 };
        case RED:
            return new int[] { 21, 23, 24 };
        case YELLOW:
            return new int[] { 26, 27, 29 };
        case GREEN:
            return new int[] { 31, 32, 34 };
        case DARK_BLUE:
            return new int[] { 37, 39 };
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
        int[] lands = getSameColor(color);
        if (getOwner(lands[0]) == null) {
            return false;
        }
        boolean monopoly = true;
        for (int i = 1; i < lands.length; i++) {
            if (!Objects.equals(getOwner(lands[0]), getOwner(lands[i]))) {
                monopoly = false;
            }
        }
        return monopoly;
    }

    String getName(int land) {
        return landName[land];
    }

    int getPrice(int land) {
        return landPrice[land];
    }

    /**
     *
     * @param land
     * @return 所有者がいなければ null
     */
    String getOwner(int land) {
        return landOwner[land];
    }

    void setOwner(int land, String owner) {
        if(owner.equals("null")){
            landOwner[land] = null;
        }
        else{
            landOwner[land] = owner;
            System.out.println("setOwner "+landName[land]+" "+landOwner[land]);
        }
    }

    int getRent(int land) {
        if (getOwner(land) == null || isMortgage(land)) {
            return 0;
        }
        LandType type = getType(land);
        if (type == LandType.PROPERTY) {
            if (isMonopoly(land) && getBuilding(land) == 0) {
                return landRent[land][0] * 2;
            }
            return landRent[land][getBuilding(land)];
        }
        if (type == LandType.RAILROAD) {
            String name = getOwner(land);
            int count = 0;
            for (int i = 0; i < 4; i++) {
                if (name.equals(getOwner(railLand[i]))) {
                    count++;
                }
            }
            return landRent[land][count];
        }
        return 0;
    }

    int getRent(int land, int level) {
        return landRent[land][level];
    }

    boolean isMortgage(int land) {
        return landMortgage[land];
    }

    boolean canMortgage(int land) {
        if (isMortgage(land)) {
            return false;
        }
        switch (getType(land)) {
        case PROPERTY:
            for (int land2 : getSameColor(getColor(land))) {
                if (getBuilding(land2) > 0) {
                    return false;
                }
            }
            return true;
        case RAILROAD:
        case COMPANY:
            return true;
        default:
            return false;
        }
    }

    /**
     * 抵当に入れる。
     *
     * @param land
     */
    void mortgage(int land) {
        landMortgage[land] = true;
    }

    boolean canUnmortgage(int land) {
        return isMortgage(land);
    }

    void unmortgage(int land) {
        landMortgage[land] = false;
    }

    int getBuildCost(int land) {
        return landBuildCost[land];
    }

    boolean canBuild(int land) {
        if (getType(land) == LandType.PROPERTY) {
            if (getBuilding(land) == 5) {
                return false;
            }
            int[] lands = getSameColor(getColor(land));
            for (int i = 0; i < lands.length; i++) {
                if (getBuilding(lands[i]) < getBuilding(land) || isMortgage(lands[i])) {
                    return false;
                }
            }
            return isMonopoly(land) && ((getBuilding(land) < 4 && getRemainingHouse() > 0)
                    || getBuilding(land) == 4 && getRemainingHotel() > 0);
        }
        return false;
    }

    void build(int land) {
        landBuilding[land]++;
    }

    void setBuilding(int land, int level) {
        landBuilding[land] = level;
    }

    int getBuilding(int land) {
        return landBuilding[land];
    }

    /**
     * そのマスの建築が破壊可能かを返す。 ホテル解体後に設置する家が足りない場合はfalseを返す。
     *
     * @param land 解体するマス
     * @return boolean
     */
    boolean canUnbuild(int land) {
        if (getType(land) == LandType.PROPERTY) {
            if(getBuilding(land) <= 0) {
                return false;
            }
            if (landBuilding[land] <= 4) {
                int[] lands = getSameColor(getColor(land));
                for (int i = 0; i < lands.length; i++) {
                    if (getBuilding(lands[i]) > getBuilding(land)) {
                        return false;
                    }
                }
                return (landBuilding[land] > 0);
            }
            // ホテル
            if (landBuilding[land] == 5) {
                if (getRemainingHouse() >= 4) {
                    return true;
                }
            }
        }
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

    List<String> getPlayers() {
        return playerNames;
    }

    void setPlayerMoney(String name, int money) {
        playerMoneys.put(name, money);
    }

    void payPlayerMoney(String name, int money) {
        playerMoneys.put(name, playerMoneys.get(name) - money);
    }

    void addPlayerMoney(String name, int money) {
        playerMoneys.put(name, playerMoneys.get(name) + money);
    }

    int getPlayerMoney(String name) {
        if (playerMoneys.containsKey(name)) {
            return playerMoneys.get(name);
        }
        return 0;
    }

    void setPlayerPosition(String name, int position) {
        playerPositions.put(name, position);
    }

    int getPlayerPosition(String name) {
        if (playerPositions.containsKey(name)) {
            return playerPositions.get(name);
        }
        return 0;
    }

    boolean isPlayerBankrupt(String name) {
        return bankruptPlayers.contains(name);
    }

    void setPlayerBankrupt(String name) {
        bankruptPlayers.add(name);
    }

    //プレイヤーの総資産を返す
    int PlayerTotalassets(String name){
        List<Integer> landOwners = new ArrayList<Integer>();
        int price = getPlayerMoney(name);
        for (int land=0;land<LAND_MAX;land++){
            String owner = getOwner(land);
            if (owner.equals(name)){
                landOwners.add(land);
                int level = getBuilding(land);
                price += (getBuildCost(land) / 2)*level;
                if (!isMortgage(land)){
                    price += (getBuildCost(land) / 2)*level;
                }
            }
        }
        return price;
    }

}
