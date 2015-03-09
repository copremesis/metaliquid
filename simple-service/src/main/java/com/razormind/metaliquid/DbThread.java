package com.razormind.metaliquid;

import java.io.IOException;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;

class DbThread implements Runnable {
	private Thread t;
	private Class<IExchangeRequest> threadTarget;
	boolean forever = true;
	IExchangeRequest exchange = null;
	private J2Sql j2sql;
	private CurrencyPair _pair;
	
	@SuppressWarnings("unchecked")
	DbThread(Class<?> class1) {
		threadTarget = (Class<IExchangeRequest>) class1;
		j2sql = new J2Sql();
		System.out.println("Creating " + threadTarget);
	}	
	public void run() {		
		System.out.println("Running " + threadTarget);
		try {
			while (forever) {				
				if (exchange == null) {
					exchange = (IExchangeRequest) Class.forName(
							threadTarget.getName()).newInstance();
				}
				OrderBook data = exchange.getOrderBookForPair(_pair);
				j2sql.InsertOrderBook(data, exchange.getClass().getSimpleName(), _pair);
				Thread.sleep(100);				
			}
		} catch (OutOfMemoryError | InterruptedException | InstantiationException
				| IllegalAccessException | ClassNotFoundException | IOException e) {
			System.out.println("Thread " + threadTarget + " interrupted.");
			try {
				new AppServices().restartApplication();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("Thread " + threadTarget + " exiting.");
	}
	public void start() {start(CurrencyPair.BTC_USD);}
	public void start(CurrencyPair pair) {
		System.out.println("Starting " + threadTarget);
		_pair = pair;
		if (t == null) {
			t = new Thread(this, threadTarget.getClass().getName());
			t.start();
		}
	}
	
	public void stop() {
		System.out.println("Stopping " + threadTarget);
		if (t == null) {
			t = new Thread(this, threadTarget.getClass().getName());
			t.destroy();;
		}
	}	
}