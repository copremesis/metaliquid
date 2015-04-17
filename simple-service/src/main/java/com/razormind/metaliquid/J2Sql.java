package com.razormind.metaliquid;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;

/**
 * Ideally this should be a singleton class. Leaving it as is since different
 * thread exchanges are using it and their shouldn't any overlap
 * 
 */
public class J2Sql {

	private Connection conn = null;

	public J2Sql() {
		connect(); // connect to the DB only once
	}

	private void connect() {
		String url = "jdbc:mysql://127.0.0.1:3306/";
		String dbName = "metaliquid";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "password";

		try {
			System.out.println("Connecting to DB @ " + url);
			Class.forName(driver).newInstance();
			conn = DriverManager
					.getConnection(url + dbName + "?allowMultiQueries=true", userName, password);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * keep this synchronized to avoid a race condition
	 * 
	 * @param _book
	 * @param _exchange
	 */
	public synchronized void InsertOrderBook(OrderBook _book, String _exchange, CurrencyPair pair) {
		try {
			System.out.println("SQL + inserting orderbook for " + _exchange);
			int id = InsertBook(_book, _exchange, pair);
			if (id != -1) {
				InsertOrder(_book, OrderType.ASK, id, pair);
				InsertOrder(_book, OrderType.BID, id, pair);
			} else {
				System.out.println("----------SKIPPING--------"
						+ _book.getTimeStamp() + ", " + _exchange);
			}			
		} catch (Exception e) {
			System.out.println("SQL + inserting orderbook FAIL " + _exchange);
			e.printStackTrace();
		}
		freememory();
		freememory();
	}
	
	private void UpdateBookMarkComplete(int bookId) throws SQLException {
		String bookQuery = "UPDATE `metaliquid`.`book` SET `complete`='1' WHERE `id`=?;";
		PreparedStatement preparedStmt = null;
		try {
			System.out.println("SQL + updating book " + bookId + " marking complete");
			preparedStmt = conn.prepareStatement(bookQuery);
			preparedStmt.setInt(1, bookId);
			System.out.println("SQL > update: "+preparedStmt);
			preparedStmt.executeUpdate();				
		} catch (SQLException e ) {
	        e.printStackTrace();
	        if (conn != null) {
	            try {
	                System.out.println("Transaction is being rolled back");
	                conn.rollback();
	            } catch(SQLException excep) {
	                excep.printStackTrace();
	            }
	        }
		} finally {
	        if (preparedStmt != null) {
	        	preparedStmt.close();
	        }
	    } 
	}

	private int InsertBook(OrderBook book, String exchange, CurrencyPair pair) throws SQLException {
		String bookQuery = "insert into metaliquid.book (timeStamp, exchange, pair) values (?, ?, ?)";
		int bookId = 0;
		if (!Unique(book, exchange))
			return -1;
		try {
			System.out.println("SQL + inserting book " + exchange);
			PreparedStatement preparedStmt = conn.prepareStatement(bookQuery,
					Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, book.getTimeStamp().toString());
			preparedStmt.setString(2, exchange);
			preparedStmt.setString(3, pair.toString());
			preparedStmt.execute();
			System.out.println(preparedStmt);
			try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					bookId = generatedKeys.getInt(1);
				} else {
					System.out
							.println("SQL + Creating book failed, no ID obtained. "
									+ exchange);
					throw new SQLException(
							"Creating book failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQL + inserting book FAIL " + exchange);
			e.printStackTrace();
		}
		return bookId;

	}

	private boolean Unique(OrderBook book, String exchange) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs;

		rs = stmt
				.executeQuery("SELECT * from metaliquid.book where timestamp = '"
						+ book.getTimeStamp().toString()
						+ "' AND exchange = '"
						+ exchange + "'");
		if (rs.next()) {
			System.out.println("Book already exists for " + book.getTimeStamp()
					+ ", exchange=" + exchange);
			return false;
		}
		return true;
	}

	private void InsertOrder(OrderBook book, OrderType type, int bookId, CurrencyPair pair)
			throws SQLException {
		String orderQuery = "insert into metaliquid.order (bookId, type, tradableAmount, currencyPair, pairId, timestamp, limitPrice) values (?, ?, ?, ?, ?, ?, ?)";
		int cnt = 0;
		for (LimitOrder order : book.getOrders(type)) {
			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = conn.prepareStatement(orderQuery);
				preparedStmt.setInt(1, bookId);
				preparedStmt.setString(2, type.toString());
				preparedStmt.setBigDecimal(3, order.getTradableAmount());
				preparedStmt.setString(4, pair.toString());
				String id = order.getId();
				if (id != null && id != "" ) {
					preparedStmt.setInt(5, Integer.parseInt(order.getId()));
				} else {
					preparedStmt.setInt(5, 0);
				}
				if (order.getTimestamp() != null) {
					preparedStmt.setString(6, order.getTimestamp().toString());
				} else {
					preparedStmt.setString(6, "");
				}
				preparedStmt.setBigDecimal(7, order.getLimitPrice());
				preparedStmt.execute();
				cnt++;

			} catch (SQLException e) {
				System.err.println(preparedStmt);
				e.printStackTrace();
			}
			if (cnt == book.getOrders(type).size() && type == OrderType.BID)
				UpdateBookMarkComplete(bookId);
		}
	}

