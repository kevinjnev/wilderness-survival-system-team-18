import java.util.ArrayList;

public class Player 
{

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

    //Constructor
    public Player()
    {
        this.name = "";
        this.strategy = "";
        this.visionType = "";
        this.stamina = 100.0;
        this.water = 100.0;
        this.food = 100.0;
        this.gold = 0;
        //TODO: change this to being a random location on the left side of the map.
        this.location = new int[] {0,0};
    }
   
    
    public void adjustResources(double dStamina, double dWater, double dFood) {
        this.stamina = Math.max(0, stamina + dStamina);
        this.water   = Math.max(0, water   + dWater);
        this.food    = Math.max(0, food    + dFood);
    }

    //setters and getters
    public void setName(String name)
    {
         this.name = name;
    }
       
    public void setStrategy (String strategyEntered)
    {
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

    public void setVision(String visionEntered)
    {
        this.visionType = visionEntered;

        //TODO: Implement the vision subclasses and uncomment this.
        /* 
        switch (visionType.toLowerCase()) {
            case "focused":
                this.vision = new Focused();
                break;
            case "cautious":
                this.vision = new Cautious();
                break;
            case "keeneyed":
                this.vision = new Keeneyed();
                break;
            case "farsight":
                this.vision = new Farsight();
                break;
        }
        */

    }
    
    public String getName()
    {
        return name;
    }
    
    public String getStrategy()
    {
        return strategy;
    }
    
    public String getVisionType()
    {
        return visionType;
    }

    public double getStamina()
    {
        return stamina;
    }

     public double getWater()
    {
        return water;
    }

     public double getFood()
    {
        return food;
    }

    public int getGold()
    {
        return gold;
    }

    public int[] getLocation()
    {
        return location;
    }

    public Vision getVision()
    {
        return vision;
    }

    public Brain getBrain()
    {
        return brain;
    }

    /* Unsure if we want to allow the player to set
     * Stamina, water, food, and gold. For now I have it set to no.
     * - Samuel
     */

     // Displays the player ID.
    void showPlayerInfo( )
    {
        System.out.println("Player Name: " + name);
        System.out.println("Player Strategy: " + strategy);
        System.out.println("Player Stamina: " + stamina);
        System.out.println("Player Water: " + water);
        System.out.println("Player Food: " + food);
        System.out.println("Player Gold: " + gold );
        System.out.println("Starting Location: " + location[0] + ", " + location[1]);

    }  

    //TODO: Make the movement and rest methods.

    //moveAlongPath takes in an arraylist of the terrain tiles that are traversed.
    //For each of the tiles we need to get the costs from the terrain and change the player's resources.
    //Need to also keep track of the player's location.
    //Maybe also check if the resources drop to 0 and end the game if they do, unless we do that somewhere else.
    public void moveAlongPath(ArrayList<Terrain> path) {

    }

    public void rest() {


    }
    


}
