import java.util.ArrayList;

public class Defensive extends Brain {
    /*
    *
    * General Plan:
    *
    * If water or food is above 70%, ask vision for gold. If no gold in sight then ask for water or food, whichever is lower. If the same choose water.
    *
    * If a trader is in vision and the player has more than zero gold and more than 70% water and food, goes to trader
    * and attempts to trade. Trades until gold is zero, trader refuses, or resources are full or too expensive.
    * 
    * When there is water or food below 70%, ask vision for that lowest resource. If it is in vision move there.
    * If it is not in vision, look for the other resource and go for it if in vision.
    * If neither food or water is in vision, ask for gold in vision. If a trader is in sight, go to trader if above 0 gold and
    * attempt to trade for lowest resource until gold is zero, trader refuses to trade, or resources are full.
    * 
    * If strength is less than half, rest until strength is full or food or water is < 30% full.
    * When strength is less than half and food or water is below 30% do not rest until that resource is above 30% full.
    * 
    * When on a repeatable resource tile, stay until resource is full or other resource is below 30% full.
    * 
    * Trader behavior:
    * 
    */

    private ArrayList<Terrain> distPriorityPath(Player player) {
        Vision vision = player.getVision();
        //ask vision for farthest path, return it.
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();
        returnedPath = vision.farthestPathToEnd(player);
        return returnedPath;
    }

    private ArrayList<Terrain> traderPriorityPath(Player player) {
        Vision vision = player.getVision();
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();

        //ask vision if trader is visible
        if(vision.isTraderVisible(player) && player.getGold() > 0) {
            //ask vision for the trader paths and choose the closest one.
            ArrayList<Terrain>[] paths = vision.findTraderPaths(player);
            returnedPath = paths[0];
        }
        else if(vision.isGoldVisible(player)) {
            ArrayList<Terrain>[] paths = vision.findGoldPaths(player);
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else if(player.getWater() <= player.getFood() && vision.isWaterVisible(player)) {
            ArrayList<Terrain>[] paths = vision.findWaterPaths(player);
            returnedPath = comparePaths(paths[0], paths[1], vision);
            
        }
        else if(player.getWater() > player.getFood() && vision.isFoodVisible(player)) {
            ArrayList<Terrain>[] paths = vision.findFoodPaths(player);
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else {
            returnedPath = distPriorityPath(player);
        }
        return returnedPath;
    }


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
        else if(vision.isTraderVisible(player) && player.getGold() > 0) {
            ArrayList<Terrain>[] paths = vision.findTraderPaths(player);

            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else if(vision.isGoldVisible(player)) {
            ArrayList<Terrain>[] paths = vision.findGoldPaths(player);
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else {
            returnedPath = distPriorityPath(player);
        }
        return returnedPath;
    }

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
        else if(vision.isTraderVisible(player) && player.getGold() > 0) {
            ArrayList<Terrain>[] paths = vision.findTraderPaths(player);

            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else if(vision.isGoldVisible(player)) {
            ArrayList<Terrain>[] paths = vision.findGoldPaths(player);
            returnedPath = comparePaths(paths[0], paths[1], vision);
        }
        else {
            returnedPath = distPriorityPath(player);
        }
        return returnedPath;
    }



    public void makeMove(Player player) {
        ArrayList<Terrain> pathToFollow = new ArrayList<Terrain>();

        //Logic for low stamina.
        if(player.getStamina() < 50) {
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
        //logic for normal stamina.
        else {
            if(player.getWater() < 70 || player.getFood() < 70) {
                if(player.getWater() <= player.getFood()) {
                    pathToFollow = waterPriorityPath(player);
                }
                else {
                    pathToFollow = foodPriorityPath(player);
                }
            }
            else {
                //if player has more than 0 gold and a trader is in vision go there
                //else ask for gold, if no gold ask for water or food whichever is lower,
                //if none of the lower resource go for the other, if neither are visible go
                //for the farthest path toward the end.
                pathToFollow = traderPriorityPath(player);
            }
            
        }

        if(pathToFollow.size() > 0) { //if there is a path to follow, it is the best option so move there.
            //move to the path.
            player.moveAlongPath(pathToFollow);
        }
        else { //if there is no path to follow, the best option is to rest.
            player.rest();
        }
    }
}