	public void purge(int max, int purgeCount) {
		String countQuery = "select exchange, count(*) cnt from metaliquid.book group by exchange";
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(countQuery);

			while (rs.next()) {
				int count = rs.getInt("cnt");
				String exchange = rs.getString("exchange");

				if (count > max) {
					// System.out.println(exchange + ":" + count);

					System.out.println("=====> Removing top " + purgeCount
							+ " rows for exchange=" + exchange);

					String deleteQuery = "DELETE from `book` where exchange = ? limit ?";
					PreparedStatement preparedStmt = conn
							.prepareStatement(deleteQuery);
					preparedStmt.setString(1, exchange);
					preparedStmt.setInt(2, purgeCount);

					preparedStmt.execute();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String PaternReplace(String toReplace, String what, String with) {
		 Pattern p = Pattern.compile(what);
		 Matcher m = p.matcher(toReplace);
		 StringBuffer sb = new StringBuffer();
		 while (m.find()) {
		     m.appendReplacement(sb, with);
		 }
		 m.appendTail(sb);
		 String retVal = sb.toString();
		 System.out.println(retVal);
		 return retVal;
	}
	
	public List<String> runStoredQuery(int queryId, String[] params) {
		List<String> results = new ArrayList<String>();
		List<String> map = getParametersForStoredQuerySet(queryId);
		List<String> queries = getRawQuerySet(queryId);
		for (String q : queries ) {
			String query = InjectParametersIntoSqlStatement(params, map, q);
			JsonParser parser = new JsonParser();
			JsonObject o = (JsonObject)parser.parse(query);
			query = o.get("rawQuery").toString().replace("\"", "");
			try {
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					
					while (rs.next()) {
						JsonObject obj = new JsonObject();
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							obj.addProperty(rs.getMetaData().getColumnName(i).toString(), rs.getObject(i).toString());
						}
						results.add(obj.toString());
					}
						
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(results);
		return results;
	}
	public void runStoredProc() throws SQLException {
		CallableStatement cStmt = conn.prepareCall("{call new_procedure()}");
		//cStmt.registerOutParameter(1, Types.VARCHAR);
		boolean hadResults = cStmt.execute();
		ResultSet rs = cStmt.getResultSet();
		if (hadResults) {
			 String foo = "";
		}
		
	}
	private String InjectParametersIntoSqlStatement(String[] params, List<String> map, String query) {
		int idx = 0;
		for (String param : map) {
			JsonParser parser = new JsonParser();
			JsonObject o = (JsonObject)parser.parse(param);
			query = PaternReplace(query, o.get("param").toString(), params[idx]);
			idx++;
		}
		return query;
	}
	
	public List<String> getRawQuerySet(int id) {
		List<String> results = new ArrayList<String>();
		String rawQuery = "select * from storedquery where queryset = "+id;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(rawQuery);
			System.out.println("==============================");
			System.out.println(rawQuery);
			System.out.println("==============================");
			
			while (rs.next()) {
				JsonObject obj = new JsonObject();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					obj.addProperty(rs.getMetaData().getColumnName(i).toString(), rs.getObject(i).toString());
				}
				results.add(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(results);
		return results;
	}

	public List<String> getParametersForStoredQuerySet(int id) {
		List<String> results = new ArrayList<String>();
		String paramQuery = "SELECT paramname, param FROM parameter_storedqueryjoin j join parameter p on p.id = j.parameterid where storedquerysetid = "+id;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(paramQuery);
			System.out.println("==============================");
			System.out.println(rs.toString());
			System.out.println("==============================");
			while (rs.next()) {
				JsonObject obj = new JsonObject();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					obj.addProperty(rs.getMetaData().getColumnName(i).toString(), rs.getObject(i).toString());
				}
				results.add(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(results);
		return results;
	}
	
	public List<String> runQuery(String query) {
		List<String> results = new ArrayList<String>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			System.out.println("==============================");
			System.out.println(rs.toString());
			System.out.println("==============================");
			while (rs.next()) {
				JsonObject obj = new JsonObject();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					obj.addProperty(rs.getMetaData().getColumnName(i).toString(), rs.getObject(i).toString());
				}
				results.add(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(results);
		return results;
	}

	public List<Double> fetchOrders(String type, String id) {
		List<Double> prices = new ArrayList<Double>();
		String query = "select limitPrice from metaliquid.order where bookId="
				+ id + " and type='" + type.toUpperCase() + "'";
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				double price = rs.getDouble("limitPrice");
				prices.add(price);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prices;
	}

	/* Create a singleton interface for the controller */
	private static J2Sql j2Sql = new J2Sql();

	public static J2Sql getInstance() {
		if (j2Sql == null) {
			synchronized (J2Sql.class) {
				j2Sql = new J2Sql();
			}
		}
		return j2Sql;
	}
	

	public String emptyBook() throws SQLException {
		Main.threads.forEach((thread) -> thread.stop());		
		String deleteQuery = "DELETE from `book` where id != -1";
		PreparedStatement preparedStmt = conn.prepareStatement(deleteQuery);

		preparedStmt.execute();	
	
		Main.threads.forEach((thread) -> thread.start());
		return "{success: 'ok'}";	
	}

	public String emptyOrder() throws SQLException {
		Main.threads.forEach((thread) -> thread.stop());
		String deleteQuery = "DELETE from `metaliquid`.`order` where id != -1";
		Statement stmt = conn.createStatement();
		int rs;
		try {
		rs = stmt.executeUpdate(deleteQuery); } catch(Exception e) {
			String err = e.getMessage();			
		}
	
		Main.threads.forEach((thread) -> thread.start());
		return "{success: 'ok'}";
	}
	

	public void freememory(){
	    Runtime basurero = Runtime.getRuntime(); 
	    basurero.gc();
	}
}