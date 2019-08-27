package gamedata.monopoly;

import java.util.Arrays;

public class MonopolyBoard {
    private static final int LAND_MAX = 41; // 0: GO, ... , 40: JAIL
    private String[] landOwner = new String[LAND_MAX]; // 未所有: null
    private boolean[] landMortgage = new boolean[LAND_MAX];
    private int[] landBuilding = new int[LAND_MAX];

    /**
     * PROPERTY: 土地, COMPANY: 公共会社, INCOME_TAX: $200, LUXURY_TAX: $100
     */
    enum LandType {
        GO, JAIL, JAIL_VISIT, GO_JAIL, PARKING, PROPERTY, RAILROAD, COMPANY, CHANCE_CARD, COMMUNITY_CARD, INCOME_TAX,
        LUXURY_TAX
    };

    enum ColorGroup {
        BROWN, LIGHT_BLUE, LIGHT_PURPLE, ORANGE, RED, YELLOW, GREEN, DARK_BLUE
    };

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
        // TODO
        default:
            return LandType.PROPERTY;
        }
    }

    ColorGroup getColor(int land) {
        switch (land) {
        case 1:
        case 3:
            return ColorGroup.BROWN;
        // TODO
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
    }

    int getPrice(int land) {
        // TODO
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
        // TODO
    }

    boolean canBuild(int land) {
        // TODO
    }

    void build(int land) {
        landBuilding[land]++;
    }

    boolean canUnbuild(int land) {
        if (landBuilding[land] <= 4)
            return (landBuilding[land] > 0);
        // ホテル
        // TODO
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

}
