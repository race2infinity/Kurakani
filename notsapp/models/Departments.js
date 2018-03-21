const mongoose = require('mongoose')

const schema = mongoose.Schema({
  id: String,
  name: String,
  location:String,
  admin: String,
  employ: [{
      type: String
  }],
})

const User = mongoose.model('Dep', schema)

module.exports = { Dep }
