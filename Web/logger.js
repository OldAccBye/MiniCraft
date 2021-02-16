const fs = require('fs'), infoStream = fs.createWriteStream('./logs/info.txt');

const Logger = (msg) => {
    console.log(msg);
    infoStream.write(new Date().toISOString() + " : " + msg + "\n")
};

module.exports = Logger;