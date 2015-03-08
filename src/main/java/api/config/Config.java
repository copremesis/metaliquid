package api.config;

import java.util.HashMap;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

public class Config {
	
	public static HashMap<String, Exchange> map = new HashMap<String, Exchange>();
	public static PollingMarketDataService pfx;
	
	public static Exchange ExchangeInstance(Class makeFrom) {
		Exchange bfx = null;
		if (!map.containsKey(makeFrom.getName())) {
			bfx = ExchangeFactory.INSTANCE.createExchange(makeFrom.getName());
			ExchangeSpecification defSpec = bfx.getDefaultExchangeSpecification();
		    ExchangeSpecification exSpec = new ExchangeSpecification(makeFrom);
		    
		    switch(makeFrom.getName()) {
		    	case "com.xeiam.xchange.bitfinex.v1.BitfinexExchange":		    		
		    		exSpec.setApiKey("aMxrHXbZz6KOKDwZij898axktTbYMHWqLwUDrFmFFY8");
		    		exSpec.setSecretKey("1sbprjasGcpcVRK8Z7QHvCcXzldUoJEOBvkqpdIy0D8");
		    		break;
		    	case "com.xeiam.xchange.okcoin.OkCoinExchange":
		    		exSpec.setExchangeSpecificParametersItem("Use_Intl", false);
		    		break;
		    	case "com.xeiam.xchange.bitstamp.BitstampExchange":
		    		exSpec = defSpec;
		    	    exSpec.setUserName("520837");
		    	    exSpec.setApiKey("a4SDmpl9s6xWJS5fkKRT6yn41vXuY0AM");
		    	    exSpec.setSecretKey("sisJixU6Xd0d1yr6w02EHCb9UwYzTNuj");
		    		break;
		    }
	
		    bfx.applySpecification(exSpec);
		    map.put(makeFrom.getName(), bfx);
	    } else {
	    	bfx = map.get(makeFrom.getName());
	    }
	    return bfx;
	  }
	
	public static PollingMarketDataService PollingServiceInstance(Class makeFrom) {			
		return ExchangeInstance(makeFrom).getPollingMarketDataService();
	}
}
