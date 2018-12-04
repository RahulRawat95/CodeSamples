var path = require('path');
var fs = require('fs'), ini = require('ini');

const self=this;

const configFile = path.resolve(path.join(__dirname, 'config.ini'));
var config = ini.parse(fs.readFileSync(configFile, 'utf-8'));
self.server=config.server;
self.database=config.database;
self.email=config.email;
module.exports = self;