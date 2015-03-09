package com.metaliquid;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

}