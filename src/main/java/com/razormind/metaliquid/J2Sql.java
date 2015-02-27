package com.razormind.metaliquid;

import java.sql.*;

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
					.getConnection(url + dbName, userName, password);

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
	public synchronized void InsertOrderBook(OrderBook _book, String _exchange) {
		try {
			System.out.println("SQL + inserting orderbook for " + _exchange);
			int id = InsertBook(_book, _exchange);
			if (id != -1) {
				InsertOrder(_book, OrderType.ASK, id);
				InsertOrder(_book, OrderType.BID, id);
			} else {
				System.out.println("----------SKIPPING--------"
						+ _book.getTimeStamp() + ", " + _exchange);
			}
		} catch (Exception e) {
			System.out.println("SQL + inserting orderbook FAIL " + _exchange);
			e.printStackTrace();
		}
	}

	private int InsertBook(OrderBook book, String exchange) throws SQLException {
		String bookQuery = "insert into metaliquid.book (timeStamp, exchange) values (?, ?)";
		int bookId = 0;
		if (!Unique(book, exchange))
			return -1;
		try {
			System.out.println("SQL + inserting book " + exchange);
			PreparedStatement preparedStmt = conn.prepareStatement(bookQuery,
					Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, book.getTimeStamp().toString());
			preparedStmt.setString(2, exchange);
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

	private void InsertOrder(OrderBook book, OrderType type, int bookId)
			throws SQLException {
		// Insert Order Statement
		int CurrencyPairId = 1;
		String orderQuery = "insert into metaliquid.order (bookId, type, tradableAmount, currencyPair, pairId, timestamp, limitPrice) values (?, ?, ?, ?, ?, ?, ?)";
		for (LimitOrder order : book.getOrders(type)) {
			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = conn.prepareStatement(orderQuery);
				preparedStmt.setInt(1, bookId);
				preparedStmt.setString(2, type.toString());
				preparedStmt.setBigDecimal(3, order.getTradableAmount());
				preparedStmt.setInt(4, CurrencyPairId);
				if (order.getId() != null) {
					preparedStmt.setString(5, order.getId());
				} else {
					preparedStmt.setString(5, "");
				}
				if (order.getTimestamp() != null) {
					preparedStmt.setString(6, order.getTimestamp().toString());
				} else {
					preparedStmt.setString(6, "");
				}
				preparedStmt.setBigDecimal(7, order.getLimitPrice());
				preparedStmt.execute();

			} catch (SQLException e) {
				System.err.println(preparedStmt);
				e.printStackTrace();
			}
		}
	}
}