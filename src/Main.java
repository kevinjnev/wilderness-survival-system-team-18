import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) 
    {
        //greeting message
        System.out.println("WELCOME TO THE WILDERNESS,YOUR OBJECTIVE? SURVIVE.");

        // allows for user input
        Scanner userInput = new Scanner(System.in);
        Player aPlayer = new Player(); //allows for access to Player class methods
 
        String userResponse = " ";
        boolean check = false;
        
        //logic: While check remains false, allow the user to re-enter their name and strategy,
        //otherwise continue on.
        do
        {
            System.out.println("Enter your name: ");
            aPlayer.setName(userInput.nextLine());

            System.out.println("Enter your Strategy (Aggressive, Balanced, Defensive): ");
            aPlayer.setStrategy(userInput.nextLine());
        
            while (!aPlayer.getStrategy().equalsIgnoreCase("Aggressive") && !aPlayer.getStrategy().equalsIgnoreCase("Balanced") && !aPlayer.getStrategy().equalsIgnoreCase("Defensive"))
            {
                System.out.println("Invalid Strategy inputted, please re-input: ");
                aPlayer.setStrategy(userInput.nextLine());
            }

            aPlayer.setVision(userInput, aPlayer.getStrategy());
        
            aPlayer.showPlayerInfo();

            System.out.println("Is this Information correct? (Yes or No)?: ");
            userResponse = userInput.nextLine();
            while (!(userResponse.equalsIgnoreCase("Yes") || userResponse.equalsIgnoreCase("No"))) {
                System.out.println("Invalid, please enter 'Yes' or 'No'");
                userResponse = userInput.nextLine();
            }
            if (userResponse.equalsIgnoreCase("Yes"))
            {
                check = true;
            }
            else
            {
                check = false;
            }

        } while (!check);
        
        // Ask user for Map Size and Create Map
        GameMap map = GameMap.askForSize(userInput);

        // Difficulty Selection
        map.askForDifficulty(userInput);

        // Generate Terrain Map after Difficulty
        map.seedItemsFromNoise();

        // Place player at left-middle
        int startY = Math.max(0, Math.min(map.height - 1, map.height / 2));
        aPlayer.getLocation()[0] = 0;
        aPlayer.getLocation()[1] = startY;
        aPlayer.setCurrentTerrain(map.getTerrainAt(0, startY));

        // Ask user for game mode (manual or auto-play)
        System.out.println("\nChoose mode for game");
        System.out.println("Enter '1' for Auto, or '2' for Manual:");
        String modeChoice = userInput.nextLine().trim();
        boolean playerChoseAuto = modeChoice.equals("1") || modeChoice.equalsIgnoreCase("auto");

        if (playerChoseAuto) {
            System.out.println("\nChose Auto Mode...\n");
        } else {
            System.out.println("\nChose Manual Mode...\n");
        }

        // Main game loop
        while (aPlayer.getStatus()) {
            System.out.println("\nMap:");
            map.printVisibleMap(aPlayer);
            map.showKey();

            if (playerChoseAuto) {
                // Here is the main game loop if the user chooses auto mode.
                // Really similar to manual mode just brain makes the move instead.
                System.out.println("Brain is making a move...");
                
                // Store position before move for item collection
                //int px = aPlayer.getLocation()[0];
                //int py = aPlayer.getLocation()[1];
                
                // Let the brain decide and execute the move
                aPlayer.getBrain().makeMove(aPlayer);
                
                // After movement, collect items at the new location
                int fx = aPlayer.getLocation()[0];
                int fy = aPlayer.getLocation()[1];
                
                // collect item if present at final tile and print earnings
                String item = map.getItemAt(fx, fy);
                if (item.equals(GameMap.RED)) {
                    aPlayer.addResources(0, 0, 10);
                    System.out.println("You found food: +10 food.");
                    map.clearItemAt(fx, fy);
                } else if (item.equals(GameMap.BLUE)) {
                    aPlayer.addResources(0, 10, 0);
                    System.out.println("You found water: +10 water.");
                    map.clearItemAt(fx, fy);
                } else if (item.equals(GameMap.YELLOW)) {
                    aPlayer.addGold(5);
                    System.out.println("You found gold: +5 gold.");
                    map.clearItemAt(fx, fy);
                }
                
                System.out.println("Status => Stamina: " + aPlayer.getStamina() + ", Water: " + aPlayer.getWater() + ", Food: " + aPlayer.getFood() + ", Gold: " + aPlayer.getGold());
                
                // Wait for user to press Enter before next move
                System.out.println("\n[Press Enter to continue to next move...]");
                userInput.nextLine();
                
            } 
            else {
                // This section is the main game loop just for the manual mode of the game.

                // list visible move options based on vision offsets
                int[][] offsets = aPlayer.getVision().getVisibleOffsets();
                int px = aPlayer.getLocation()[0];
                int py = aPlayer.getLocation()[1];

                ArrayList<int[]> options = new ArrayList<>();
                System.out.println("\nVisible move options (x y):");
                for (int[] off : offsets) {
                    int tx = px + off[0];
                    int ty = py + off[1];
                    if (tx >= 0 && tx < map.width && ty >= 0 && ty < map.height) {
                        options.add(new int[] { tx, ty });
                        String item = map.getItemAt(tx, ty);
                        String desc = "";
                        if (item.equals(GameMap.RED)) desc = "+Food";
                        else if (item.equals(GameMap.BLUE)) desc = "+Water";
                        else if (item.equals(GameMap.YELLOW)) desc = "+Gold";
                        System.out.printf("(%d,%d) %s\n", tx, ty, desc);
                    }
                }

                System.out.println("Enter target coordinates as 'x y', or enter 'r' to rest, or enter 'q' to quit:");
                String line = userInput.nextLine().trim();
                if (line.equalsIgnoreCase("q")) {
                    System.out.println("You quit the game.");
                    break;
                }
                if (line.equalsIgnoreCase("r")) {
                    aPlayer.rest();
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Invalid input. Try again.");
                    continue;
                }
                int tx, ty;
                try {
                    tx = Integer.parseInt(parts[0]);
                    ty = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numbers. Try again.");
                    continue;
                }

                // ensure target is among visible options
                boolean allowed = false;
                for (int[] opt : options) {
                    if (opt[0] == tx && opt[1] == ty) { allowed = true; break; }
                }
                if (!allowed) {
                    System.out.println("Target not visible or out of range.");
                    continue;
                }

                // Use pathfinding to account for intermediate tiles
                ArrayList<Terrain> path = aPlayer.getVision().findPathTo(aPlayer, tx, ty);
                if (path == null || path.isEmpty()) {
                    System.out.println("No viable path to target within resource limits.");
                    continue;
                }
                aPlayer.moveAlongPath(path);

                // After movement, use player's updated location for item collection
                int fx = aPlayer.getLocation()[0];
                int fy = aPlayer.getLocation()[1];
                Terrain dest = map.getTerrainAt(fx, fy);
                aPlayer.setCurrentTerrain(dest);

                // collect item if present at final tile and print earnings
                String item = map.getItemAt(fx, fy);
                if (item.equals(GameMap.RED)) {
                    aPlayer.addResources(0, 0, 10);
                    System.out.println("You found food: +10 food.");
                    map.clearItemAt(fx, fy);
                } else if (item.equals(GameMap.BLUE)) {
                    aPlayer.addResources(0, 10, 0);
                    System.out.println("You found water: +10 water.");
                    map.clearItemAt(fx, fy);
                } else if (item.equals(GameMap.YELLOW)) {
                    aPlayer.addGold(5);
                    System.out.println("You found gold: +5 gold.");
                    map.clearItemAt(fx, fy);
                }

                System.out.println("Status => Stamina: " + aPlayer.getStamina() + ", Water: " + aPlayer.getWater() + ", Food: " + aPlayer.getFood() + ", Gold: " + aPlayer.getGold());
            }

            // victory if right edge
            if (aPlayer.getLocation()[0] >= map.width - 1) {
                System.out.println("You have reached the edge of the map. You survived! Congratulations.");
                break;
            }
            // death check
            if (aPlayer.getStamina() <= 0 || aPlayer.getWater() <= 0 || aPlayer.getFood() <= 0) {
                System.out.println("Game Over - you have died.");
                break;
            }
        }

        //close Scanner.
        userInput.close();
    }
}
