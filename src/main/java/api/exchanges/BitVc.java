package api.exchanges;

import java.io.IOException;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.bitvc.BitVcExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import api.config.Config;

public class BitVc {
	private static Class<?> targetExchange = BitVcExchange.class;
	
	public static AccountInfo GetAccountInfo() throws IOException {
		
		/*Exchange bfx = Config.ExchangeInstance(targetExchange);
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
	    
	    return accountInfo;*/
		return new AccountInfo(null,null);
	}
	
	public static OrderBook GetOrderBook(CurrencyPair pair) throws IOException {
		PollingMarketDataService marketDataService = Config.PollingServiceInstance(targetExchange);
		OrderBook orderBook = marketDataService.getOrderBook(pair);
		return orderBook;
	}
	
	
}

