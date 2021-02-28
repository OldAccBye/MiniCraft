const path = require('path'), express = require('express'), app = express(), router = express.Router(), mongoose = require('mongoose'),
player = require('./models/player'), playerFFA = require('./models/ffa'), playerGTC = require('./models/gtc'), playerOnline = require('./models/online'),
banned = require('./models/banned'), fetch = require('node-fetch'), schedule = require('node-schedule');

module.exports = {
    path: path,
    express: express,
    app: app,
    router: router,
    mongoose: mongoose,
    player: player,
    playerFFA: playerFFA,
    playerGTC: playerGTC,
    playerOnline: playerOnline,
    banned: banned,
    fetch: fetch,
    schedule: schedule
}