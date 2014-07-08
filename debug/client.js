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
    function writeStr(buffer, offset, str) {
        var lengthByteSize = Buffer.byteLength(str);

        var lengthBuffer = new Buffer(4);
        buffer.writeInt32BE(lengthByteSize, offset);

        buffer.write(str, offset + 4, lengthByteSize, 'utf8');

        return offset + 4 + lengthByteSize;
    }

    // calculate message size in bytes
    var paramLength = 0;
    parameterAry.forEach(function(str) {
        paramLength += 4 + Buffer.byteLength(str)
    });
    var messageSize = 8 + Buffer.byteLength(action) + 4 + paramLength;

    // create buffer
    var buffer = new Buffer(messageSize),
        offset = 0;

    // write message size
    buffer.writeInt32BE(messageSize, offset);
    offset += 4;

    // write action name
    offset = writeStr(buffer, offset, action);

    // write param count
    var paramCount = parameterAry.length;
    buffer.writeInt32BE(paramCount, offset);
    offset += 4;

    // write params
    parameterAry.forEach(function(str) {
        offset = writeStr(buffer, offset, str);
    });

    return buffer;
}

var client = net.connect({
    port: port,
    host: host
}, function() {
    console.log('connected');
    var authAction = 'NET_CLIENT_AUTH_REQ';
    client.write(transform(authAction, ['CLI User', 'second', 'third']));
    console.log('send auth');
});

client.on('data', function(buffer) {
    console.log('received', buffer.length, 'bytes');
});