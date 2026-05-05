package PA1;

import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

public class Blackjack {
    public static void main(String[] args) {
        int seed = Integer.parseInt(args[0]);
        int numPlayers = Integer.parseInt(args[1]);

        Deck deck = new Deck();
        deck.shuffle(seed);

        Player player1 = new Player(deck);
        ArrayList<Computer> computers = new ArrayList<>();
        for (int i = 2; i <= numPlayers; i++) {
            computers.add(new Computer(deck));
        }
        House house = new House(deck);
        
        //
        player1.addCard(deck);
        for (int i = 0; i <= numPlayers-2; i++) {
            (computers.get(i)).addCard(deck);
        }
        house.addCard(deck);
        player1.addCard(deck);
        for (int i = 0; i <= numPlayers-2; i++) {
            (computers.get(i)).addCard(deck);
        }
        house.addCard(deck);
        //

        System.out.println("House: HIDDEN, " + house.reveal().get(1));
        System.out.println("Player1: " + player1.handString() + " (" + player1.getValue() + ")");
        for (int i = 0; i < computers.size(); i++) {
            System.out.println("Player" + (i+2) + ": " + computers.get(i).handString() + " (" + computers.get(i).getValue() + ")");
        }

        if (house.getValue() == 21) {
            System.out.println("\n--- Game Results ---");
            System.out.println("House: " + house.handString() + " (21)");
            System.out.println("[Lose] Player1: " + player1.handString() + " (" + player1.getValue() + ")");
            for (int i = 0; i < computers.size(); i++) {
                Computer c = computers.get(i);
                System.out.println("[Lose] Player" + (i+2) + ": " + c.handString() + " (" + c.getValue() + ")");
            }
            return;
        }

        System.out.println("\n--- Player1 turn ---");
        player1.playTurn(deck);

        for (int i = 0; i < computers.size(); i++) {
            System.out.println("--- Player" + (i+2) + " turn ---");
            computers.get(i).playTurn(deck, i+2);
        }

        System.out.println("--- House turn ---");
        house.playTurn(deck);

        System.out.println();
        System.out.println("--- Game Results ---");
        if (house.isBust()) {
        	System.out.println("House: " + house.handString() + " (" + house.getValue() + ") - Bust!");
        }
        else {
        	System.out.println("House: " + house.handString() + " (" + house.getValue() + ")");
        }
        

        int houseVal = house.getValue();

        int p1Val = player1.getValue();
        if (player1.isBust()) System.out.println("[Lose] Player1: " + player1.handString() + " (" + p1Val + ") - Bust!");
        else if (house.isBust() || p1Val > houseVal) System.out.println("[Win] Player1: " + player1.handString() + " (" + p1Val + ")");
        else if (p1Val < houseVal) System.out.println("[Lose] Player1: " + player1.handString() + " (" + p1Val + ")");
        else System.out.println("[Draw] Player1: " + player1.handString() + " (" + p1Val + ")");

        for (int i = 0; i < computers.size(); i++) {
            Computer c = computers.get(i);
            int val = c.getValue();

            if (c.isBust()) System.out.println("[Lose] Player" + (i+2) + ": " + c.handString() + " (" + val + ") - Bust!");
            else if (house.isBust() || val > houseVal) System.out.println("[Win] Player" + (i+2) + ": " + c.handString() + " (" + val + ")");
            else if (val < houseVal) System.out.println("[Lose] Player" + (i+2) + ": " + c.handString() + " (" + val + ")");
            else System.out.println("[Draw] Player" + (i+2) + ": " + c.handString() + " (" + val + ")");
        }
    }
}

class Card {
    private int value;
    private int suit;

    public Card() {}

    public Card(int theValue, int theSuit) {
        value = theValue;
        suit = theSuit;
    }

    public int getValue() {
        if (value >= 2 && value <= 10) return value;
        else if (value >= 11 && value <= 13) return 10;
        else return 1;
    }

