const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

const schema = new Schema({
    UUID: {
        type: String,
        required: true
    },
    kills: {
        type: Number,
        required: true
    },
    deaths: {
        type: Number,
        required: true
    }
}), data = mongoose.model('ffa', schema);

module.exports = data;