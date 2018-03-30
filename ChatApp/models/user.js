var mongoose = require('mongoose');

//MongoDB schema for users
module.exports = mongoose.model("User",{
  name: String,
  empid: String,
  emailid: String,
  mobile_no: String,
  location: String,
  department: String,
  aadhar: String,
  designation: String,
  password1:String,
  password2:String
});
