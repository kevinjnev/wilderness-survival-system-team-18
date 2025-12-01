import java.util.ArrayList;
import java.util.Scanner;

import javax.management.RuntimeErrorException;

public class Player {

    private String name;
    private String strategy;
    private String visionType;
    private double stamina;
    private double water;
    private double food;
    private int gold;
    private int[] location;
    private Brain brain;
    private Vision vision;
    private boolean alive; // added to help moveAlongPath work.
    private Terrain currentTerrain; // added to help rest method work

    // Constructor
    public Player() {
        this.name = "";
        this.strategy = "";
        this.visionType = "";
        this.stamina = 100.0;
        this.water = 100.0;
        this.food = 100.0;
        this.gold = 0;
        this.alive = true; // added this to allow handleGameover to work.
        // TODO: change this to being a random location on the left side of the map.
        this.location = new int[] { 0, 0 };
    }

    // modified this to subtract values, we are only passing positive values in.
    // we could
    public void adjustResources(double dStamina, double dWater, double dFood) {
        this.stamina = Math.max(0, stamina - dStamina);
        this.water = Math.max(0, water - dWater);
        this.food = Math.max(0, food - dFood);
    }

    // allows player to add resources to their status
    public void addResources(double dStamina, double dWater, double dFood) {
        this.stamina = stamina + dStamina;
        this.water = water + dWater;
        this.food = food + dFood;
    }

    /**
     * Attempt to spend gold. Returns true if player had enough gold and it was
     * deducted.
     */
    public boolean spendGold(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("amount must be >= 0");
        if (this.gold < amount)
            return false;
        this.gold -= amount;
        return true;
    }

    /**
     * Add gold (positive) or remove gold (negative, floored at 0).
     */
    public void addGold(int amount) {
        this.gold = Math.max(0, this.gold + amount);
    }

    // setters and getters
    public void setName(String name) {
        this.name = name;
    }

    public void setStrategy(String strategyEntered) {
        this.strategy = strategyEntered;
        // Create and store the appropriate Brain subclass based on strategy
        switch (strategy.toLowerCase()) {
            case "aggressive":
                this.brain = new Aggressive();
                break;
            case "balanced":
                this.brain = new Balanced();
                break;
            case "defensive":
                this.brain = new Defensive();
                break;
        }
    }

    /*
     * User will be prompted to select a vision type based on the strategy they
     * selected
     */
    public void setVision(Scanner userInput, String strategy) {
        boolean notSelected = true;
        String visionSelection;

        while (notSelected) {
            switch (strategy.toLowerCase()) {
                case "aggressive":
                    System.out.println("Enter your Vision (KeenEyed, FarSight): ");
                    visionSelection = userInput.nextLine();

                    if (visionSelection.equalsIgnoreCase("KeenEyed")) {
                        this.vision = new KeenEyed();
                        notSelected = false;
                    } else if (visionSelection.equalsIgnoreCase("FarSight")) {
                        this.vision = new FarSight();
                        notSelected = false;
                    }

                    break;

                case "balanced":
                    System.out.println("Enter your Vision (Focused, Cautious): ");
                    visionSelection = userInput.nextLine();

                    if (visionSelection.equalsIgnoreCase("Focused")) {
                        this.vision = new Focused();
                        notSelected = false;
                    } else if (visionSelection.equalsIgnoreCase("Cautious")) {
                        this.vision = new Cautious();
                        notSelected = false;
                    }

                    break;

                case "defensive":
                    System.out.println("KeenEyed vision automatically selected for Defensive strategy.");
                    this.vision = new KeenEyed();
                    notSelected = false;

                    break;
            }

            if (notSelected) {
                System.out.println("Invalid Vision type inputted, please re-input: ");
            }
        }
    }

    // added to access Terrain data
    public void setCurrentTerrain(Terrain type) {
        this.currentTerrain = type;
    }

    // added to get terrain data.
    public Terrain getCurrentTerrain() {
        return currentTerrain;
    }

