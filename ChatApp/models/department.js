var mongoose = require('mongoose');

//MongoDB schema for Departments
module.exports = mongoose.model("Department",{
  id: String,
  name: String,
  location:String,
  admin: String,
  sid:String
});
