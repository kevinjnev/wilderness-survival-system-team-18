public class NeutralTrader extends Trader {
    public NeutralTrader() { super(Temperament.NEUTRAL, 100, 100); }
    public NeutralTrader(int gold, int limit) { super(Temperament.NEUTRAL, gold, limit); }
}
