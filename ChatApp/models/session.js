var mongoose = require('mongoose');

//MongoDB schema for Sessions
module.exports = mongoose.model("Session",{
  name: String,
  admin : String,
  Lastmessage: String,
  LastMT:{
    type:Date,
    default:new Date()
  },
  members: [{
    type: String
  }],
  invited: [{
    type: String
  }],
  created_at: {
    type: Date,
    default: new Date()
  }
});
