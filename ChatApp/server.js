var express = require("express")
var mongoose = require("mongoose")
var bodyParser = require("body-parser")
var AutoIncrement=require('mongoose-sequence')(mongoose);
var app = express()
var expressValidator = require('express-validator');
var http = require("http").Server(app)
var io = require("socket.io")(http)
var path = require('path')
var session = require('express-session')
var passport = require('passport')
var LocalStrategy = require('passport-local').Strategy
var flash = require('connect-flash')
const SocketIOFile = require('socket.io-file')

var conString = "mongodb://localhost:27017/mylearning";
//var conString = "mongodb://localhost:27017/mylearning";
//app.use(express.static(__dirname))

var routes = require('./routes/index')
var users = require('./routes/users')
var server = require('./server')

var User = require('./models/user');
var Department = require('./models/department');
var Session = require('./models/session');
var Messages = require('./models/message');
var Broadcast = require('./models/broadcast');

//view engine
app.set('views', path.join(__dirname, 'views'))
app.set('view engine', 'ejs')

//Set static folder
app.use(express.static(path.join(__dirname,'public')));
app.use('/css', express.static(__dirname+ '/node_modules/bootstrap/dist/css'))

//Body-parser Middleware
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))

//Express session Middleware
app.use(session({
  secret:'secret',
  saveUninitialized: true,
  resave: true
}))

//Passport Middleware
app.use(passport.initialize())
app.use(passport.session())

mongoose.Promise = Promise

//Express Vvlidator Middleware
app.use(expressValidator({
    errorFormatter: function(param, msg, value) {
        var namespace = param.split('.'),
        root = namespace.shift(),
        formParam = root;

        while(namespace.length) {
            formParam += '[' + namespace.shift() + ']';
        }

        return {
            parma : formParam,
            msg : msg,
            value : value
        };
    }
}));

//Connect-Flash middleware
app.use(flash());
app.use(function(req, res, next) {
    res.locals.messages = require('express-messages')(req, res);
    next();
});

app.get('*', function(req, res, next) {
    res.locals.user = req.user || null;
    next();
});

//Connecting to the database
mongoose.connect(conString, { useMongoClient: true }, (err) => {
    console.log("Database connection", err)
})

//Saving chats in the database
//Changes
app.post("/messages", async (req, res) => {
    try {
        var name,des,dep;
        var message = new Messages(req.body);
        var d = new Date()
        var mum_offset = 5.5*60;
        d.setMinutes(d.getMinutes() + mum_offset);
        message.created_at=d;
        //message.send_name="Chaitanya";
        //console.log(message.sender)
        User.findOne({empid:message.sender},(err,user)=>{
           message.send_name =user.name;
          //console.log(message.send_name)
           message.send_des =user.designation;
          // console.log(message.send_des)
          Department.findOne({id:user.department},(err,dep)=>{
             message.send_dep =dep.name;
             //console.log("1"+message.send_dep)
             var me = new Messages(message)
             me.save()
             console.log(me);
             io.emit("chat",me);
          })
        })
        //  console.log(message)
        await message.save();
        //console.log(message.send_dep)

        //await message.save();

        Session.findByIdAndUpdate(message.sess_id,
        { $set  : { Lastmessage : message.body, LastMT  : d}},
           () => console.log("Last Message Added")
        );
        //console.log(message)
        res.sendStatus(200);
          //Emit the event
          io.emit("refresh");
    } catch (error) {
        res.sendStatus(500);
        console.error(error);
    }
})

app.post("/file",(req,res)=>
{
    var uploader = new SocketIOFile(socket,{
      uploadDir:'../',
      maxFileSize:5242880,
      chunkSize:51200,
      accepts:['image/jpeg', 'image/png'],
      transmissionDelay:0,
      overwrite:true
    });
    uploader.on('start',(fileInfo)=>{
        console.log('Start uploading');
        console.log(fileInfo);
    });
    uploader.on('stream', (fileInfo) => {
        console.log(`${fileInfo.wrote} / ${fileInfo.size} byte(s)`);
    });
    uploader.on('complete', (fileInfo) => {
        console.log('Upload Complete.');
        console.log(fileInfo);
    });
    uploader.on('error', (err) => {
        console.log('Error!', err);
    });
    uploader.on('abort', (fileInfo) => {
        console.log('Aborted: ', fileInfo);
    });

});

//fetching messages from the database
//Changes
app.get("/messages/:seid", (req, res) => {
    var sid=req.params.seid;
    Messages.find({sess_id:sid}, (error, chats) => {
        res.send(chats)
        console.log("Chats Accessed");
    });
});

//creating  a new Sessions
app.post("/newsession",async(req,res)=>{
  try{
    var session = new Session(req.body);
    var d = new Date()
    var mum_offset = 5.5*60;
    d.setMinutes(d.getMinutes() + mum_offset);
    session.created_at= d;
    session.LastMT=d;
    User.findOne({empid:session.admin},(err,user)=>{
      session.admin_name=user.name;
      var se = new Session(session);
      se.save();
    })
    await session.save();
    console.log("Session Created");
    console.log(req.body)
    res.sendStatus(200);
    //Emit the event
    //io.emit("sessioncreate",req.body);
  }catch(error){
      res.sendStatus(500);
      console.error(error);
  }
})

