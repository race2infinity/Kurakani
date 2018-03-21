const mongoose = require('mongoose')

const schema = mongoose.Schema({
  id: String,
  name: String,
  members: [{
      type: String
  }],
  invited: [{
      type: String
  }],
  created_at: {
      type: Date,
      default: Date.now()
  },

})

const Message = mongoose.model('Session', schema)

module.exports = { Session }
