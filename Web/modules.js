const path = require('path'), express = require('express'), app = express(), mongoose = require('mongoose'), players = require('./models/players'), 
router = express.Router(), playersFFA = require('./models/ffa'), playersGTC = require('./models/gtc'), playersOnline = require('./models/online'),
fetch = require('node-fetch'), schedule = require('node-schedule');

module.exports = {
    path: path,
    express: express,
    app: app,
    mongoose: mongoose,
    players: players,
    router: router,
    playersFFA: playersFFA,
    playersGTC: playersGTC,
    playersOnline: playersOnline,
    fetch: fetch,
    schedule: schedule
}