var mongoose = require('mongoose');

//MongoDB schema for Super
//Change
module.exports = mongoose.model("Super",{
  empid:String,
  password:String
});
