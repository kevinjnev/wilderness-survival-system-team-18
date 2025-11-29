public class FriendlyTrader extends Trader {
    public FriendlyTrader() { super(Temperament.FRIENDLY, 200, 200); }
    public FriendlyTrader(int gold, int limit) { super(Temperament.FRIENDLY, gold, limit); }
}
