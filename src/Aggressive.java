import java.util.ArrayList;

public class Aggressive extends Brain {
    /*
    *
    * General Plan:
    * Beelines it for the end of the map until dangerously low on a resource. Ignores gold and traders.
    *
    * Asks vision for farthest tile toward end goal it can see.
    *
    * When low on food or water, will ask for the needed resource and move to that tile if in vision.
    * If unable to see the needed resource move to the farthest tile toward end goal it can see, try again there.
    *
    * When strength is under 30% full, will rest until strength is back to full, or until food or water is under 30% full.
    *     If was able to rest continue original plan
    *     If strength is under 30% full and food or water is under 30% full, do not rest until the food and water are at least 30% full.
    * 
    * When on a repeatable resource tile, stay until resource and strength are 50% full or other resource is below 30% full.
    * 
    * Trader behavior: none, if at a trader by chance will not trade.
    * 
    */

    //This method asks for the path that gets farthest toward the end.
    private ArrayList<Terrain> distPriorityPath(Player player) {
        Vision vision = player.getVision();
        //ask vision for farthest path, return it.
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();
        returnedPath = vision.farthestPathToEnd(player);
        return returnedPath;
    }

    //This method prioritizes water, but if none is visible will go for food, then if neither
    //are visible it will go for the farthest path toward the end.
    private ArrayList<Terrain> waterPriorityPath(Player player) {
        Vision vision = player.getVision();
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();
        //ask vision if water is visible
        if(vision.isWaterVisible(player)) {
            //ask vision for 2 closest water paths
            ArrayList<Terrain>[] paths = vision.findWaterPaths(player);

            //compare the paths and choose the one that ends farthest east.
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else if(vision.isFoodVisible(player)) {
            //ask vision for 2 closest food paths
            ArrayList<Terrain>[] paths = vision.findFoodPaths(player);

            //compare the paths and choose the one that ends farthest east.
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else {
            //ask vision for path to farthest tile
            returnedPath = distPriorityPath(player);
        }
        return returnedPath;
    }

    //This method prioritizes food, but if none is visible will go for water, then if neither
    //are visible it will go for the farthest path toward the end.
    private ArrayList<Terrain> foodPriorityPath(Player player) {
        Vision vision = player.getVision();
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();
        //ask vision if food is visible
        if(vision.isFoodVisible(player)) {
            //ask vision for 2 closest food paths
            ArrayList<Terrain>[] paths = vision.findFoodPaths(player);

            //compare the paths and choose the one that ends farthest east.
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else if(vision.isWaterVisible(player)) {
            //ask vision for 2 closest water paths
            ArrayList<Terrain>[] paths = vision.findWaterPaths(player);

            //compare the paths and choose the one that ends farthest east.
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else {
            //ask vision for path to farthest tile
            returnedPath = distPriorityPath(player);
        }
        return returnedPath;
    }

    public void makeMove(Player player) {
        ArrayList<Terrain> pathToFollow = new ArrayList<Terrain>();

        //Logic for low stamina.
        if(player.getStamina() < 30) {
            //if player is low on water or food
            if(player.getWater() < 30 || player.getFood() < 30) {
                //if water is less than or equal to food, prioritize water.
                if(player.getWater() <= player.getFood()) {

                    pathToFollow = waterPriorityPath(player);
                }
                //otherwise prioritize food.
                else {
                    pathToFollow = foodPriorityPath(player);
                }
            }
        }
        //If the player is low on stamina but not on resources it
        //will have pathToFollow as an empty path and means the player will rest.

        
        //logic for normal stamina.
        else
        {
            //if player is low on water or food
            if(player.getWater() < 30 || player.getFood() < 30) {
                //if water is less than or equal to food, prioritize water.
                if(player.getWater() <= player.getFood()) {
                    pathToFollow = waterPriorityPath(player);
                }
                //otherwise prioritize food.
                else {
                    pathToFollow = foodPriorityPath(player);
                }
            }
            else {
                //if player is not low on resources it will go for the farthest path toward the end.
                pathToFollow = distPriorityPath(player);
            }
        }

        if(pathToFollow.size() > 0) { //if there is a path to follow, it is the best option so move there.
            player.moveAlongPath(pathToFollow);
        }
        else { //if there is no path to follow, the best option is to rest.
            player.rest();
        }
    }
}
