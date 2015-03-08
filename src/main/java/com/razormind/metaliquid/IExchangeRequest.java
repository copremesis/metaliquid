package com.razormind.metaliquid;

import java.io.IOException;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;

public interface IExchangeRequest {

	String account() throws IOException;
	String getOrderBook() throws IOException;
	OrderBook getOrderBookObj() throws IOException;
	String getOrderBookForPair(CurrencyPair pair) throws IOException;	
}
