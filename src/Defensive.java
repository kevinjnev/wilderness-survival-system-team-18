public class Defensive extends Brain {
    /*
    *
    * General Plan:
    *
    * Goes for gold when in vision, otherwise makes its lowest resource the priority resource.
    *
    * If a trader is in vision and the player has more than zero gold, goes to trader
    * and attempts to trade. Trades until gold is zero, trader refuses, or resources are full or too expensive.
    * 
    * When it sees the priority resource in vision, will go for it.
    * If it is not in vision, look for the other resource and go for it if in vision.
    * If neither food or water is in vision, move forward one square and repeat.
    * 
    * If strength is less than half, rest until strength is full or food or water is <= 20% full.
    * 
    * When food or water is less than half, will prioritize that over gold and traders.
    * 
    * Trader behavior:
    * 
    */
    public void makeMove() {
        
    }
}
