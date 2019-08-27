package gamedata.monopoly;

import java.util.Arrays;

public class MonopolyBoard {
    private static final int LAND_MAX = 41;
    private String[] landOwner = new String[LAND_MAX]; // 未所有: null
    private boolean[] landMortgage = new boolean[LAND_MAX];
    private int[] landBuilding = new int[LAND_MAX];

    enum LandType{
        GO,
        JAIL,
        JAIL_VISIT,
        GO_JAIL,
        PARKING,
        PROPERTY,
        RAILROAD,
        COMPANY,
        CHANCE_CARD,
        COMMUNITY_CARD,
        LUXURY_TAX,
        INCOME_TAX
    };

    MonopolyBoard() {
        Arrays.fill(landOwner, null);
        Arrays.fill(landMortgage, false);
        Arrays.fill(landBuilding, 0);
    }

    LandType getType(int land) {
        if(land == 0)
            return LandType.GO;
        if(land == 40)
            return LandType.JAIL;

        
        return LandType.PROPERTY;
    }

    int getPrice(int land) {

    }

    String getOwner(int land) {
        return landOwner[land];
    }

    int getRent(int land) {
        LandType type = getType(land);
        
    }

    boolean isMortgage(int land) {
        return landMortgage[land];
    }

    int getBuildCost(int land) {

    }

}
