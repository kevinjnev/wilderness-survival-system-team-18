public class Aggressive extends Brain {
    /*
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
    *     If strength is under 30% full and food or water is under 30% full, do not rest until the food and water are at least30% full.
    * 
    * When on a repeatable resource tile, stay until resource and strength are full or other resource is below 30% full.
    * 
    * Trader behavior: none, if at a trader by chance will not trade.
    * 
    */
    public void makeMove() {
        
    }
}
