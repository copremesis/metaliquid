package api.exchanges;

import java.io.IOException;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.bitfinex.v1.dto.account.BitfinexBalancesResponse;
import com.xeiam.xchange.bitfinex.v1.service.polling.BitfinexAccountServiceRaw;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import api.config.Config;

public class BitFinex {
	private static Class<?> targetExchange = BitfinexExchange.class;
	
	public static BitfinexBalancesResponse[] GetAccountInfo() throws IOException {
		Exchange bfx = Config.ExchangeInstance(targetExchange);
	    BitfinexAccountServiceRaw accountService = (BitfinexAccountServiceRaw) bfx.getPollingAccountService();
	    BitfinexBalancesResponse[] accountInfo = accountService.getBitfinexAccountInfo();
	    return accountInfo;
	}
	
	public static OrderBook GetOrderBook(CurrencyPair pair) throws IOException {
		PollingMarketDataService marketDataService = Config.PollingServiceInstance(targetExchange);
		OrderBook orderBook = marketDataService.getOrderBook(pair);
		return orderBook;
	}
	
	public static boolean PlaceOrder(CurrencyPair pair) {
		
		return false;		
	}
}