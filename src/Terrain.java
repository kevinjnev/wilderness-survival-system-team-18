public abstract class Terrain {

    protected final double moveCost;
    protected final double waterCost;
    protected final double foodCost;
    protected final String name;

    public Terrain(String name, double moveCost, double waterCost, double foodCost) {
        if (moveCost < 0 || waterCost < 0 || foodCost < 0)
            throw new IllegalArgumentException("Terrain costs must be non-negative");
        this.name = name;
        this.moveCost = moveCost;
        this.waterCost = waterCost;
        this.foodCost = foodCost;
    }

    // Can the player afford to enter?
    public boolean canEnter(Player player) {
        return player.getStamina() >= moveCost
            && player.getWater()   >= waterCost
            && player.getFood()    >= foodCost;
    }

    /**
     * Apply terrain entry costs. Returns true if applied, false if not enough resources.
     */
    public boolean applyTerrainCost(Player player) {
        if (!canEnter(player)) return false;
        player.adjustResources(moveCost, waterCost, foodCost);
        onEntered(player); // hook for special effects
        return true;
    }

    // Optional hook for subclasses (e.g., swamp slow, river drift, etc.)
    protected void onEntered(Player player) {}

    public String getName() { return name; }
    public double getMoveCost() { return moveCost; }
    public double getWaterCost() { return waterCost; }
    public double getFoodCost() { return foodCost; }
}
