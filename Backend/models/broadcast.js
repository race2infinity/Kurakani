var mongoose = require("mongoose");
//MongoDB schema for Broadcast
module.exports = mongoose.model("Broadcast", {
  sender: String,
  send_name: String,
  send_des: String,
  send_dep: String,
  body: String,
  created_at: {
    type: Date,
    default: new Date()
  }
});
