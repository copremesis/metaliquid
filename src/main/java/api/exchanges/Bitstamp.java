package api.exchanges;

import java.io.IOException;
import java.util.List;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.bitstamp.dto.account.BitstampBalance;
import com.xeiam.xchange.bitstamp.dto.account.BitstampDepositAddress;
import com.xeiam.xchange.bitstamp.dto.account.DepositTransaction;
import com.xeiam.xchange.bitstamp.dto.account.WithdrawalRequest;
import com.xeiam.xchange.bitstamp.service.polling.BitstampAccountServiceRaw;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import api.config.Config;

public class Bitstamp {
	private static Class<?> targetExchange = BitstampExchange.class;
	
	public static BitstampAccountInfo GetAccountInfo() throws IOException {
		
		Exchange bfx = Config.ExchangeInstance(targetExchange);
		BitstampAccountServiceRaw accountService = (BitstampAccountServiceRaw) bfx.getPollingAccountService();
		BitstampBalance bitstampBalance = accountService.getBitstampBalance();
	    System.out.println("BitstampBalance: " + bitstampBalance);

	    BitstampDepositAddress depositAddress = accountService.getBitstampBitcoinDepositAddress();
	    System.out.println("BitstampDepositAddress address: " + depositAddress);

	    final List<DepositTransaction> unconfirmedDeposits = accountService.getUnconfirmedDeposits();
	    System.out.println("Unconfirmed deposits:");
	    for (DepositTransaction unconfirmedDeposit : unconfirmedDeposits) {
	      System.out.println(unconfirmedDeposit);
	    }

	    final List<WithdrawalRequest> withdrawalRequests = accountService.getWithdrawalRequests();
	    System.out.println("Withdrawal requests:");
	    for (WithdrawalRequest unconfirmedDeposit : withdrawalRequests) {
	      System.out.println(unconfirmedDeposit);
	    }  
	    BitstampAccountInfo accountInfo = new BitstampAccountInfo();
	    accountInfo.bitstampBalance = bitstampBalance;
	    accountInfo.depositAddress = depositAddress;
	    accountInfo.unconfirmedDeposits = unconfirmedDeposits;
	    accountInfo.withdrawalRequests = withdrawalRequests;
	    
	    return accountInfo;
	}
	
	public static OrderBook GetOrderBook(CurrencyPair pair) throws IOException {
		PollingMarketDataService marketDataService = Config.PollingServiceInstance(targetExchange);
		OrderBook orderBook = marketDataService.getOrderBook(pair);
		return orderBook;
	}
	
	
}

