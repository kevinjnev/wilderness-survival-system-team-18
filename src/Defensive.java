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
    * When there is water or food below 70%, ask vision for that lowest resource. If it is in vision move there, ignore traders.
    * If it is not in vision, look for the other resource and go for it if in vision, ignore traders.
    * 
    * If neither food or water is in vision, ask for gold in vision. If a trader is in sight, go to trader if above 0 gold and
    * attempt to trade for lowest resource until gold is zero, trader refuses to trade, or resources are full.
    * 
    * If strength is less than half, rest until strength is full or food or water is < 30% full.
    * When strength is less than half and food or water is below 30% do not rest until that resource is above 30% full.
    * 
    * When on a repeatable resource tile, stay until resource is full or other resource is below 50% full.
    * 
    * Trader behavior:
    * 
    */
    public void makeMove() {
        
    }
}
