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
    .then((result) => modules.app.listen(80, () => {
        console.log(`Server gestartet!`);

        // Entfernt immer um **:**:59 die Gruppe 'premium' von den Spielern deren Mitgliedschaft abgelaufen ist und nicht auf dem Netzwerk Online sind
        modules.schedule.scheduleJob('59 * * * * *', async () => {
            const players = await modules.players.find({ group: 'premium', endOfPremium: {$lte: new Date().getTime() } });

            for (player of players) {
                if (!await modules.playersOnline.exists({ UUID: player.UUID })) {
                    await modules.players.updateOne({ UUID: player.UUID }, { group: 'default', endOfPremium: 0 })
                    .then((user) => console.log(`[PR][SUCCESS] Spieler ${player.UUID} verlor die Gruppe Premium!`))
                    .catch((err) => console.log(`[PR][FAILED] Spieler ${player.UUID} verlor nicht die Gruppe Premium!`));
                }
            }
        });
    }))
    .catch((err) => console.log(err));