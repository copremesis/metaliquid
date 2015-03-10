package com.razormind.metaliquid;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.razormind.metaliquid.api.ControlController;
import com.razormind.metaliquid.api.StreamGobbler;
import com.xeiam.xchange.currency.CurrencyPair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://127.0.0.1:8082/MetaLiquid/";
    static HttpServer _server = new HttpServer();
    public static List<DbThread> threads = new ArrayList<DbThread>();
    
    public static HttpServer getServer() {
    	return _server;
    }
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @throws InterruptedException 
     */
    public static HttpServer startServer() throws InvocationTargetException, BindException {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig(ControlController.class);
        try {
        	return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        } catch(Exception e) {
        	System.out.println("Here it is");
        	return new HttpServer();
        }
       
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, InterruptedException {
    	try {
    	 _server = startServer();
         System.out.println(String.format("Jersey app started with WADL available at "
                 + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    	
    	DbThread bitstampThread = new DbThread( bitstamp.class);
    	DbThread bitfinexThread = new DbThread( bitfinex.class);
    	DbThread bitvcThreadUsd = new DbThread( bitvc.class);
    	DbThread bitvcThreadCny = new DbThread( bitvc.class);
    	DbThread okcoinThreadUsd = new DbThread( okcoin.class);
    	DbThread okcoinThreadCny = new DbThread( okcoin.class);
    	Purger purgeThread = new Purger();
    	
    	purgeThread.start();
    	bitstampThread.start();
    	bitfinexThread.start();
    	bitvcThreadUsd.start();
    	okcoinThreadUsd.start();
    	bitvcThreadCny.start(CurrencyPair.BTC_CNY);
    	okcoinThreadCny.start(CurrencyPair.BTC_CNY);
    	
    	
    	threads.add(bitstampThread);
    	threads.add(bitfinexThread);
    	threads.add(bitvcThreadUsd);
    	threads.add(okcoinThreadUsd);
    	threads.add(bitvcThreadCny);
    	threads.add(okcoinThreadCny); }
    	catch(OutOfMemoryError | InvocationTargetException | BindException e) {
    		System.out.println("============================--OOPS--=============");
    		new AppServices().freememory();
    		System.exit(0);
    	} finally {
    		System.out.println("============================--FINALLY--=============");
    		
    	}
    	
    }
    
    
}

