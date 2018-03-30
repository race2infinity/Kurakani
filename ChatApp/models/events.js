var mongoose = require('mongoose');
var Schema = mongoose.Schema
//MongoDB schema for Sessions

var EventSchema=new Schema({
  name: String,
  venue:String,
  creator : String,
  creat_name: String,
  members: [{
      type: String
  }],
  invited: [{
      type: String
  }],
  starts_at:{
    type : Date
  },
  ends_at:{
    type:Date
  },
  created_at: {
      type: Date,
      default: new Date()
    }},
    {usePushEach:true}
 )

module.exports = mongoose.model("Events",EventSchema);
