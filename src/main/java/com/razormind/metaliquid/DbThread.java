package com.razormind.metaliquid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.streams.StreamReader;

import com.razormind.metaliquid.api.StreamGobbler;
import com.xeiam.xchange.dto.marketdata.OrderBook;

class DbThread implements Runnable {
	private Thread t;
	private Class<IExchangeRequest> threadTarget;
	boolean forever = true;
	IExchangeRequest exchange = null;
	private J2Sql j2sql;
	
	DbThread(Class<?> class1) {
		threadTarget = (Class<IExchangeRequest>) class1;
		j2sql = new J2Sql();
		System.out.println("Creating " + threadTarget);
	}
	
	public void run() {
		int cnt = 0;
		System.out.println("Running " + threadTarget);
		try {
			while (forever) {
				System.out.println(threadTarget + " Tick" + cnt++);
				if (exchange == null) {
					exchange = (IExchangeRequest) Class.forName(
							threadTarget.getName()).newInstance();
				}
				OrderBook data = exchange.getOrderBookObj();
				j2sql.InsertOrderBook(data, exchange.getClass().getSimpleName());
				Thread.sleep(100);
				if (cnt > 10) {
					new AppServices().restartApplication();
				}
			}
		} catch (InterruptedException | InstantiationException
				| IllegalAccessException | ClassNotFoundException | IOException e) {
			System.out.println("Thread " + threadTarget + " interrupted.");
		}
		System.out.println("Thread " + threadTarget + " exiting.");
	}
	
	public void start() {
		System.out.println("Starting " + threadTarget);
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