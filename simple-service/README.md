# metaliquid

```
cd build/
java -jar rest.jar
```

## REST API

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
