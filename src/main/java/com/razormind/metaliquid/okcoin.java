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
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.okcoin.dto.account.OkCoinUserInfo;

import api.exchanges.BitFinex;
import api.exchanges.OkCoin;


/**
 * Root resource (exposed at "okcoin" path)
 */
@Path("okcoin")
public class okcoin implements IExchangeRequest {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account")
    public String account() throws IOException {    	
    	AccountInfo info = OkCoin.GetAccountInfo();
    	return ToJson(info);    	
    }    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook")
    public String getOrderBook() throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_USD); 
    	return ToJson(orderBook);    	
    }
    
    public OrderBook getOrderBookObj() throws IOException {
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_USD);   	
    	return orderBook;    	
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook_cny")
    public String getOrderBookCny() throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_CNY); 
    	return ToJson(orderBook);    	
    }
    
    @POST
    @Path("orderbook")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getOrderBookForPair(CurrencyPair pair) throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(pair);   	
    	return ToJson(orderBook);    	
    }
    
    private String ToJson(Object obj) throws JsonProcessingException {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = ow.writeValueAsString(obj);
    	return json;
    }
}
