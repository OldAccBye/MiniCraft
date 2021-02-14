const path = require('path'), express = require('express'), app = express(), mongoose = require('mongoose'), players = require('./models/players');

// Express settings
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(__dirname + '/public'));
app.use(express.json());

// Router
app.use('/', require('./routes/get'));
app.use('/posts', require('./routes/post'));

app.use(async (req, res, next) => {
    if (res.status(404)) return res.render('error', { userCount: await players.countDocuments(), title: '404', msg: 'Diese Seite existiert nicht!' });
    res.render('error', { userCount: await players.countDocuments(), title: '?', msg: 'Ein unbekannter Fehler ist aufgetreten!' });
});

// Connect to mongodb and start the server
mongoose.connect('mongodb+srv://miniuser:minipass@minicraft.kxkh9.mongodb.net/MiniCraft?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true })
    .then((result) => app.listen(80, () => console.log(`Server started!`)))
    .catch((err) => console.log(err));