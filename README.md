# metaliquid

```
cd simple-service/build/
java -jar watchdog.jar
```

## REST API

```
http://127.0.0.1:8081/MetaLiquid/application.wadl
```

#### @Path("/book/query")
Example: /stats/book/query?q=select * from metaliquid.order limit 10

```
[  
   {  
      id:"1",
      bookId:"1",
      type:"ASK",
      tradableAmount:"50.015262",
      currencyPair:"1",
      pairId:"",
      timestamp:""
   },
   {  
      id:"2",
      bookId:"1",
      type:"ASK",
      tradableAmount:"1.240410",
      currencyPair:"1",
      pairId:"",
      timestamp:""
   },
   {  
      id:"3",
      bookId:"1",
      type:"ASK",
      tradableAmount:"0.100000",
      currencyPair:"1",
      pairId:"",
      timestamp:""
   }
]
```

#### @Path("/book/{id}")
Example: /stats/book/10
```
{
   ask: {
      count: 150,
      max: 1669,
      mean: 1653.8524666666667,
      min: 1640.24
   },
   bid: {
      count: 150,
      max: 1639.14,
      mean: 1628.6680000000001,
      min: 1614.52
   }
}
```

#### @Path("/book/{type}/{id}")
Example: /stats/book/ASK/10
```
{
  count: 300,
  max: 1668.5,
  mean: 1640.988233333333,
  min: 1613.45
}
```



#### @Path("/book/recent")
Example: /stats/book/recent
```
[
    {
        "id": "17388",
        "timestamp": "Mon Mar 09 20:42:32 PDT 2015",
        "exchange": "bitfinex",
        "pair": "BTC/USD"
    },
    {
        "id": "17396",
        "timestamp": "Mon Mar 09 20:43:08 PDT 2015",
        "exchange": "bitstamp",
        "pair": "BTC/USD"
    },
    {
        "id": "17400",
        "timestamp": "Mon Mar 09 23:43:26 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/CNY"
    },
    {
        "id": "17401",
        "timestamp": "Mon Mar 09 23:43:27 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/USD"
    },
    {
        "id": "17395",
        "timestamp": "Mon Mar 09 23:43:08 PDT 2015",
        "exchange": "okcoin",
        "pair": "BTC/CNY"
    },
    {
        "id": "17397",
        "timestamp": "Mon Mar 09 23:43:12 PDT 2015",
        "exchange": "okcoin",
        "pair": "BTC/USD"
    }
]
```

#### @Path("/book/recent/{n}/{exchange}")
Example: /stats/book/5/bitvc
```
[
    {
        "id": "17401",
        "timestamp": "Mon Mar 09 23:43:27 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/USD"
    },
    {
        "id": "17400",
        "timestamp": "Mon Mar 09 23:43:26 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/CNY"
    },
    {
        "id": "17399",
        "timestamp": "Mon Mar 09 23:43:13 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/USD"
    },
    {
        "id": "17398",
        "timestamp": "Mon Mar 09 23:43:12 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/CNY"
    },
    {
        "id": "17394",
        "timestamp": "Mon Mar 09 23:42:58 PDT 2015",
        "exchange": "bitvc",
        "pair": "BTC/USD"
    }
]
```

#### @Path("/book/total")
Example: /stats/book/total
```
[
    {
        "totalBooks": "4383"
    }
]
```

#### @Path("/book/total/bypair")
Example: /stats/book/total/bypair
```
[
    {
        "totalBooks": "1935",
        "e": "BTC/CNY"
    },
    {
        "totalBooks": "2448",
        "e": "BTC/USD"
    }
]
```

#### @Path("/book/total/byexchange")
Example: /stats/book/total/byexchange
```
[
    {
        "totalBooks": "281",
        "e": "bitfinex"
    },
    {
        "totalBooks": "227",
        "e": "bitstamp"
    },
    {
        "totalBooks": "1964",
        "e": "bitvc"
    },
    {
        "totalBooks": "1911",
        "e": "okcoin"
    }
]
```
