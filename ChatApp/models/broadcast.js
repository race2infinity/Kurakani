var mongoose = require("mongoose");
//MongoDB schema for Broadcast
module.exports = mongoose.model("Broadcast", {
  sender: String,
  body: String,
  created_at: {
    type: Date,
    default: new Date()
  }
});
