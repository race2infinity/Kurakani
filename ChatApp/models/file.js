const mongoose = require("mongoose");

//MongoDB schema for Messages

const FileSchema = new mongoose.Schema({
  sender: { type: String },
  sess_id: { type: String },
  filename:{type:String},
  data: { type: String },
  created_at: {
    type: Date,
    default: new Date()
  }
});
//Change
module.exports = mongoose.model("Files", FileSchema);
