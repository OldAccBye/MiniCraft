const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

const schema = new Schema({
    username: {
        type: String,
        required: true
    },
    UUID: {
        type: String,
        required: true
    },
    group: {
        type: String,
        required: true
    },
    premiumTimestamp: {
        type: Number,
        required: true
    },
    cookies: {
        type: Number,
        required: true
    },
    securitycode: {
        type: String,
        required: true
    },
    registrationTimestamp: {
        type: Number,
        required: true
    }
}), data = mongoose.model('player', schema);

module.exports = data;