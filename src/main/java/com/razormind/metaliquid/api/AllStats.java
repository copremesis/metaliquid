package com.razormind.metaliquid.api;


class AllStats {

	private Stats bid;
	private Stats ask;

	public AllStats(Stats bid, Stats ask) {
		this.bid = bid;
		this.ask = ask;
	}
	
	public Stats getBid() {
		return bid;
	}

	public void setBid(Stats bid) {
		this.bid = bid;
	}

	public Stats getAsk() {
		return ask;
	}

	public void setAsk(Stats ask) {
		this.ask = ask;
	}
}
