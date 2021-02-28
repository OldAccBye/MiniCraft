const modules = require('../modules'), functions = require('../functions');

modules.router.get('/', async (req, res) => {
    res.render('index');
});

modules.router.get('/team', async (req, res) => {
    const b = await modules.player.find({ group: "builder" }, 'UUID username'),
    s = await modules.player.find({ group: "supporter" }, 'UUID username'),
    m = await modules.player.find({ group: "moderator" }, 'UUID username'),
    a = await modules.player.find({ group: "admin" }, 'UUID username'),
    o = await modules.player.find({ group: "owner" }, 'UUID username');

    res.render('team', { b: b, s: s, m: m, a: a, o: o });
});

modules.router.get('/top', async (req, res) => {
    let FFAData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", kills: 0, deaths: 0 },
    GTCData = { uuid: "fc6d4376-a67f-45e8-a46a-96a3c375", username: "none", won: 0 };

    // FFA
    let game = await modules.playerFFA.find().sort({ kills: -1, deaths: 1 }).limit(1),
    player = await modules.player.findOne({ UUID: game[0].UUID }, 'username');

    if (game !== null && player !== null)
        FFAData = { uuid: game[0].UUID, username: player.username, kills: game[0].kills, deaths: game[0].deaths };

    // GTC
    game = await modules.playerGTC.find().sort({ won: -1 }).limit(1);
    player = await modules.player.findOne({ UUID: game[0].UUID }, 'username');

    if (game !== null && player !== null)
        GTCData = { uuid: game[0].UUID, username: player.username, won: game[0].won };

    res.render('topPlayers', { ffa: FFAData, gtc: GTCData});
});

modules.router.get('/donate', async (req, res) => {
    res.render('donate');
});

modules.router.get('/premium', async (req, res) => {
    res.render('premium');
});

modules.router.get('/uuid', async (req, res) => {
    res.render('uuid');
});

modules.router.get('/help', async (req, res) => {
    res.render('help');
});

modules.router.get('/p/:uuid', async (req, res) => {
    const player = await modules.player.findOne({ UUID: req.params.uuid }, 'username group registrationTimestamp');
    if (!player)
        return res.render('profile', { error: 'notFound' });
    
    let FFAData = { kills: 0, deaths: 0 },
    GTCData = { won: 0 },
    lastNameList = [{ name: "Fehler" }];

    const nameHistory = await functions.getNameHistory(req.params.uuid);
    if (!!nameHistory) {
        lastNameList = nameHistory;
        const lastName = lastNameList[lastNameList.length - 1].name;
    
        if (lastName !== player.username) {
            player.username = lastName;
            player.save();
        }
    }

    let game = await modules.playerFFA.findOne({ UUID: req.params.uuid }, 'kills deaths');
    if (!!game)
        FFAData = game;
    
    game = await modules.playerGTC.findOne({ UUID: req.params.uuid }, 'won');
    if (!!game)
        GTCData = game;

    const profileData = { uuid: req.params.uuid,
        username: player.username,
        banned: await modules.banned.exists({ UUID: req.params.uuid }) ? true : false,
        group: player.group,
        registrationTimestamp: player.registrationTimestamp,
        ffa: FFAData,
        gtc: GTCData,
        lastNameList: lastNameList,
        onlineStatus: await modules.playerOnline.exists({ UUID: req.params.uuid }) ? "online" : "offline" };

    res.render('profile', { profile: profileData });
});

module.exports = modules.router;