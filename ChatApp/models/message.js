'use strict';

const mongoose = require("mongoose");
const mongooseToCsv = require('mongoose-to-csv');

//MongoDB schema for Messages

const MessageSchema = new mongoose.Schema({
  sender: { type: String },
  send_name: { type: String },
  send_des: { type: String },
  send_dep: { type: String },
  sess_id: { type: String },
  body: { type: String },
  created_at: {
    type: Date,
    default: new Date()
  }
});

MessageSchema.plugin(mongooseToCsv, {
  headers: 'EmpId SenderName Designation Department SessionId Body CreatedAt',
  constraints: {
    'EmpId': 'sender',
    'SenderName': 'send_name',
    'Designation': 'send_des',
    'Department': 'send_dep',
    'SessionId': 'sess_id',
    'Body': 'body',
    'CreatedAt': 'created_at'
  },
  virtuals: {
    'CreatedAt': function (doc) {
      return new Date(doc.created_at);
    }
  }
})
//Change
module.exports = mongoose.model("Messages", MessageSchema);
