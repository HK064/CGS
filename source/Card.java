package source;

public class Card{
    private static String[] suitString = {"Spade", "Heart", "Diamond", "Club"};
    private int suit;
    private int number;

    Card(int suit, int number){
        this.suit = suit;
        this.number = number;
    }

    public String getString(){
        if(0 <= suit && suit < 4){
            return suitString[suit] + number;
        } else {
            return "Joker";
        }
    }
}
