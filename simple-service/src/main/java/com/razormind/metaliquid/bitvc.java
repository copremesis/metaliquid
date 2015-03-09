package com.razormind.metaliquid;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xeiam.xchange.bitfinex.v1.dto.account.BitfinexBalancesResponse;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;

import api.exchanges.BitFinex;
import api.exchanges.BitVc;


/**
 * Root resource (exposed at "bitfinex" path)
 */
@Path("bitvc")
public class bitvc implements IExchangeRequest {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account")
    public String account() throws IOException {    	
    	AccountInfo info = BitVc.GetAccountInfo();
    	return ToJson(info);    	
    }    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook")
    public OrderBook getOrderBook() throws IOException {    	
    	OrderBook orderBook = BitVc.GetOrderBook(CurrencyPair.BTC_USD);   	
    	return orderBook;    	
    }
    
    public OrderBook getOrderBookObj() throws IOException {
    	OrderBook orderBook = BitVc.GetOrderBook(CurrencyPair.BTC_USD);   	
    	return orderBook;    	
    }
    
    @POST
    @Path("orderbook")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderBook getOrderBookForPair(CurrencyPair pair) throws IOException {    	
    	OrderBook orderBook = BitVc.GetOrderBook(pair);   	
    	return orderBook;    	
    }
    
    private String ToJson(Object obj) throws JsonProcessingException {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = ow.writeValueAsString(obj);
    	return json;
    }
}
