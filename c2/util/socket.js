// -- Node.js HTTP Server ----------------------------------------------------------

var http    = require('http'),
    io      = require('socket.io'),
    fs      = require('fs'),
    util    = require('util');


server = http.createServer(function(req, res){
  server.use(allowCrossDomain);
  res.writeHead(200, {'Content-Type': 'text/html'})
  fs.readFile(__dirname + '/index.html', function(err, data){
    res.write(data, 'utf8');
    res.end();
  });
})
server.listen(3001, '0.0.0.0');

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', config.allowedDomains);
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');

    next();
}