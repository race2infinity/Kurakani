var mongoose = require("mongoose");

//MongoDB schema for Messages
//Change
module.exports = mongoose.model("Messages", {
    sender: String,
    send_name: String,
    send_des: String,
    send_dep: String,
    sess_id:String,
    body: String,
    created_at: {
      type: Date,
      default: new Date()
  }
});
