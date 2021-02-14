const express = require('express'), router = express.Router(), players = require('../models/players'), playersFFA = require('../models/ffa');

router.get('/', async (req, res) => {
    res.render('index', { userCount: await players.countDocuments() });
});

router.get('/team', async (req, res) => {
    res.render('team', { userCount: await players.countDocuments() });
});

router.get('/top', async (req, res) => {
    const FFAPlayer = await playersFFA.find().sort({ kills: -1 }).limit(1);
    const player = await players.findOne({ UUID: FFAPlayer[0].UUID });

    if (FFAPlayer === null || player === null) {
        res.render('topPlayers', { userCount: await players.countDocuments() })
        return;
    }

    res.render('topPlayers', { userCount: await players.countDocuments(), ffa: { uuid: FFAPlayer[0].UUID, username: player.username, kills: FFAPlayer[0].kills, deaths: FFAPlayer[0].deaths }});
});

router.get('/canditature', async (req, res) => {
    res.render('bewerbung', { userCount: await players.countDocuments() })
});

router.get('/complaint', async (req, res) => {
    res.render('beschwerde', { userCount: await players.countDocuments() })
});

router.get('/about', async (req, res) => {
    res.render('über', { userCount: await players.countDocuments() })
});

router.get('/p/:username', async (req, res) => {
    const username = req.params.username;
    const player = await players.findOne({ username: username });
    if (player === null)
        return res.render('profil', { userCount: await players.countDocuments(), error: 'notFound' });

    const playerFFA = await playersFFA.findOne({ UUID: player.UUID });
    if (playerFFA === null)
        return res.render('profil', { userCount: await players.countDocuments(), profil: { uuid: player.UUID, username: username, banned: player.banned, group: player.perm_group } });
    
    const FFAData = { kills: playerFFA.kills, deaths: playerFFA.deaths };
    const profilData = { uuid: player.UUID, username: username, banned: player.banned, group: player.perm_group, ffa: FFAData };

    res.render('profil', { userCount: await players.countDocuments(), profil: profilData });
});

module.exports = router;