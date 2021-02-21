const express = require('express'), router = express.Router(), players = require('../models/players'), playersFFA = require('../models/ffa'), playersGTC = require('../models/gtc');

router.get('/', async (req, res) => {
    res.render('index', { userCount: await players.countDocuments() });
});

router.get('/team', async (req, res) => {
    const b = await players.find({ group: "builder" }),
    s = await players.find({ group: "supporter" }),
    m = await players.find({ group: "moderator" }),
    a = await players.find({ group: "admin" }),
    o = await players.find({ group: "owner" });

    res.render('team', { userCount: await players.countDocuments(), b: b, s: s, m: m, a: a, o: o });
});

router.get('/top', async (req, res) => {
    let FFAData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", kills: 0, deaths: 0 },
    GTCData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", won: 0 };

    // FFA
    let game = await playersFFA.find().sort({ kills: -1, deaths: 1 }).limit(1),
    player = await players.findOne({ UUID: game[0].UUID });

    if (game !== null && player !== null)
        FFAData = { uuid: game[0].UUID, username: player.username, kills: game[0].kills, deaths: game[0].deaths };

    // GTC
    game = await playersGTC.find().sort({ won: -1 }).limit(1);
    player = await players.findOne({ UUID: game[0].UUID });

    if (game !== null && player !== null)
        GTCData = { uuid: game[0].UUID, username: player.username, won: game[0].won };

    res.render('topPlayers', { userCount: await players.countDocuments(), ffa: FFAData, gtc: GTCData});
});

router.get('/donate', async (req, res) => {
    res.render('spenden', { userCount: await players.countDocuments() })
});

router.get('/help', async (req, res) => {
    res.render('help', { userCount: await players.countDocuments() })
});

router.get('/p/:username', async (req, res) => {
    const username = req.params.username,
    player = await players.findOne({ username: username });
    if (player === null)
        return res.render('profil', { userCount: await players.countDocuments(), error: 'notFound' });

    let FFAData = { kills: 0, deaths: 0 }, GTCData = { won: 0 };

    let game = await playersFFA.findOne({ UUID: player.UUID });
    if (game !== null)
        FFAData = { kills: game.kills, deaths: game.deaths };
    
    game = await playersGTC.findOne({ UUID: player.UUID });
    if (game !== null)
        GTCData = { won: game.won };
    
    const profilData = { uuid: player.UUID, username: username, banned: player.banned, group: player.group, ffa: FFAData, gtc: GTCData };

    res.render('profil', { userCount: await players.countDocuments(), profil: profilData });
});

module.exports = router;