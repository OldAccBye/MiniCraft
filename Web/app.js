const modules = require('./modules');

// Express settings
modules.app.set('view engine', 'ejs');
modules.app.set('views', modules.path.join(__dirname, 'views'));
modules.app.use(modules.express.static(__dirname + '/public'));
modules.app.use(modules.express.json());

// Router
modules.app.use('/', require('./routes/get'));
modules.app.use('/post', require('./routes/post'));
modules.app.use(async (req, res, next) => {
    if (res.status(404)) return res.render('error', { userCount: await modules.players.countDocuments(), title: '404', msg: 'Diese Seite existiert nicht!' });
    return res.render('error', { userCount: await modules.players.countDocuments(), title: 'Hoppla!', msg: 'Ein unbekannter Fehler ist aufgetreten!' });
});

// Connect to mongodb and start the server
modules.mongoose.connect('mongodb+srv://miniuser:minipass@minicraft.kxkh9.mongodb.net/MiniCraft?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true })
.then((result) => modules.app.listen(80, async () => {
    modules.app.locals.userCount = await modules.players.countDocuments();
    console.log(`Server gestartet!`);

    // Funktion wird unabhängig von Besuchern jede Minute in der Sekunde 59 ausgeführt
    modules.schedule.scheduleJob('59 * * * * *', async () => {
        // Entfernt allen Spieler die Gruppe Premium welche offline sind und deren Zeit abgelaufen ist
        const players = await modules.players.find({ group: 'premium', endOfPremium: {$lte: new Date().getTime() } });
        for (player of players) {
            if (await modules.playersOnline.exists({ UUID: player.UUID })) continue;
            await modules.players.updateOne({ UUID: player.UUID }, { group: 'default', endOfPremium: 0 })
            .then((user) => console.log(`[PR][\x1b[32mSUCCESS\x1b[0m] Spieler \x1b[33m${player.UUID}\x1b[0m verlor die Gruppe Premium!`))
            .catch((err) => console.log(`[PR][\x1b[31mFAILED\x1b[0m] Spieler \x1b[33m${player.UUID}\x1b[0m verlor nicht die Gruppe Premium!`));
        }

        modules.app.locals.userCount = await modules.players.countDocuments();
    });
}))
.catch((err) => console.log(err));