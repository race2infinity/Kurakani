const mongoose = require('mongoose')

const schema = mongoose.Schema({
  from: String,
  sess_id: String,
  body: {
     text: String,
  },
  created_at: {
      type: Date,
      default: Date.now()
  },

})

const Message = mongoose.model('Message', schema)

module.exports = { Message }