    public String toString() {
        String val;
        if (value == 1) val = "A";
        else if (value == 11) val = "J";
        else if (value == 12) val = "Q";
        else if (value == 13) val = "K";
        else val = String.valueOf(value);

        String suitStr = "";
        if (suit == 0) suitStr = "c";
        else if (suit == 1) suitStr = "h";
        else if (suit == 2) suitStr = "d";
        else suitStr = "s";

        return val + suitStr;
    }
}

class Deck {
    private Card[] deck;
    private int cardsUsed;

    public Deck() {
        deck = new Card[52];
        int index = 0;

        for (int value = 1; value <= 13; value++) {
        	for (int suit = 0; suit < 4; suit++) {
                deck[index++] = new Card(value, suit);
            }
        }

        cardsUsed = 0;
    }

    public void shuffle(int seed) {
        Random random = new Random(seed);
        for (int i = deck.length - 1; i > 0; i--) {
            int rand = random.nextInt(i + 1);
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsUsed = 0;
    }

    public Card dealCard() {
        if (cardsUsed == deck.length)
            throw new IllegalStateException("No cards are left in the deck.");
        cardsUsed++;
        return deck[cardsUsed - 1];
    }
}

class Hand {
    private ArrayList<Card> hand;

    public Hand(Deck deck){
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }
    public void addCard(Deck deck) {
    	hand.add(deck.dealCard());
    }

    public ArrayList<Card> reveal(){
        return hand;
    }

    public String handString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i));
            if (i != hand.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public int getValue() {
        int total = 0;
        int aces = 0;

        for (Card card : hand) {
            int value = card.getValue();
            if (value == 1) aces++;
            total += value;
        }

        while (aces > 0 && total + 10 <= 21) {
            total += 10;
            aces--;
        }

        return total;
    }

    public boolean isBust() {
        return getValue() > 21;
    }
}

class Computer extends Hand {
    public Computer(Deck deck) {
        super(deck);
    }

    public void playTurn(Deck deck, int idx) {
        Random random = new Random();

        while (true) {
            if (isBust()) {
                System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ") - Bust!");
                break;
            }

            int sum = getValue();

            if (sum < 14) {
            	System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                addCard(deck.dealCard());
                System.out.println("Hit");
            } else if (sum > 17) {
            	System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                System.out.println("Stand");
                System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                break;
            } else {
                int is_hit = random.nextInt(2);
                if (is_hit == 1) {
                	System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                    addCard(deck.dealCard());
                    System.out.println("Hit");
                } else {
                	System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                    System.out.println("Stand");
                    System.out.println("Player" + idx + ": " + handString() + " (" + getValue() + ")");
                    break;
                }
            }
        }
        System.out.println();
    }
}

class Player extends Hand {
    public Player(Deck deck) {
        super(deck);
    }

    public void playTurn(Deck deck) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            if (isBust()) {
                System.out.println("Player1: " + handString() + " (" + getValue() + ") - Bust!");
                break;
            }

            System.out.println("Player1: " + handString() + " (" + getValue() + ")");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("Hit")) {
                addCard(deck.dealCard());
            } else {
                System.out.println("Player1: " + handString() + " (" + getValue() + ")");
                break;
            }
        }
        System.out.println();
    }
}

class House extends Hand {
    public House(Deck deck) {
        super(deck);
    }

    public void playTurn(Deck deck) {

        while (getValue() <= 16) {
        	System.out.println("House: " + handString() + " (" + getValue() + ")");
            addCard(deck.dealCard());
            System.out.println("Hit");
        }

        if (!isBust()) {
        	System.out.println("House: " + handString() + " (" + getValue() + ")");
            System.out.println("Stand");
            System.out.println("House: " + handString() + " (" + getValue() + ")");
        } else {
            System.out.println("House: " + handString() + " (" + getValue() + ") - Bust!");
        }
    }
}