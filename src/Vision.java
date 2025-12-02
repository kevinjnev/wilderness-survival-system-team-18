import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;

public class Vision {
    protected final String type;

    public Vision(String type) {
        this.type = type;
    }

    // Node for A* search that also tracks cumulative resource costs
    private static class Node {
        int x, y;
        Node parent;
        double gMove, gWater, gFood; // cumulative resource costs
        double cost; // combined scalar cost for priority

        Node(int x, int y, Node parent, double gMove, double gWater, double gFood, double cost) {
            this.x = x; this.y = y; this.parent = parent;
            this.gMove = gMove; this.gWater = gWater; this.gFood = gFood; this.cost = cost;
        }
    }

    // A* search from player's location to target tile coordinates.
    // Returns list of Terrain tiles (in order) to traverse (does NOT include starting tile).
    private ArrayList<Terrain> searchPath(Player player, int targetX, int targetY) {
        GameMap map = GameMap.current;
        ArrayList<Terrain> empty = new ArrayList<Terrain>();
        if (map == null) return empty;

        int[] loc = player.getLocation();
        int startX = loc[0];
        int startY = loc[1];

        int w = map.width;
        int h = map.height;

        if (targetX < 0 || targetX >= w || targetY < 0 || targetY >= h) return empty;
        if (startX == targetX && startY == targetY) return empty; // already there

        double maxStamina = player.getStamina();
        double maxWater = player.getWater();
        double maxFood = player.getFood();

        boolean[][] visited = new boolean[h][w];

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
        open.add(new Node(startX, startY, null, 0.0, 0.0, 0.0, heuristic(startX, startY, targetX, targetY)));

        while (!open.isEmpty()) {
            Node cur = open.poll();

            if (visited[cur.y][cur.x]) continue;
            visited[cur.y][cur.x] = true;

            if (cur.x == targetX && cur.y == targetY) {
                // reconstruct path excluding starting tile
                ArrayList<Terrain> path = new ArrayList<>();
                Node n = cur;
                while (n != null && !(n.x == startX && n.y == startY)) {
                    path.add(0, map.terrainGrid[n.y][n.x]);
                    n = n.parent;
                }
                return path;
            }

            // explore neighbors (4-directional)
            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : dirs) {
                int nx = cur.x + d[0];
                int ny = cur.y + d[1];
                if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
                if (visited[ny][nx]) continue;

                Terrain nextTile = map.terrainGrid[ny][nx];
                if (nextTile == null) continue;

                double newMove = cur.gMove + nextTile.getMoveCost();
                double newWater = cur.gWater + nextTile.getWaterCost();
                double newFood = cur.gFood + nextTile.getFoodCost();

                // prune if resource budgets exceeded
                if (newMove > maxStamina || newWater > maxWater || newFood > maxFood) continue;

                // scalar cost: prioritize lower total resource consumption; weight movement a bit higher
                double scalar = newMove * 1.0 + newWater * 0.8 + newFood * 0.8;
                double heur = heuristic(nx, ny, targetX, targetY);
                Node neighbor = new Node(nx, ny, cur, newMove, newWater, newFood, scalar + heur);
                open.add(neighbor);
            }
        }

