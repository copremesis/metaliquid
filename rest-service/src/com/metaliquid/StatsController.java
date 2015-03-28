package com.metaliquid;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/stats")
public class StatsController {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/output")
	public String Output() {
		return StreamGobbler.output;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/startcrawler")
	public String Crawler() {
		try {
			new AppServices().startApplication("rest.jar");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "{'status': 'ok'}";
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
	
	/************* Stats By Shannon Code ***************/
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/recent/{n}/{exchange}")
	public String recentNByExchange(
			@PathParam("n") int n,
			@PathParam("exchange") String exchange) {
		J2Sql sql = J2Sql.getInstance();
		String query = "select * from (select * from book where complete = 1 order by id desc) as i where exchange = '"+exchange+"' order by id desc limit "+n;
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/recent")
	public String recent() {
		J2Sql sql = J2Sql.getInstance();
		String query = "select * from (select * from book where complete = 1 order by id desc) as i group by i.exchange, pair";
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/recent/{n}")
	public String recentN(@PathParam("n") int n) {
		J2Sql sql = J2Sql.getInstance();
		String query = "select * from (select * from book where complete = 1 order by id desc) as i group by i.exchange, pair limit "+n;
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/total/bypair")
	public String totalByPair() {
		J2Sql sql = J2Sql.getInstance();
		String query = "SELECT count(*) totalBooks, pair e FROM metaliquid.book group by pair, e";
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/total/byexchange")
	public String totalByExchange() {
		J2Sql sql = J2Sql.getInstance();
		String query = "SELECT count(*) totalBooks, `exchange` e FROM metaliquid.book group by e";
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/total")
	public String totalBooks() {
		J2Sql sql = J2Sql.getInstance();
		String query = "SELECT count(*) totalBooks FROM metaliquid.book";
		return sql.runQuery(query).toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/vwd/{type}/{bookId}/{target}")
	public String volumeWeightedDepth(
			@PathParam("type") String type,
			@PathParam("bookId") String bookId,
			@PathParam("target") float target) {
		System.out.println("made it");
		J2Sql sql = J2Sql.getInstance();
		String query = "select * from (select *, @sum_tradableAmount := @sum_tradableAmount + tradableAmount as sum_tradableAmount from metaliquid.order where bookId = '"+bookId+"' and type = '"+type+"' limit 50) as t cross join (select @sum_tradableAmount := 0 as reset) const";
		List<String> orders = sql.runQuery(query);
		//JsonObject vwd = getVolumeWeightedDepth(target,orders);
		//.out.println(vwd);
		return orders.toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/cwd/{type}/{bookId}/{target}")
	public String costWeightedDepth(
			@PathParam("type") String type,
			@PathParam("bookId") String bookId,
			@PathParam("target") float target) {
		J2Sql sql = J2Sql.getInstance();
		String query = "select * from (select *, @sum_Cost := @sum_Cost + (limitPrice * tradableAmount) as sum_Cost from metaliquid.order where bookId = '"+bookId+"' and type = '"+type+"' limit 50) as t cross join (select @sum_Cost := 0 as reset) const";
		List<String> result = sql.runQuery(query);
		System.out.println(result);
		return result.toString();
	}
	
	
	/************* Stats By Shannon Code ***************/

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
		return stats;
	}
	
	/*private float getCostWeightedDepth(float target, List<String> orders) {
	int cnt = 0;
	float sumCoin = 0;
		for (String order:orders) {
			if (order.sum_Cost >= target) {
				cnt++;
				sumCoin += (order.tradableAmount - target);
				return sumCoin;
			}
			sumCoin += order.tradableAmount;
			cnt++;
		}
	}*/

	private JsonObject getVolumeWeightedDepth(float target, List<String> orders) {
		 
		int cnt = 0;
		int sumSpent = 0;
		float average = 0;
		JsonParser parser = new JsonParser();
			for (String order:orders) {
				System.out.println("made it into order loop");
				JsonObject o = (JsonObject)parser.parse(order);
				System.out.println("parsing the first order"+order+" as "+o);
				System.out.println("=================");
				String SumtradableAmount = o.get("sum_tradableAmount").toString();
				System.out.println("lets parse "+SumtradableAmount);
				//Float floated = Float.parseFloat(SumtradableAmount);
				System.out.println("lets parse again "+ SumtradableAmount);
				if (Float.parseFloat(o.get("sum_tradableAmount").toString()) >= target) {
					cnt++;
					sumSpent += (Float.parseFloat(o.get("sum_tradableAmount").toString()) - target) * Float.parseFloat(o.get("limitPrice").toString());
					average = sumSpent / target;					
				} else {
					sumSpent += Float.parseFloat(o.get("tradableAmount").toString()) * Float.parseFloat(o.get("limitPrice").toString());
					cnt++;
				}
			}
			System.out.println("made it out of order loop");
			System.out.println("{\"average\":\""+average+"\",\"depth\":\""+cnt+"\"}");
			return (JsonObject)parser.parse("{\"average\":\""+average+"\",\"depth\":\""+cnt+"\"}");
	}
}
