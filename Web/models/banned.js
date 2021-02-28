const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

const schema = new Schema({
    UUID: {
        type: String,
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
}), data = mongoose.model('banned', schema);

module.exports = data;