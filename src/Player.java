public class Player 
{

    private String name;
    private String strategy;
    private double stamina;
    private double water;
    private double food;
    private int gold;

    //Constructor
    public Player()
    {
        this.name = " ";
        this.strategy = " ";
        this.stamina = 100.0;
        this.water = 100.0;
        this.food = 100.0;
        this.gold = 0;
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
       
    public void setStrategy (String strategy)
    {
        this.strategy = strategy;
    }
    

    public String getName()
    {
        return name;
    }
    
    public String getStrategy()
    {
        return strategy;
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

    }  

}
