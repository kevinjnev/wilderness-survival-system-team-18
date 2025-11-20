public class Aggressive extends Brain {
    /*
    * General Plan:
    * Beelines it for the end of the map until dangerously low on a resource, only deviating
    * if gold is in vision or if resource is low.
    *
    * If gold is in vision and the player moves forward at least 1 tile to get it, will go for it.
    *
    * When low on food or water, will go for the needed resource then return to original plan.
    * The needed resource takes priority over any gold in vision.
    * If unable to see the needed resource look for trader to buy from.
    *   Try to buy resource from trader. If not possible or fails look again for priority resource tile.
    * If there is no trader, move as far forward as visible and look again for the resource.
    * 
    * When strength is the priority resource, will rest and not move until strength is back to full,
    * or until food or water is low.
    * If was able to rest continue original plan.
    * 
    * Trader behavior:
    * 
    */
    public void makeMove() {
        
    }
}
