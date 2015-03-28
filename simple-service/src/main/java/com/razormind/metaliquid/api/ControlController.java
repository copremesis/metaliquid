package com.razormind.metaliquid.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import api.exchanges.BitVc;
import api.exchanges.OkCoin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.razormind.metaliquid.AppServices;
import com.razormind.metaliquid.J2Sql;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.trade.LimitOrder;

@Path("/exchange")
public class ControlController {
	
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
    	LimitOrder order = new LimitOrder(t, amount, p,"123",date, price);
    	System.out.println(order);
    	String orderResponse = BitVc.PlaceLimitOrder(order); 
    	System.out.println("==================="+orderResponse);
    	return ToJson(orderResponse);    	
    }
	
	private String ToJson(Object obj) throws JsonProcessingException {
    	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    	String json = ow.writeValueAsString(obj);
    	return json;
    }
	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/order/{exchange}/{type}/{qty}/{volume}")
	public String Order() {
		LimitOrder order = new LimitOrder()
		return StreamGobbler.output;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/{type}/{id}")
	public Stats bookStatsByType(@PathParam("type") String type,
			@PathParam("id") String id) {
		J2Sql sql = J2Sql.getInstance();
		return computeDescriptiveStats(sql.fetchOrders(type, id));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/{id}")
	public AllStats bookStats(@PathParam("id") String id) throws Exception {
		J2Sql sql = J2Sql.getInstance();
		Stats ask = computeDescriptiveStats(sql.fetchOrders("ASK", id));
		Stats bid = computeDescriptiveStats(sql.fetchOrders("BID", id));

		AllStats stats = new AllStats(bid, ask);
		return stats;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/query")
	public String bookStatsQuery(@QueryParam("q") String q) {
		J2Sql sql = J2Sql.getInstance();
		return sql.runQuery(q).toString();
	}

	private Stats computeDescriptiveStats(List<Double> values) {
		Stats stats = new Stats();

		double sum = 0;
		for (int i = 0; i < values.size(); i++) {
			sum += values.get(i);
			stats.max = Math.max(stats.max, values.get(i));
			stats.min = Math.min(stats.min, values.get(i));
		}
		stats.mean = sum / values.size();
		stats.count = values.size();
//		System.out.println(stats);
		return stats;
	}
*/
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/restart")
	public String Restart() throws InterruptedException {
		System.out.println("restarting");
		new AppServices().restartApplicationGone();
		return "{response: \"OK\"}";
	}
	
	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/emptybooks")
	public String EmptyBook() throws InterruptedException, SQLException {
		System.out.println("emptying book table");
		J2Sql sql = J2Sql.getInstance();
		sql.emptyBook();
		return "{status: ok}";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/emptyorders")
	public String EmptyOrder() throws InterruptedException, SQLException {
		System.out.println("emptying order table");
		J2Sql sql = J2Sql.getInstance();
		sql.emptyOrder();
		return "{status: ok}";
	}*/
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/emptydatabase")
	public String EmptyAll() throws InterruptedException, SQLException {
		System.out.println("emptying order table");
		J2Sql sql = J2Sql.getInstance();
		sql.emptyOrder();
		sql.emptyBook();
		System.exit(0); 
		return "{'status': 'ok'}";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/stop")
	public String Stop()  {
		System.exit(0); 
		return "{'status': 'ok'}";
	}
	
	 
}
