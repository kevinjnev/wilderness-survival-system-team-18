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
            //TODO: Get vision type from user.
        
            aPlayer.showPlayerInfo();

            System.out.println("Is this Information correct? (Yes or No)?: ");
            userResponse = userInput.nextLine();
            if (userResponse.equalsIgnoreCase("Yes"))
            {
                check = true;
            }
            else
            {
                check = false;
            }

        } while (!check);

        //assign Player private variables to local Main variables
        String name = aPlayer.getName();
        String strategy = aPlayer.getStrategy();
        double stamina = aPlayer.getStamina();
        double water = aPlayer.getWater();
        double food = aPlayer.getFood();
        int gold = aPlayer.getGold();

        //temporary check to make sure strategy has been changed.
        System.out.println(aPlayer.getStrategy());
        
        //close Scanner.
        userInput.close();

        //TODO: MAP GENERATION
    }
}
