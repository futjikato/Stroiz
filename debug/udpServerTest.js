var dgram = require("dgram");

var s = dgram.createSocket("udp4");
s.bind(10080);

s.on("error", function (err) {
  console.log("server error:\n" + err.stack);
  server.close();
});

s.on("message", function (msg, rinfo) {
  console.log("server got: " + msg + " from " + rinfo.address + ":" + rinfo.port);
});