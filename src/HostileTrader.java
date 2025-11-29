public class HostileTrader extends Trader {
    public HostileTrader() { super(Temperament.HOSTILE, 50, 50); }
    public HostileTrader(int gold, int limit) { super(Temperament.HOSTILE, gold, limit); }
}
