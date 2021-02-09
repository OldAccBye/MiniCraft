const path = require('path'), express = require('express'), app = express(), PORT = process.env.PORT || 80;
const mongoose = require('mongoose');
const getRouter = require('./routes/get');

// Connect to mongodb
mongoose.connect('mongodb+srv://miniuser:minipass@minicraft.kxkh9.mongodb.net/MiniCraft?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true })
    .then((result) => app.listen(PORT, () => console.log(`Server started on port ${PORT}`)))
    .catch((err) => console.log(err));

// Express settings
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));
app.use(express.static(__dirname + '/public'));

// Router
app.use("/", getRouter);