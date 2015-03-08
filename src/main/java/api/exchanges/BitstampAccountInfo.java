package api.exchanges;

import java.util.List;

import com.xeiam.xchange.bitstamp.dto.account.BitstampBalance;
import com.xeiam.xchange.bitstamp.dto.account.BitstampDepositAddress;
import com.xeiam.xchange.bitstamp.dto.account.DepositTransaction;
import com.xeiam.xchange.bitstamp.dto.account.WithdrawalRequest;

public class BitstampAccountInfo {
	public BitstampBalance bitstampBalance;
	public BitstampDepositAddress depositAddress;
	public List<DepositTransaction> unconfirmedDeposits;
	public List<WithdrawalRequest> withdrawalRequests;
}