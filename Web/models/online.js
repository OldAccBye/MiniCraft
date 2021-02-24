const mongoose = require('mongoose'), Schema = mongoose.Schema;
mongoose.pluralize(null);

const schema = new Schema({
    UUID: {
        type: String,
        required: true
    }
}), data = mongoose.model('online', schema);

module.exports = data;