//fetching broadcast from the database
app.get("/broadcast", (req, res) => {
    Broadcast.find({}, (error, bcast) => {
        res.send(bcast)
        console.log("Broadcast Accessed")
    })
})

//Saving broadcast in the database
app.post("/broadcast", async (req, res) => {
    try {
    console.log(req.body)
        var bcast = new Broadcast(req.body)
        var d = new Date()
        var mum_offset = 5.5*60;
        d.setMinutes(d.getMinutes() + mum_offset);
        bcast.created_at=d;
        User.findOne({empid:bcast.sender},(err,user)=>{
           bcast.send_name =user.name;
           bcast.send_des =user.designation;
          Department.findOne({id:user.department},(err,dep)=>{
             bcast.send_dep =dep.name;
             var bc = new Broadcast(bcast)
             bc.save()
             console.log(bc);
             //Emit the event
             io.emit("broadcast", bc)
             res.sendStatus(200)
          })
        })
        await bcast.save()
        //res.sendStatus(200)

    } catch (error) {
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
        if(user.password1==user.password2){
          Department.findOne({id:user.department},(err,depart)=>{
            if(err){
              return err
            }
            else {
                user.dname=depart.name
                user.markModified('dname')
                user.save()
            }
          })
        await user.save()
        console.log("User Created")
        Department.findOne({id:user.department},(err,dep)=>{
        	Session.findByIdAndUpdate(
			dep.sid,
			{ $push: { members: user.empid } },

			() => console.log('User added')
		  );
        })
        res.sendStatus(200)
        //Emit the event
        io.emit("usercreated",req.body)
        }
        else{
          res.status(500).send({code:'PDM',message:'Password Doesnt Match'})
        }
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
      console.log(pass)
      if(pass==user.password1){
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
    User.findOne({empid:id},'-password1 -password2 -aadhar', (error, user) => {
        res.send(user)
        console.log("Users Accesed")
    })
})

//fetching data of departments
app.get("/depdata",(req,res)=>{
  console.log(req.headers)
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
        sess.created_at=new Date()
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

//inviting to an event
app.post("/events",)

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

//session profile
app.get("/sessions/:sid",(req,res)=>
{
    var sid = req.params.sid;
    console.log(sid);
    Session.findOne({_id:sid},(err,ses)=>
  {
    User.find({empid:{$in:ses.members}},'-mobile_no -emailid -_id -__v -location -password1 -password2 -aadhar',(err,users)=>{
    	const result={
    		name:ses.name,
    		emps:users
    	}
    	return res.send(result)
    })
    console.log("Sessions Data Accessed");
  })
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
  User.findByIdAndUpdate(

  )
})
//creating a socket connection
io.on("connection", (socket) => {
    console.log("Socket is connected...")

})





//adding to a session
app.post("/addsession/",(req,res)=>{
  var sid = req.body.sid;
  var empid = req.body.id;
  var i =0;
  Session.findById(sid, function(err, session){
    if(err)
    {
      res.sendStatus(400)
    }
    empid.forEach(function(id){
      session.invited.push(id);
    });
    console.log("new value: "+session.invited)
      session.markModified('invited')
      session.save();
  })
  res.sendStatus(200)
})

//leaving a session
app.post("/leavesession/",(req,res)=>{
  var sid = req.body.sid;
  var empid = req.body.id;
  Session.findOne({_id:sid},(err,sa)=>{
    if(err)
      return console.log(err);
    else {
      console.log(sa.admin)
      if(sa.admin==empid){

        console.log("Admin : "+ sa.admin)
        Session.findByIdAndUpdate(
          sid,
          { $pull : { members : empid } },
          ()=>console.log("Admin left session")
        );
        /*Session.findByIdAndUpdate(
          sid,
        { $set : { admin : $arrayElemAt : [ "$members", 1 ] } },
        ()=>console.log("Admin Replaced")
      );*/
      }
      else {
        {
          Session.findByIdAndUpdate(
            sid,
            { $pull : { members : empid } },
            ()=>console.log("User left session")
          );
        }
      }
      res.sendStatus(200)
    }
  })
})

//deleting a session
app.post("/deletesession/",(req,res)=>{
  var sid = req.body.sid;
  Session.remove({_id:sid},function(err){
    if(!err)
    {
      console.log("Session Deleted");
      res.sendStatus(200);
    }
    else {
      {
        console.error(err);
      }
    }
  })

})

//Define routes
app.use('/',routes)
app.use('/users', users)

//creating a server
var server = http.listen(3020, () => {
    console.log(new Date())
    console.log("Well done, now I am listening on ", server.address().port)
})
