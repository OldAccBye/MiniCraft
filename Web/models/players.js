const mongoose = require('mongoose'), Schema = mongoose.Schema;

// Player schema
const playerSchema = new Schema({
    username: {
        type: String,
        required: true
    },
    UUID: {
        type: String,
        required: true
    },
    perm_group: {
        type: String,
        required: true
    },
    language: {
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
}), players = mongoose.model('players', playerSchema);

module.exports = players;