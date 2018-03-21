const mongoose = require('mongoose')

const schema = mongoose.Schema({
  name: String,
  empid: String,
  emailid: String,
  mobile_no: String,
  location: String,
  department: String,
  aadhar: String,
  designation: String,
  password:String,
  created_at: {
      type: Date,
      default: Date.now()
  },
  updated_at: {
      type: Date,
      default: Date.now()
  }
})

const User = mongoose.model('User', schema)

module.exports = { User }