        return empty; // no path found within resource limits
    }

    private double heuristic(int x, int y, int tx, int ty) {
        // Manhattan distance scaled; prefer tiles closer to target
        return Math.abs(tx - x) + Math.abs(ty - y);
    }

    // Return list of relative offsets (dx,dy) that are visible for this Vision type.
    // Player is assumed at (0,0) column; dx >= 0 (only facing east/right).
    private int[][] visibleOffsets() {
        switch (type.toLowerCase()) {
            case "focused":
                return new int[][] { {1,-1}, {1,0}, {1,1} };
            case "cautious":
                return new int[][] { {0,-1}, {0,1}, {1,0} };
            case "keeneyed":
                return new int[][] { {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}, {2,0} };
            case "farsight":
                return new int[][] {
                    {0,-2}, {0,-1}, {0,1}, {0,2},
                    {1,-2}, {1,-1}, {1,0}, {1,1}, {1,2},
                    {2,-1}, {2,0}, {2,1}
                };
            default:
                return new int[][] { {1,0} };
        }
    }

    // Public accessor so other classes can query which offsets this vision can see.
    public int[][] getVisibleOffsets() {
        return visibleOffsets();
    }

    // Public wrapper for the internal A* search so callers can request a path.
    public ArrayList<Terrain> findPathTo(Player player, int targetX, int targetY) {
        return searchPath(player, targetX, targetY);
    }

    public ArrayList<Terrain> farthestPathToEnd(Player player) {
        GameMap map = GameMap.current;
        ArrayList<Terrain> empty = new ArrayList<Terrain>();
        if (map == null) return empty;

        ArrayList<Terrain> best = empty;
        int bestDist = Integer.MAX_VALUE;

        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                ArrayList<Terrain> path = searchPath(player, x, y);
                if (path.size() == 0) continue;
                Terrain last = path.get(path.size()-1);
                int dist = getDistToEnd(last);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = path;
                }
            }
        }
        return best;
    }

    // helper: find up to two nearest tiles that match the predicate and return their paths
    @SuppressWarnings("unchecked")
    private ArrayList<Terrain>[] findPathsForPredicate(Player player, java.util.function.Predicate<Terrain> matcher) {
        GameMap map = GameMap.current;
        ArrayList<Terrain>[] result = new ArrayList[2];
        result[0] = new ArrayList<>();
        result[1] = new ArrayList<>();
        if (map == null) return result;

        int[] loc = player.getLocation();
        int sx = loc[0], sy = loc[1];

        int[][] offsets = visibleOffsets();
        // collect visible matching tiles with Manhattan distance
        List<int[]> candidates = new java.util.ArrayList<>();
        for (int[] off : offsets) {
            int tx = sx + off[0];
            int ty = sy + off[1];
            if (tx < 0 || tx >= map.width || ty < 0 || ty >= map.height) continue;
            Terrain t = map.terrainGrid[ty][tx];
            if (t != null && matcher.test(t)) {
                candidates.add(new int[] { tx, ty, Math.abs(tx - sx) + Math.abs(ty - sy) });
            }
        }

        // sort by distance
        candidates.sort((a,b) -> Integer.compare(a[2], b[2]));

        for (int i = 0; i < Math.min(2, candidates.size()); i++) {
            int tx = candidates.get(i)[0];
            int ty = candidates.get(i)[1];
            ArrayList<Terrain> path = searchPath(player, tx, ty);
            if (i == 0) result[0] = path;
            else result[1] = path;
        }

        return result;
    }

    public ArrayList<Terrain>[] findTraderPaths(Player player) {
        // no trader placement in map yet; return empty paths
        @SuppressWarnings("unchecked")
        ArrayList<Terrain>[] r = new ArrayList[2];
        r[0] = new ArrayList<>(); r[1] = new ArrayList<>();
        return r;
    }

    public ArrayList<Terrain>[] findGoldPaths(Player player) {
        // gold placement not represented by terrain; return empty for now
        @SuppressWarnings("unchecked")
        ArrayList<Terrain>[] r = new ArrayList[2];
        r[0] = new ArrayList<>(); r[1] = new ArrayList<>();
        return r;
    }

    public ArrayList<Terrain>[] findWaterPaths(Player player) {
        // water = River terrain
        return findPathsForPredicate(player, t -> t.getName().equalsIgnoreCase("River"));
    }

    public ArrayList<Terrain>[] findFoodPaths(Player player) {
        // food = Forest terrain
        return findPathsForPredicate(player, t -> t.getName().equalsIgnoreCase("Forest"));
    }

    public boolean isTraderVisible(Player player) {
        return false; // no traders placed on map currently
    }
    public boolean isGoldVisible(Player player) {
        return false; // no gold placement logic currently
    }
    public boolean isWaterVisible(Player player) {
        ArrayList<Terrain>[] p = findWaterPaths(player);
        return (p[0].size() > 0 || p[1].size() > 0);
    }
    public boolean isFoodVisible(Player player) {
        ArrayList<Terrain>[] p = findFoodPaths(player);
        return (p[0].size() > 0 || p[1].size() > 0);
    }

    public int getDistToEnd(Terrain tile) {
        GameMap map = GameMap.current;
        if (map == null || tile == null) return Integer.MAX_VALUE;
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                if (map.terrainGrid[y][x] == tile) {
                    return (map.width - 1 - x);
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
