var mongoose = require('mongoose');

//MongoDB schema for Super
//Change
module.exports = mongoose.model("Admin",{
  empid:String,
  password:String
});
