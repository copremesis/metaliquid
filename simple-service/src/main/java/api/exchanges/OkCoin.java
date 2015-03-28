package api.exchanges;

import java.io.IOException;

import com.xeiam.xchange.okcoin.OkCoinExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;

import api.config.Config;

public class OkCoin {
	private static Class<?> targetExchange = OkCoinExchange.class;
	
	public static AccountInfo GetAccountInfo() throws IOException {
		OkCoinExchange e = new OkCoinExchange();
		PollingAccountService a = e.getPollingAccountService();
	    return a.getAccountInfo();
	}
	
	public static OrderBook GetOrderBook(CurrencyPair pair) throws IOException {
		PollingMarketDataService marketDataService = Config.PollingServiceInstance(targetExchange);
		OrderBook orderBook = marketDataService.getOrderBook(pair);
		return orderBook;
	}
	
	public static String PlaceLimitOrder(LimitOrder limitOrder) throws IOException {
		PollingTradeService tradeService = Config.PollingTradeServiceInstance(targetExchange);
		String returnVal = tradeService.placeLimitOrder(limitOrder);
		return returnVal;
	}
	
	public static String PlaceMarketOrder(MarketOrder marketOrder) throws IOException {
		PollingTradeService tradeService = Config.PollingTradeServiceInstance(targetExchange);
		String returnVal = tradeService.placeMarketOrder(marketOrder);
		return returnVal;
	}
		 
	 public static boolean PlaceOrder(CurrencyPair pair) {
			
			return false;		
		}
}