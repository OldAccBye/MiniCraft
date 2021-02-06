const path = require('path'), express = require('express'), app = express(), router = express.Router(), PORT = process.env.PORT || 80;
const mongoose = require('mongoose'), db = mongoose.connection;

// Models
const players = require('./models/players');
const playersFFA = require('./models/ffa');

// Connect to mongodb
mongoose.connect('mongodb+srv://miniuser:minipass@minicraft.kxkh9.mongodb.net/MiniCraft?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true })
    .then((result) => app.listen(PORT, () => console.log(`Server started on port ${PORT}`)))
    .catch((err) => console.log(err));

// Express settings
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));
app.use(express.static(__dirname + '/public'));

// Router get
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

router.get('/p/:username', async (req, res) => {
    const username = req.params.username;
    const player = await players.findOne({ username: username });
    if (player === null) {
        res.render('profil', { userCount: await players.countDocuments(), error: 'notFound' });
        return;
    }

    const playerFFA = await playersFFA.findOne({ UUID: player.UUID });
    if (playerFFA === null)
        return res.render('profil', { userCount: await players.countDocuments(), profil: { uuid: player.UUID, username: username, banned: player.banned, group: player.perm_group } });
    
    res.render('profil', { userCount: await players.countDocuments(), profil: { uuid: player.UUID, username: username, banned: player.banned, group: player.perm_group, ffa: { kills: playerFFA.kills, deaths: playerFFA.deaths } } });
});

// Extras
router.use(async (req, res, next) => {
    res.status(404).render('error', { userCount: await players.countDocuments(), title: '404', msg: 'Diese Seite existiert nicht!' });
});

app.use("/", router);