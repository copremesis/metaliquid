// ===================================
// `tail -f` in Node.js and WebSockets
// ===================================
var http    = require('http'),
    io      = require('socket.io'),
    fs      = require('fs'),
    util    = require('util');

var backlog_size = 2000;

function startSocketTail(filename) {
    filename = "./log/"+filename
    // -- Setup Socket.IO ---------------------------------------------------------
    var socket = io.listen(3000);
    socket.on('connection', function(client){
        client.emit('start', { filename: filename })
        fs.stat(filename,function(err,stats){
            if (err) throw err;
            var start = (stats.size > backlog_size)?(stats.size - backlog_size):0;
            var stream = fs.createReadStream(filename,{start:start, end:stats.size});
            // initially stream the file
            stream.addListener("data", function(lines){
                lines = lines.toString('utf-8');
                lines = lines.slice(lines.indexOf("\n")+1).split("\n");
                client.emit('tail-'+filename, { lines: lines })
            });
        });
    })
}