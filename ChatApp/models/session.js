var mongoose = require('mongoose');
var Schema = mongoose.Schema
//MongoDB schema for Sessions

var SessionSchema=new Schema({
  name: String,
  admin : String,
  admin_name: String,
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
  files:[{
    name : String,
    file : Buffer,
    created_at:{
      type:Date,
      default: new Date()
    }
  }],
  created_at: {
      type: Date,
      default: new Date()
    }},{usePushEach:true}
 )

module.exports = mongoose.model("Session",SessionSchema);
