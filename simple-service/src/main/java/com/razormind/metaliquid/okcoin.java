package com.razormind.metaliquid;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;
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
    	System.out.println(info);
    	return ToJson(info);    	
    }    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook")
    public String getOrderBookREST() throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_USD); 
    	return ToJson(orderBook);    	
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order/{type}/{amount}/{pair}/{price}")
    public String makeOrder(
    		@PathParam("type") String type,
			@PathParam("amount") BigDecimal amount,
			@PathParam("pair") String pair,
			@PathParam("price") BigDecimal price) throws IOException {  
    	OrderType t = OrderType.BID;
    	Date date = new Date();
    	CurrencyPair p = CurrencyPair.BTC_USD;
    	if (pair == "BTCCNY")
    		p = CurrencyPair.BTC_CNY;
    	if (type == "ASK")
    		t = OrderType.ASK;
    	System.out.println("place me an order");
    	LimitOrder order = new LimitOrder(t, amount, p,null,date, price);
    	System.out.println(order);
    	String orderResponse = OkCoin.PlaceLimitOrder(order); 
    	return ToJson(orderResponse);    	
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook")
    public OrderBook getOrderBook() throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_USD); 
    	return orderBook;    	
    }
    
    public OrderBook getOrderBookObj() throws IOException {
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_USD);   	
    	return orderBook;    	
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("orderbook_cny")
    public OrderBook getOrderBookCny() throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(CurrencyPair.BTC_CNY); 
    	return orderBook;    	
    }
    
    @POST
    @Path("orderbook")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderBook getOrderBookForPair(CurrencyPair pair) throws IOException {    	
    	OrderBook orderBook = OkCoin.GetOrderBook(pair);   	
    	return orderBook;    	
    }
    
    private String ToJson(Object obj) throws JsonProcessingException {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = ow.writeValueAsString(obj);
    	return json;
    }
}
