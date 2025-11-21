public class Balanced extends Brain {
    /*
    * General Plan:
    * 
    * If water or food is above 50%, ask vision for gold. If no gold in sight then ask for the farthest tile toward end goal it can see.
    *
    * If a trader is in vision and the player has more than zero gold and more than 50% water and food, goes to trader.
    * and attempts to trade. Trades until gold is zero, trader refuses, or resources are full or too expensive.
    * 
    * When there is water or food below 50%, ask vision for that lowest resource. If it is in vision move there, ignore traders.
    *     If it is not in vision, go to trader and trade for the lowest resource until gold is zero, trader refuses to trade, or resources are full.
    *     If a trader is not in sight, go to the farthest tile toward end goal it can see.
    * 
    * If strength is less than half, rest until strength is full or food or water is < 30% full.
    * When strength is less than half and food or water is below 30% do not rest until that resource is above 30% full.
    * 
    * When on a repeatable resource tile, stay until resource is full or other resource is below 50% full.
    * 
    * 
    * Trader behavior:
    * 
    */
    public void makeMove() {
        
    }
}
