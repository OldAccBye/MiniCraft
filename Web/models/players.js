const mongoose = require('mongoose'), Schema = mongoose.Schema;

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
    endOfPremium: {
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
    banned: {
        type: Boolean,
        required: true
    },
    banSinceTimestamp: {
        type: Number,
        required: true
    },
    banExpiresTimestamp: {
        type: Number,
        required: true
    },
    banReason: {
        type: String,
        required: true
    },
    bannedFrom: {
        type: String,
        required: true
    }
}), data = mongoose.model('players', schema);

module.exports = data;