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

//Body-parser Middleware
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

mongoose.Promise = Promise


//MongoDB schema for Messages
//Change
var Messages = mongoose.model("Messages", {
    sender: String,
    sess_id:String,
    body: String,
    created_at: {
      type: Date,
      default: new Date()
  }
})

//MongoDB schema for Super
//Change
var Super = mongoose.model("Super",{
  emailid:String,
  password:String
})

//MongoDB schema for Super
//Change
var Admin = mongoose.model("Admin",{
  emailid:String,
  password:String
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
  sid:String
})

//MongoDB schema for Sessions
var Session = mongoose.model("Session",{
  name: String,
  admin : String,
  Lastmessage: String,
  LastMT:Date,
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
//Changes
app.post("/messages", async (req, res) => {
    try {
        var message = new Messages(req.body)
        await message.save()
        Session.findByIdAndUpdate(message.sess_id,
           {$set:{Lastmessage:message.body,LastMT:message.created_at}},
           () => console.log('Last Message Removed')
          )
        res.sendStatus(200)
        //Emit the event
        io.emit("chat", req.body)
    } catch (error) {
        res.sendStatus(500)
        console.error(error)
    }
})
//192.168.0.5
//fetching messages from the database
//Changes
app.get("/messages/:seid", (req, res) => {
    var sid=req.params.seid
    Messages.find({sess_id:sid}, (error, chats) => {
        res.send(chats)
        console.log("Chats Accessed")
    })
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
    User.findOne({empid:user.empid},async(err,user1)=>{
      if(err){
        return err
      }
      if(!user1){
        await user.save()
        console.log("User Created")
        /*Department.findOne({id:user.department},(err,dep)=>{
        	Session.findByIdAndUpdate(
			dep.sid,
			{ $push: { members: user.empid } },
			() => console.log('User added')
		  );
        })*/
		
        res.sendStatus(200)
        //Emit the event
        io.emit("usercreated",req.body)
      }
      else{
        res.status(500).send({code:'AAE',message:'Account Already Exists'})
      }
    })

  }catch(error){
    res.sendStatus(500)
    console.error(error)
  }
})


//logging in to the application
app.post("/login/app", (req,res)=>{
  var id = req.body.id
  var pass = req.body.password
  console.log("Login Accessed")
  User.findOne({empid:id},(err,user)=>{
    if(err){
      return err
    }
    if(!user){
      res.status(500).send({code:"EII", message:"Employ ID Incorrect"})
      console.log("User does not exist")
    }
    else {
      if(pass==user.password){
        res.sendStatus(200)
        console.log("Right password")
      }
      else{
        res.status(500).send({code:"PII", message:"Password ID Incorrect"})
        console.log("Wrong password")
      }
    }
  })
})
//fetching data of user from id
app.get("/userdata/:id/", (req, res) => {
    var id = req.params.id
    User.find({empid:id},'-password -aadhar', (error, user) => {
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


//Creating a department
app.post("/depdata/",async(req,res)=>{
  try{
    var dep = new Department(req.body)
    Department.findOne({id:dep.id},async(err,dep1)=>{
      if(err){
        return err
      }
      if(!dep1){
        await dep.save()
        console.log("Department Created")
        var sess = new Session()
        sess.name=dep.name
        sess.admin=dep.admin
        sess.save()
        var x=sess._id
        console.log(dep.id)
        Department.findByIdAndUpdate(dep._id,
           {$set:{sid:sess._id}},
           () => console.log(sess._id)
          )
        res.sendStatus(200)
        //Emit the event
        io.emit("depcreated",req.body)
      }
      else{
        res.status(500).send({code:'DAE',message:'Department Already Exists'})
      }
    })

  }catch(error){
    res.sendStatus(500)
    console.error(error)
  }
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
