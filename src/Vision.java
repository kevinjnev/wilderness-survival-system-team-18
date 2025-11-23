import java.util.ArrayList;
public class Vision {
    


    public ArrayList<Terrain> farthestPathToEnd(Player player) {
        return null;
    }


    //As the brain classes are now, they expect these to return an arraylist of the easiest path for the two
    //closest tiles of that resource, or if there is none of that resource in vision it would be an empty list for that path.
    //That way they can compare the paths and choose the one that ends farthest east.
    
    //Just keep that in mind and if you want to change how it works let me know in the group chat.
    public ArrayList<Terrain>[] findTraderPaths(Player player) {
        return null;
    }

    public ArrayList<Terrain>[] findGoldPaths(Player player) {
        return null;
    }

    public ArrayList<Terrain>[] findWaterPaths(Player player) {
        return null;
    }

    public ArrayList<Terrain>[] findFoodPaths(Player player) {
        return null;
    }
    public boolean isTraderVisible(Player player) {
        return false;
    }
    public boolean isGoldVisible(Player player) {
        return false;
    }
    public boolean isWaterVisible(Player player) {
        return false;
    }
    public boolean isFoodVisible(Player player) {
        return false;
    }
    public int getDistToEnd(Terrain tile) {
        return 0;
    }
}
