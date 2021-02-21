const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

const schema = new Schema({
    UUID: {
        type: String,
        required: true
    },
    won: {
        type: Number,
        required: true
    }
}), data = mongoose.model('gtc', schema);

module.exports = data;