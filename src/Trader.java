public abstract class Trader {

	public enum Temperament { FRIENDLY, NEUTRAL, HOSTILE }

	protected Temperament temperament;
	protected int gold;
	protected double waterPrice = 1.0;
	protected double foodPrice = 1.0;
	protected boolean acceptDeal = true;
	protected int tradesMade = 0;
	protected int tradeLimit = 100;
    protected double profitMargin = 0.15;
    protected int acceptableTolerance = 5;
    protected int annoyance = 0;
    protected double goldWeight = 1.0;
    protected double goodsWeight = 1.0;

	public static class TradeOffer {
		public final int goldOffered;
		public final double waterOffered;
		public final double foodOffered;
		public final double waterRequested;
		public final double foodRequested;

		public TradeOffer(int goldOffered, double waterRequested, double foodRequested) {
			this(goldOffered, 0.0, 0.0, waterRequested, foodRequested);
		}

		public TradeOffer(int goldOffered, double waterOffered, double foodOffered, double waterRequested, double foodRequested) {
			this.goldOffered = Math.max(0, goldOffered);
			this.waterOffered = Math.max(0.0, waterOffered);
			this.foodOffered = Math.max(0.0, foodOffered);
			this.waterRequested = Math.max(0.0, waterRequested);
			this.foodRequested = Math.max(0.0, foodRequested);
		}

		@Override
		public String toString() {
			return "Offer{gold=" + goldOffered + ", payW=" + waterOffered + ", payF=" + foodOffered + ", reqW=" + waterRequested + ", reqF=" + foodRequested + "}";
		}
	}

	public static class TradeResponse {
		public enum Action { ACCEPT, COUNTER, REJECT }

		public final Action action;
		public final TradeOffer counterOffer;
		public final String message;

		public TradeResponse(Action action, TradeOffer counterOffer, String message) {
			this.action = action;
			this.counterOffer = counterOffer;
			this.message = message;
		}
	}

	protected Trader(Temperament temperament, int startingGold, int tradeLimit) {
		this.temperament = temperament == null ? Temperament.NEUTRAL : temperament;
		this.gold = Math.max(0, startingGold);
		this.tradeLimit = Math.max(0, tradeLimit);
		applyTemperamentDefaults();
	}

	public boolean supplyStatus() {
		return acceptDeal && tradesMade < tradeLimit;
	}
	public TradeResponse analyzeOffer(TradeOffer offer) {
		if (offer == null) return new TradeResponse(TradeResponse.Action.REJECT, null, "Invalid");
		if (!supplyStatus()) return new TradeResponse(TradeResponse.Action.REJECT, null, "No trades");
		double valueRequested = offer.waterRequested * waterPrice + offer.foodRequested * foodPrice;
		double expected = valueRequested * (1.0 + profitMargin);
		double paymentValue = offer.goldOffered * goldWeight + (offer.waterOffered * waterPrice + offer.foodOffered * foodPrice) * goodsWeight;
		if (paymentValue >= expected) {
			return new TradeResponse(TradeResponse.Action.ACCEPT, null, "Accepted");
		}
		annoyance++;
		if (annoyance > acceptableTolerance) { acceptDeal = false; return new TradeResponse(TradeResponse.Action.REJECT, null, "Left"); }
		int need = (int) Math.ceil(expected - paymentValue);
		TradeOffer counter = new TradeOffer((int)Math.max(0, offer.goldOffered + need), offer.waterOffered, offer.foodOffered, offer.waterRequested, offer.foodRequested);
		return new TradeResponse(TradeResponse.Action.COUNTER, counter, "Counter:" + need);
	}

	public boolean executeTrade(Player player, TradeOffer agreedOffer) {
		if (player == null || agreedOffer == null) return false;
		if (!supplyStatus()) return false;
		if (!player.spendGold(agreedOffer.goldOffered)) return false;
		player.addResources(0.0, agreedOffer.waterRequested, agreedOffer.foodRequested);
		this.gold += agreedOffer.goldOffered;
		this.tradesMade++;
		this.annoyance = 0;
		return true;
	}

	protected double temperamentMultiplier() {
		switch (temperament) {default: return 1.0;}
	}

	public TradeOffer counterOffer(TradeOffer incoming) {
		TradeResponse resp = analyzeOffer(incoming);
		return resp.counterOffer;
	}

	protected void applyTemperamentDefaults(){
		switch(temperament){
			case FRIENDLY: profitMargin=0.05; acceptableTolerance=10; goldWeight=1.0; goodsWeight=1.0; break;
			case HOSTILE: profitMargin=0.30; acceptableTolerance=2; goldWeight=2.0; goodsWeight=0.5; break;
			default: profitMargin=0.15; acceptableTolerance=5; goldWeight=1.0; goodsWeight=1.0; break;
		}
	}

	public String supplyStatusMessage() {
		return "Trades: " + tradesMade + " / " + tradeLimit + (supplyStatus() ? " (can trade)" : " (no more trades)");
	}


}
