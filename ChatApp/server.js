var express = require("express")
var mongoose = require("mongoose")
var bodyParser = require("body-parser")
var AutoIncrement=require('mongoose-sequence')(mongoose);
var app = express()
var http = require("http").Server(app)
var io = require("socket.io")(http)

var conString = "mongodb://localhost:27017/mylearning";
//var conString = "mongodb://localhost:27017/mylearning";
app.use(express.static(__dirname))
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

mongoose.Promise = Promise


//MongoDB schema for Chats
var Chats = mongoose.model("Chats", {
    name: String,
    chat: String,
    created_at: {
      type: Date,
      default: Date.now()
  }
})


var Super = mongoose.model("Super",{
  empid:String
})

//MongoDB schema for users
var User = mongoose.model("User",{
  name: String,
  empid: String,
  emailid: String,
  mobile_no: String,
  location: String,
  department: String,
  aadhar: String,
  designation: String,
  password:String,
})

//MongoDB schema for Departments
var Department = mongoose.model("Department",{
  id: String,
  name: String,
  location:String,
  admin: String,
})

//MongoDB schema for Sessions
var Session = mongoose.model("Session",{
  name: String,
  admin : String,
  members: [{
      type: String
  }],
  invited: [{
      type: String
  }],
  created_at: {
      type: Date,
      default: new Date()
    }});

//Connecting to the database
mongoose.connect(conString, { useMongoClient: true }, (err) => {
    console.log("Database connection", err)
})

//Saving chats in the database
app.post("/chats", async (req, res) => {
    try {
        var chat = new Chats(req.body)
        await chat.save()
        res.sendStatus(200)
        //Emit the event
        io.emit("chat", req.body)
    } catch (error) {
        res.sendStatus(500)
        console.error(error)
    }
})

//creating  a new Sessions
app.post("/newsession",async(req,res)=>{
  try{
    var session = new Session(req.body)
    await session.save()
    console.log("Session Created");
    res.sendStatus(200)
    //Emit the event
    io.emit("sessioncreate",req.body)
  }catch(error){
      res.sendStatus(500)
      console.error(error)
  }
})

//registering a new user
app.post("/userdata/",async(req,res)=>{
  try{
    var user = new User(req.body)
    await user.save()
    console.log("User created");
    res.sendStatus(200)
    //Emit the event
    io.emit("usercreated",req.body)
  }catch(error){
    res.sendStatus(500)
    console.error(error)
  }
})

//fetching messages from the database
app.get("/chats", (req, res) => {
    Chats.find({}, (error, chats) => {
        res.send(chats)
        console.log("Chats Accessed")
    })
})

//fetching data of user from id
app.get("/userdata/:id/", (req, res) => {
    var id = req.params.id
   // res.send(id)
    User.find({empid:id},'-password -aadhar', (error, user) => {
        //res.json(user)
        //res.send(id)
        res.send(user)
        console.log("Users Accesed")
    })

})

//fetching data of departments
app.get("/depdata",(req,res)=>{
    Department.find({},(error,dep)=>{
      res.send(dep)
      console.log("Departments Accessed")
    })
})

//fetching employees from a department
app.get("/depdata/:id",(req,res)=>{
  var id=req.params.id
  User.find({department:id},(error,user)=>{
      res.send(user)
      console.log("Department User Accessed")
  })
})

//find sessions the employee is invited to
app.get("/findinvites/:id",(req,res)=>{
  var id=req.params.id
  Session.find({invited:id},(error,session)=>{
    res.send(session)
    console.log("Session Invites Accessed")
  })
})

//find sessions the employee is a member of
app.get("/findsessions/:id",(req,res)=>{
  var id = req.params.id
  Session.find({members:id},(error,session)=>{
    res.send(session)
    console.log("Session members Accesed")
  })
})

//declining a session
app.post("/sessions/no",(req,res)=>{
  const sid = req.body.sid;
  const id = req.body.id;
  console.log(id,sid, req.body);
  Session.findByIdAndUpdate(
    sid,
    { $pull: { invited: id } },
    () => console.log('User removed')
  );
})

//accepting a session
app.post("/sessions/yes",(req,res)=>
{
  var sid = req.body.sid;
  var id=req.body.id;
  Session.findByIdAndUpdate(
    sid,
    { $pull: { invited: id } },
    () => console.log('User removed')
  );
  Session.findByIdAndUpdate(
    sid,
    { $push: { members: id } },
    () => console.log('User added')
  );
})
//creating a socket connection
io.on("connection", (socket) => {
    console.log("Socket is connected...")
})

//creating a server
var server = http.listen(3020, () => {
    console.log(new Date())
    console.log("Well done, now I am listening on ", server.address().port)
})
