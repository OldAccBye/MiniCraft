const modules = require('../modules'), functions = require('../functions');

modules.router.post('/getUUID', async (req, res) => {
    if (!req.body.username)
        return res.json({ message: '<div class="alert alert-danger text-center mt-3 mb-0"><p class="mb-0">Bitte gib einen Benutzernamen ein!</p></div>' });
    
    let uuidMessage;

    const getUUID = await functions.getUUIDFromUsername(req.body.username);
    if (!getUUID)
        uuidMessage = '<div class="alert alert-danger text-center mt-3 mb-0"><p class="mb-0">Spieler nicht gefunden!</p></div>';
    else if (await modules.players.exists({ UUID: getUUID }))
        uuidMessage = `<div class="alert alert-success text-center mt-3 mb-0"><h3>${getUUID}</h3><a class="mb-0" href="/p/${getUUID}">Profil anzeigen</a></div>`;
    else
        uuidMessage = `<div class="alert alert-success text-center mt-3 mb-0"><h3 class="mb-0">${getUUID}</h3></div>`;
    
    res.json({ message: uuidMessage });
});

modules.router.post('/buyPremium', async (req, res) => {
    if (!req.body.uuid)
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Bitte gib deine UUID von Minecraft an!</p></div>' });
    else if (!req.body.securitycode)
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Bitte gib deinen Sicherheitscode ein!</p></div>' });
    else if (!req.body.endOfPremium)
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Bitte wähle die Dauer deiner Mitgliedschaft!</p></div>' });

    var d = new Date(), price = 0;
    switch (req.body.endOfPremium) {
        case "1": {
            d.setMonth(d.getMonth() + 1);
            price = 950;
            break;
        }
        case "2": {
            d.setMonth(d.getMonth() + 2);
            price = 1750;
            break;
        }
        case "3": {
            d.setMonth(d.getMonth() + 3);
            price = 2500;
            break;
        }
        default:
            return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Ein unbekannter Fehler ist aufgetreten!</p></div>' });   
    }

    const player = await modules.players.findOne({ UUID: req.body.uuid });
    if (player === null)
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Du bist nicht auf unserem Netzwerk registriert!</p></div>' });
    else if (player.group !== 'default')
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Du gehörst bereits zu einer Gruppe mit den selben Funktionen wie die Gruppe "Premium"!</p></div>' });
    else if (player.cookies < price)
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Du besitzt nicht genügend Cookies!</p></div>' });
    
    if (await modules.playersOnline.exists({ UUID: req.body.uuid }))
        return res.json({ message: '<div class="alert alert-danger"><h5 class="alert-heading">Hoppla!</h5><p class="mb-0">Du darfst dich dafür aktuell nicht auf dem Netzwerk befinden!</p></div>' });
    
    player.cookies -= price;
    player.group = "premium";
    player.endOfPremium = d.getTime();
    await player.save();
    res.json({ message: '<div class="alert alert-success"><h5 class="alert-heading">Klasse!</h5><p class="mb-0">Du gehörst nun zur Gruppe Premium!</p></div>' });
});

module.exports = modules.router;