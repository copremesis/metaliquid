package com.razormind.metaliquid;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.razormind.metaliquid.api.StatsController;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://127.0.0.1:8081/MetaLiquid/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig(StatsController.class);
        
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
    	DbThread bitstampThread = new DbThread( bitstamp.class);
    	DbThread bitfinexThread = new DbThread( bitfinex.class);
    	DbThread bitvcThread = new DbThread( bitvc.class);
    	DbThread okcoinThread = new DbThread( okcoin.class);
    	Purger purgeThread = new Purger();
    	
    	purgeThread.start();
    	bitstampThread.start();
    	bitfinexThread.start();
    	bitvcThread.start();
    	okcoinThread.start();
    	
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

