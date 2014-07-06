var net = require('net');

var port = process.argv[2];
var host = process.argv[3];

if(!host) {
    host = 'localhost';
}

/**
 * Transforms an action name with parameters to a Buffer object containing the information.
 */
function transform(action, parameterAry) {
    /**
     * Helper function to write a string in the format <int:strlen><utf8:str>
     */
    function writeStr(buffer, str) {
        var lengthByteSize = Buffer.byteLength(str);

        var lengthBuffer = new Buffer(4);
        lengthBuffer.writeInt32BE(lengthByteSize, 0);

        var strBuffer = new Buffer(lengthByteSize);
        strBuffer.write(str, 0, lengthByteSize, 'utf8');

        return Buffer.concat([buffer, lengthBuffer, strBuffer]);
    }

    var sendBuffer = writeStr(new Buffer(0), action);

    var paramCount = parameterAry.length;

    var paramCountBuffer = new Buffer(4);
    paramCountBuffer.writeInt32BE(paramCount, 0);

    sendBuffer = Buffer.concat([sendBuffer, paramCountBuffer]);

    parameterAry.forEach(function(str) {
        sendBuffer = writeStr(sendBuffer, str);
    });

    return sendBuffer;
}

var client = net.connect({
    port: port,
    host: host
}, function() {
    console.log('connected');
    var authAction = 'NET_CLIENT_AUTH_REQ';
    client.write(transform(authAction, ["CLI User"]));
    console.log('send auth');
});

client.on('data', function(buffer) {
    console.log('received', buffer.length, 'bytes');

    var actionNameLength = buffer.readInt32BE(0);
    var actionName = buffer.toString('utf8', 4, 4 + actionNameLength);

    var paramCount = buffer.readInt32BE(4 + actionNameLength);

    console.log('response', actionName, 'with', paramCount, 'parameters');
});