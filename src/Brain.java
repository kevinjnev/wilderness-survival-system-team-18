import java.util.ArrayList;
public abstract class Brain {

    //This asks the vision for the distance to the end from the given tile.
    private int distToEnd(ArrayList<Terrain> path, Vision vision) {
        Terrain finalTile = path.get(path.size() - 1);
        int dist = vision.getDistToEnd(finalTile);

        return dist;
    }

    //This method compares the two paths given and returns the path that ends farthest east/toward the end.
    public ArrayList<Terrain> comparePaths(ArrayList<Terrain> path1, ArrayList<Terrain> path2, Vision vision) {
        ArrayList<Terrain> returnedPath = new ArrayList<Terrain>();
        
        //if the lengths of the paths are 0 return an empty arraylist of terrains.
        if(path1.size() == 0 && path2.size() == 0) { }

        //if length of one path is 0, choose the other path.
        else if(path1.size() == 0) {
            returnedPath = path2;
        }
        else if(path2.size() == 0) {
            returnedPath = path1;
        }

        //else compare how far east the last tile of each path is and choose the path that ends farther east.
        else {
            if(distToEnd(path1, vision) < distToEnd(path2, vision)) {
                returnedPath = path1;
            }
            else {
                returnedPath = path2;
            }
        }
        return returnedPath;
    }

    public abstract void makeMove(Player player);

}
