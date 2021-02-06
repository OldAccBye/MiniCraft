const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

// FFA schema
const ffaSchema = new Schema({
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
}), playersFFA = mongoose.model('ffa', ffaSchema);

module.exports = playersFFA;