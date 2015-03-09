package com.razormind.metaliquid;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.razormind.metaliquid.api.ControlController;
import com.razormind.metaliquid.api.StreamGobbler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
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
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig(ControlController.class);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	 _server = startServer();
         System.out.println(String.format("Jersey app started with WADL available at "
                 + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    	
    	DbThread bitstampThread = new DbThread( bitstamp.class);
    	DbThread bitfinexThread = new DbThread( bitfinex.class);
    	DbThread bitvcThread = new DbThread( bitvc.class);
    	DbThread okcoinThread = new DbThread( okcoin.class);
    	Purger purgeThread = new Purger();
    	Recycler recyclerThread = new Recycler();
    	
    	purgeThread.start();
    	bitstampThread.start();
    	bitfinexThread.start();
    	bitvcThread.start();
    	okcoinThread.start();
    	
    	threads.add(bitstampThread);
    	threads.add(bitfinexThread);
    	threads.add(bitvcThread);
    	threads.add(okcoinThread);
    	
        //System.in.read();
        //_server.stop();
    }
    
    
}

