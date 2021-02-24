const modules = require('../modules');

modules.router.get('/', async (req, res) => {
    res.render('index', { userCount: await modules.players.countDocuments() });
});

modules.router.get('/team', async (req, res) => {
    const b = await modules.players.find({ group: "builder" }, 'UUID username'),
    s = await modules.players.find({ group: "supporter" }, 'UUID username'),
    m = await modules.players.find({ group: "moderator" }, 'UUID username'),
    a = await modules.players.find({ group: "admin" }, 'UUID username'),
    o = await modules.players.find({ group: "owner" }, 'UUID username');

    res.render('team', { userCount: await modules.players.countDocuments(), b: b, s: s, m: m, a: a, o: o });
});

modules.router.get('/top', async (req, res) => {
    let FFAData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", kills: 0, deaths: 0 },
    GTCData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", won: 0 };

    // FFA
    let game = await modules.playersFFA.find().sort({ kills: -1, deaths: 1 }).limit(1),
    player = await modules.players.findOne({ UUID: game[0].UUID }, 'username');

    if (game !== null && player !== null)
        FFAData = { uuid: game[0].UUID, username: player.username, kills: game[0].kills, deaths: game[0].deaths };

    // GTC
    game = await modules.playersGTC.find().sort({ won: -1 }).limit(1);
    player = await modules.players.findOne({ UUID: game[0].UUID }, 'username');

    if (game !== null && player !== null)
        GTCData = { uuid: game[0].UUID, username: player.username, won: game[0].won };

    res.render('topPlayers', { userCount: await modules.players.countDocuments(), ffa: FFAData, gtc: GTCData});
});

modules.router.get('/donate', async (req, res) => {
    res.render('donate', { userCount: await modules.players.countDocuments() })
});

modules.router.get('/premium', async (req, res) => {
    res.render('premium', { userCount: await modules.players.countDocuments() })
});

modules.router.get('/uuid', async (req, res) => {
    res.render('uuid', { userCount: await modules.players.countDocuments() })
});

modules.router.get('/help', async (req, res) => {
    res.render('help', { userCount: await modules.players.countDocuments() })
});

modules.router.get('/p/:uuid', async (req, res) => {
    const player = await modules.players.findOne({ UUID: req.params.uuid }, 'username banned group');
    if (player === null)
        return res.render('profil', { userCount: await modules.players.countDocuments(), error: 'notFound' });

    let FFAData = { kills: 0, deaths: 0 }, GTCData = { won: 0 }, lastNameList = [{ name: "Fehler" }], onlineStatus = "offline";

    let game = await modules.playersFFA.findOne({ UUID: req.params.uuid }, 'kills deaths');
    if (game !== null)
        FFAData = game;
    
    game = await modules.playersGTC.findOne({ UUID: req.params.uuid }, 'won');
    if (game !== null)
        GTCData = game;
    
    const response = await modules.fetch(`https://api.mojang.com/user/profiles/${req.params.uuid}/names`);
    if (response.status === 200) {
        const data = await response.json();
        if (!data.error)
            lastNameList = data;
    }
    
    if (await modules.playersOnline.exists({ UUID: req.params.uuid }))
        onlineStatus = "online";

    const profilData = { uuid: req.params.uuid,
        username: player.username,
        banned: player.banned,
        group: player.group,
        ffa: FFAData,
        gtc: GTCData,
        lastNameList: lastNameList,
        onlineStatus: onlineStatus };

    res.render('profil', { userCount: await modules.players.countDocuments(), profil: profilData });
});

module.exports = modules.router;