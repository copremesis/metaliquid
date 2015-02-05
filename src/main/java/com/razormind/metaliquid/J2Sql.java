package com.razormind.metaliquid;
import java.sql.*;

import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.trade.LimitOrder;

import org.sqlite.JDBC;

public class J2Sql implements Runnable  {
	
	public J2Sql(OrderBook book, String exchange) {
		_book = book;
		_exchange = exchange;
	}
	private Thread t;
	static Connection conn = null;
	private OrderBook _book;
	private String _exchange;
	
	public void connect() {
		String url = "jdbc:sqlite:";
		String dbName = _exchange+".db";
		String driver = "org.sqlite.JDBC";
		String userName = "";
		String password = "";
		
		try {
			 System.out.println("SQL + connecting " +  _exchange );
			Class.forName(driver).newInstance();
			if (conn == null)
				System.out.println("SQL + connecting FAIL " +  _exchange );
				conn = DriverManager.getConnection(url+dbName,userName,password);			
			
		} catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	public void run() {
		connect();
		try {
			InsertOrderBook();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start ()
	   {
	      System.out.println("SQL + Starting " +  _exchange );
	      if (t == null)
	      {
	         t = new Thread (this, _exchange);
	         t.start ();
	      }
	   }
	
	public void InsertOrderBook() throws SQLException {		
		try {		
			System.out.println("SQL + inserting orderbook " +  _exchange );
			int id = InsertBook(_book, _exchange); 
			InsertOrder(_book, OrderType.ASK, id);
			InsertOrder(_book, OrderType.BID, id);
			
		} catch (Exception e){
			System.out.println("SQL + inserting orderbook FAIL " +  _exchange );
			e.printStackTrace();			
		}
		//conn.close();
	}
	
	public int InsertBook(OrderBook book, String exchange) throws SQLException {
		connect();
		String bookQuery = "insert into book (timeStamp, exchange) values (?, ?)";
		int bookId = 0;
		
		try {
			System.out.println("SQL + inserting book " +  _exchange );
			PreparedStatement preparedStmt = conn.prepareStatement(bookQuery);
			preparedStmt.setDate(1, new java.sql.Date(book.getTimeStamp().getTime()));
			preparedStmt.setString(2, exchange);
			preparedStmt.execute();
			
			try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                bookId = generatedKeys.getInt(1);
	            }
	            else {
	            	System.out.println("SQL + Creating book failed, no ID obtained. " +  _exchange );
	                throw new SQLException("Creating book failed, no ID obtained.");
	            }
	        }
		} catch (SQLException e) {
			System.out.println("SQL + inserting book FAIL " +  _exchange );
			e.printStackTrace();
		}
		return bookId;
		
	}
	public void InsertOrder(OrderBook book, OrderType type, int bookId) throws SQLException {
		//Insert Order Statement
		connect();
		int CurrencyPairId = 1;
		String orderQuery = "insert into 'order' (bookId, type, tradableAmount, currencyPair, id, timestamp, limitPrice) values (?, ?, ?, ?, ?, ?, ?)";
	    for(LimitOrder order : book.getOrders(type)) {
	    	PreparedStatement preparedStmt;
			try {
				preparedStmt = conn.prepareStatement(orderQuery);
				preparedStmt.setInt (1, bookId);
		    	preparedStmt.setString (2, type.toString());
		        preparedStmt.setBigDecimal(3, order.getTradableAmount());
		        preparedStmt.setInt (4, CurrencyPairId);
		        if (order.getId() != null )
		        	preparedStmt.setString(5, order.getId());
		        else
		        	preparedStmt.setString(5, "");
		        if (order.getTimestamp() != null )
		        	preparedStmt.setString(6, order.getTimestamp().toString());
		        else
		        	preparedStmt.setString(6, "");
		        preparedStmt.setBigDecimal(7, order.getLimitPrice());
		        preparedStmt.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}	    	
		}
	}	
}
