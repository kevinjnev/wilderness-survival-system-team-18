import java.util.Random;
import java.util.Scanner;

public class TradingMethods {
    // Randomly create one of the 3 trader types
    public static Trader createRandomTrader() {
        Random rand = new Random();
        int type = rand.nextInt(3);
        switch (type) {
            case 0:
                System.out.println("You found a friendly trader. (Fair prices, patient)");
                return new FriendlyTrader();
            case 1:
                System.out.println("You found a hostile trader. (High prices, impatient)");
                return new HostileTrader();
            default:
                System.out.println("You found a neutral trader. (Normal prices)");
                return new NeutralTrader();
        }
    }
    
    // Handle trader interaction for manual mode
    public static void manualTrade(Player player, Scanner userInput) {
        Trader trader = createRandomTrader();
        boolean trading = true;

        System.out.println("You have " + player.getGold() + " gold, " + player.getWater() + " water, " + player.getFood() + " food.");
        System.out.println("\nWould you like to trade? (yes/no)");
        
        String userWantsToTrade = userInput.nextLine().trim();
        while (!userWantsToTrade.equalsIgnoreCase("Yes") && !userWantsToTrade.equalsIgnoreCase("No")) {
            System.out.println("Invalid input. Please enter 'Yes' or 'No'.");
            userWantsToTrade = userInput.nextLine().trim();
        }
        if (!userWantsToTrade.equalsIgnoreCase("Yes")) {
            System.out.println("You walk away from the trader.");
            trading = false;
        }
        
        
        while (trading && trader.supplyStatus()) {
            System.out.println("\n" + trader.supplyStatusMessage());
            System.out.println("Enter trade offer as: <gold> <water_wanted> <food_wanted>");
            System.out.println("Example: '5 10 0' means offer 5 gold for 10 water and 0 food");
            System.out.println("Or enter 'done' to stop trading.");
            
            String input = userInput.nextLine().trim();
            if (input.equalsIgnoreCase("done")) {
                trading = false;
                continue;
            }
            
            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                System.out.println("Invalid format. Try again.");
                continue;
            }
            
            try {
                int goldOffer = Integer.parseInt(parts[0]);
                double waterWanted = Double.parseDouble(parts[1]);
                double foodWanted = Double.parseDouble(parts[2]);
                
                if (goldOffer > player.getGold()) {
                    System.out.println("You don't have enough gold!");
                    continue;
                }
                
                Trader.TradeOffer offer = new Trader.TradeOffer(goldOffer, waterWanted, foodWanted);
                Trader.TradeResponse traderResponse = trader.analyzeOffer(offer);
                
                switch (traderResponse.action) {
                    case ACCEPT:
                        if (trader.executeTrade(player, offer)) {
                            System.out.println("Trade successful! +" + waterWanted + " water, +" + foodWanted + " food");
                        }
                        break;
                    case COUNTER:
                        System.out.println("Trader counters: wants " + traderResponse.counterOffer.goldOffered + " gold instead.");
                        System.out.println("Accept counter? (yes/no)");
                        String accept = userInput.nextLine().trim();
                        if (accept.equalsIgnoreCase("yes") || accept.equalsIgnoreCase("y")) {
                            if (traderResponse.counterOffer.goldOffered <= player.getGold()) {
                                if (trader.executeTrade(player, traderResponse.counterOffer)) {
                                    System.out.println("Trade successful!");
                                }
                            } else {
                                System.out.println("You don't have enough gold for the counter offer.");
                            }
                        }
                        break;
                    case REJECT:
                        System.out.println("Trader rejects: " + traderResponse.message);
                        if (!trader.supplyStatus()) {
                            System.out.println("The trader leaves!");
                            trading = false;
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid numbers. Try again.");
            }
        }
        System.out.println("Trading complete.");
    }
    
    // This is just a simple auto trade system without any extra logic for different brains.
    public static void autoTrade(Player player) {
        Trader trader = createRandomTrader();
        
        if (player.getGold() >= 3) {
            double waterWanted = 0;
            double foodWanted = 0;
            
            if (player.getWater() <= player.getFood()) {
                waterWanted = 5;
            } else {
                foodWanted = 5;
            }
            
            Trader.TradeOffer offer = new Trader.TradeOffer(3, waterWanted, foodWanted);
            Trader.TradeResponse response = trader.analyzeOffer(offer);
            
            if (response.action == Trader.TradeResponse.Action.ACCEPT) {
                if (trader.executeTrade(player, offer)) {
                    System.out.println("AI traded 3 gold for +" + waterWanted + " water, +" + foodWanted + " food");
                }
            } else if (response.action == Trader.TradeResponse.Action.COUNTER) {
                if (response.counterOffer.goldOffered <= player.getGold()) {
                    if (trader.executeTrade(player, response.counterOffer)) {
                        System.out.println("AI accepted counter offer: " + response.counterOffer.goldOffered + " gold");
                    }
                } else {
                    System.out.println("AI declined - not enough gold for counter offer.");
                }
            } else {
                System.out.println("Trader rejected the offer.");
            }
        } else {
            System.out.println("AI has no gold to trade.");
        }
    }
    
}
