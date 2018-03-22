var express = require("express")
var mongoose = require("mongoose")
var bodyParser = require("body-parser")

var app = express()
var http = require("http").Server(app)
var io = require("socket.io")(http)

var conString = "mongodb://localhost:27017/mylearning"
//var conString = "mongodb://localhost:27017/mylearning";
app.use(express.static(__dirname))
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

mongoose.Promise = Promise

var Chats = mongoose.model("Chats", {
    name: String,
    chat: String,
    created_at: {
      type: Date,
      default: Date.now()
  }
})

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
var Department = mongoose.model("Department",{
  id: String,
  name: String,
  location:String,
  admin: String,
  employ: [{
      type: String
  }],
})

mongoose.connect(conString, { useMongoClient: true }, (err) => {
    console.log("Database connection", err)
})

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

app.get("/chats", (req, res) => {
    Chats.find({}, (error, chats) => {
        res.send(chats)
        console.log("App has Crashed....")
    })
})
app.get("/userdata/:id", (req, res) => {
    var id = req.params.id
   // res.send(id)
    User.find({empid:id}, (error, user) => {
        //res.json(user)
        //res.send(id)
        res.send(user)
        console.log("Users Accesed")
    })
})
app.get("/depdata",(req,res)=>{
    Department.find({},(error,dep)=>{
      res.send(dep)
      console.log("Departments Accessed")
    })
})
app.get("/depdata/:id",(req,res)=>{
  var id=req.params.id
  User.find({department:id},(error,user)=>{
      res.send(user)
      console.log("Department User Accessed")
  })
})
io.on("connection", (socket) => {
    console.log("Socket is connected...")
})

var server = http.listen(3020, () => {
    console.log("Well done, now I am listening on ", server.address().port)
})