    public String getName() {
        return name;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getVisionType() {
        return visionType;
    }

    public double getStamina() {
        return stamina;
    }

    public double getWater() {
        return water;
    }

    public double getFood() {
        return food;
    }

    public int getGold() {
        return gold;
    }

    public int[] getLocation() {
        return location;
    }

    public Vision getVision() {
        return vision;
    }

    public Brain getBrain() {
        return brain;
    }

    /*
     * Unsure if we want to allow the player to set
     * Stamina, water, food, and gold. For now I have it set to no.
     * - Samuel
     */

    // Displays the player ID.
    void showPlayerInfo() {
        System.out.println("Player Name: " + name);
        System.out.println("Player Strategy: " + strategy);
        System.out.println("Player Stamina: " + stamina);
        System.out.println("Player Water: " + water);
        System.out.println("Player Food: " + food);
        System.out.println("Player Gold: " + gold);
        System.out.println("Starting Location: " + location[0] + ", " + location[1]);

    }

    // TODO: Make the movement and rest methods.

    // moveAlongPath takes in an arraylist of the terrain tiles that are traversed.
    // For each of the tiles we need to get the costs from the terrain and change
    // the player's resources.
    // Need to also keep track of the player's location.
    // Maybe also check if the resources drop to 0 and end the game if they do,
    // unless we do that somewhere else.
    public void moveAlongPath(ArrayList<Terrain> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("No path to travel. ");
            handleGameOver();
            return;
        }

        for (int i = 0; i < path.size(); i++) {
            Terrain tile;
            tile = path.get(i);

            // check if player can afford to enter
            if (!tile.canEnter(this)) {
                System.out.println("Not enough resources to move onto" + tile.getName());
                handleGameOver();
                return;
            }

            // apply the terrain's resource costs
            boolean success = false;
            success = tile.applyTerrainCost(this);

            if (!success) {
                System.out.println("Failed to enter terrain: " + tile.getName());
                handleGameOver();
                return;
            }

            // If location is being tracked by player I need to know.
            // this.currentTerrain = tile;

            if (getStamina() <= 0 || getWater() <= 0 || getFood() <= 0) {
                System.out.println("You have run out or resources on :" + tile.getName());
                handleGameOver();
                return;
            }

            System.out.println("Moved onto " + tile.getName() + "| Stamina: " + getStamina() +
                    ", Water: " + getWater() + ", Food: " + getFood());
        }

        System.out.println("Finished moving along the path. ");
    }

    // helper method that handles game over messages.
    private void handleGameOver() {
        if (this.getStamina() <= 0 || this.getWater() <= 0 || this.getFood() <= 0) {
            this.alive = false;
        }

        if (this.alive == false) {
            System.out.println("Game Over - you have died, lost to the forest");
        } else // for cases that don't involve resource depletion
        {
            System.out.println("Game Over - The Shibboleth got you. (We have zero idea how you managed" +
                    " to pull off dying via breaking the game.) ");
        }
    }

    // handles the calcuations for player stats when resting at a tile.
    public void rest() {
        if (currentTerrain == null) {
            System.out.println("Cannot rest - current terrain is unknown.");
            return;
        }

        // Regain 2 Stamina (movement)
        addResources(2, 0, 0);

        double halfWatercost = 0.0;
        double halfFoodCost = 0.0;

        halfWatercost = currentTerrain.getWaterCost() / 2.0;
        halfFoodCost = currentTerrain.getFoodCost() / 2.0;

        adjustResources(0, halfWatercost, halfFoodCost);

        // if player dies when resting
        if (getStamina() <= 0 || getWater() <= 0 || getFood() <= 0) {
            System.out.println("You have died while resting, at least it was peaceful.");
            handleGameOver();
            return;
        }

        System.out.println("You rest for a turn. ");
        System.out.println("Stamina: " + getStamina() + ", Water: "
                + getWater() + ", Food: " + getFood());
    }

}
