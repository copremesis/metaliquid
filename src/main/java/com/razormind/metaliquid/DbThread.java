package com.razormind.metaliquid;

import java.io.IOException;
import com.xeiam.xchange.dto.marketdata.OrderBook;

class DbThread implements Runnable {
   private Thread t;
   private Class<IExchangeRequest> threadTarget;
   boolean forever = true;
   IExchangeRequest exchange = null;
   
   DbThread(Class<?> class1){
	   threadTarget = (Class<IExchangeRequest>)class1;
       
       System.out.println("Creating " +  threadTarget );
   }
   
   public void run() {
	  int cnt = 0;
      System.out.println("Running " +  threadTarget );
      try {
         while(forever) {
            System.out.println(threadTarget+" Tick" + cnt++);
            if (exchange == null)
            	exchange = (IExchangeRequest) Class.forName(threadTarget.getName()).newInstance();
            OrderBook data = exchange.getOrderBookObj();
            //System.out.println(data);
            sendBookToDatabase(data);
            Thread.sleep(500);
         }
     } catch (InterruptedException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
         System.out.println("Thread " +  threadTarget + " interrupted.");
     }
     System.out.println("Thread " +  threadTarget + " exiting.");
   }
   
   private void sendBookToDatabase(OrderBook book) {
	   J2Sql sql = new J2Sql(book,threadTarget.getSimpleName());
	   sql.start();
   }

public void start ()
   {
      System.out.println("Starting " +  threadTarget );
      if (t == null)
      {
         t = new Thread (this, threadTarget.getClass().getName());
         t.start ();
      }
   }
}