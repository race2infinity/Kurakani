var express = require("express");
var mongoose = require("mongoose");
var bodyParser = require("body-parser");
var AutoIncrement=require('mongoose-sequence')(mongoose);
var app = express();
var expressValidator = require('express-validator');
var http = require("http").Server(app);
var io = require("socket.io")(http);
var path = require('path');
var session = require('express-session');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var flash = require('connect-flash');
var crypto = require('crypto'),
    algorithm = 'aes-256-ctr',
    password = 'd6F3Efeq';

function encrypt(text){
  var cipher = crypto.createCipher(algorithm,password)
  var crypted = cipher.update(text,'utf8','hex')
  crypted += cipher.final('hex');
  return crypted;
}

function decrypt(text){
  var decipher = crypto.createDecipher(algorithm,password)
  var dec = decipher.update(text,'hex','utf8')
  dec += decipher.final('utf8');
  return dec;
}
// var Department = require('../models/depar'ment')
var conString = "mongodb://localhost:27017/mylearning";
//var conString = "mongodb://localhost:27017/mylearning";
//app.use(express.static(__dirname))

var routes = require('./routes/index');
var users = require('./routes/users');
var server = require('./server');
var fs = require('fs');

var User = require('./models/user');
var Department = require('./models/department');
var Session = require('./models/session');
var Messages = require('./models/message');
var Broadcast = require('./models/broadcast');

var Events = require('./models/events');
var Files= require('./models/file');
//view engine
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

//Set static folder
app.use(express.static(path.join(__dirname,'public')));
app.use('/css', express.static(__dirname+ '/node_modules/bootstrap/dist/css'));

//Body-parser Middleware
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));

//Express session Middleware
app.use(session({
  secret:'secret',
  saveUninitialized: true,
  resave: true
}));

//Passport Middleware
app.use(passport.initialize());
app.use(passport.session());

mongoose.Promise = Promise;

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
    console.log("Database connection", err);
});

//Saving chats in the database
//Changes
app.post("/messages", async (req, res) => {
    try {
        var name,des,dep;
        var x = req.body;
        x.body=encrypt(x.body)
        var message = new Messages();
        message.sender = x.sender;
        message.body = x.body;
        message.sess_id=x.sess_id;
        console.log(message.body);
        var d = new Date();
        var mum_offset = 5.5*60;
        d.setMinutes(d.getMinutes() + mum_offset);
        message.created_at=d;
        x.created_at=d;
        //message.send_name="Chaitanya";
        //console.log(message.sender)
        User.findOne({empid:message.sender},(err,user)=>{
           message.send_name =user.name;
           x.send_name=user.name;
          //console.log(message.send_name)
           message.send_des =user.designation;
           x.send_des =user.designation;
          // console.log(message.send_des)
          Department.findOne({id:user.department},(err,dep)=>{
             message.send_dep =dep.name;
             x.send_dep =dep.name;
             //console.log("1"+message.send_dep)
             var me = new Messages(message);
             me.save();
             console.log(me);
             x.body=decrypt(x.body);
             console.log(x)
             io.emit("chat",x);
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
          //io.emit("refresh");
    } catch (error) {
        res.sendStatus(500);
        console.error(error);
    }
})


//fetching messages from the database
//Changes
app.get("/messages/:seid", (req, res) => {
    var sid=req.params.seid;
    Messages.find({sess_id:sid}, (error, chats) => {
      chats.forEach(function(mes){
        mes.body=decrypt(mes.body)
      })
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

function encryptPassword (password, salt) {
  console.log(password, salt)
  if (!password || !salt) return ''
  var saltgen = new Buffer(salt, 'base64')
  return crypto.pbkdf2Sync(password, saltgen, 10000, 64, 'sha512').toString('base64')
}

function makeSalt() {
  return crypto.randomBytes(16).toString('base64')
}

//registering a new user
app.post("/userdata/",async(req,res)=>{
  try{
    var data = req.body
    data.salt =  makeSalt()
    console.log(data.password1);
    data.hashedPassword = encryptPassword(data.password1, data.salt)
    delete data.password1
    // var user = new User(data)
    User.findOne({empid: data.empid}, (err,user)=>{
      if(err){
        return err
      }
      if(!user){
        user = new User(data);
        Department.findOne({id: data.department}, (err, depart) => {
          if (err)
            return err
          else {
            user.dname = depart.name
            user.save();
            console.log("User Created");
            Session.findByIdAndUpdate(depart.sid, {$push: {members: user.empid}}, (err, sess) => {
              if (err) return err
            })
            res.sendStatus(200);
            // Emit the event
            io.emit("usercreated", req.body);
          }
        });
      } else {
        res.status(500).send({code:'AAE',message:'Account already exists'})
      }
    })
  } catch(error) {
    res.sendStatus(400)
    console.error(error);
  }
});

app.post('/userdata/:id/verify', (req, res) => {
  return User.findByIdAndUpdate(req.params.id, { verified: true }, (err, user) => {
    if (err) {
      return res.status(500).send({ message: 'Something went wrong' });
    }
    res.status(200).redirect(`/depdata/${user.department}`);
  });
});

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
      var tempHash = encryptPassword(pass, user.salt)
      if(tempHash==user.hashedPassword){
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
    User.findOne({empid:id},'-hashedPassword -salt -aadhar', (error, user) => {
      if (error) {
        return res.status(500).send({ message: 'Something went wrong' });
      }
      console.log("Users Accesed")
      if (req.headers.accept && req.headers.accept.indexOf('text/html') > -1) {
        return Department.findById(user.department, 'name', (err, dep) => {
          user.departmentDetails = { name: dep.name };
          return res.render('user', { user: user });
        });
      } else {
        return res.send(user)
      }
    });
})

//fetching data of departments
app.get("/depdata", (req, res) => {
  Department.find({}, (error, dep)=>{
    if (error) {
      return res.status(500).send({message: "ERROR"})
    }
    if (req.headers.accept && req.headers.accept.indexOf('text/html') > -1) {
      return Session.find({}, 'name admin_name members created_at', (error, sessions) => {
        return res.render('depdata', { department: dep, sessions: sessions });
      });
    } else {
      return res.send(dep)
    }
  });
});

//fetching employees from a department
app.get("/depdata/:id", (req, res) => {
  var id = req.params.id;
  return User.find({ department: id }, '-salt -hashedPassword -aadhar', (error, users) => {
    if (error) {
      return res.status(500).send({ message: "Something went wrong" });
    }
    console.log("Department User Accessed");

    if (req.headers.accept && req.headers.accept.indexOf('text/html') > -1) {
      return res.render('users', { users: users });
    } else {
      return res.send(users);
    }
  });
});

//Creating a department
app.post("/depdata/", async(req, res) => {
  try {
    var dep = new Department(req.body);
    await dep.save();
    Department.findOne({ id: dep.id }, async(err, dep1) => {
      if(err) {
        return err
      }
      if(dep1) {
        console.log("Department Created")
        //var dept = dep1
        var sess = new Session()
        sess.name=dep.name
        sess.admin=dep.admin
        sess.created_at=new Date()
        sess.save()
        var x=sess._id
        console.log(dep1._id)
        Department.findByIdAndUpdate(dep._id,
          { $set: { sid: sess._id } },
          () => console.log(sess._id)
        )

        io.emit("depcreated",req.body)
      }
      else {
        res.status(500).send({message:'Department not created'})
      }
    })

  } catch(error) {
    res.sendStatus(500)
    console.error(error)
  }
});

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

//creating  a new Event
app.post("/events",async(req,res)=>{
    var event = new Events(req.body);
    var d = new Date()
    var mum_offset = 5.5*60;
    d.setMinutes(d.getMinutes() + mum_offset);
    event.created_at= d;
    User.findOne({empid:event.creator},(err,user)=>{
      event.creat_name=user.name;
      var ev = new Events(event);
      ev.save();
    })
    await event.save();
    console.log("Event Created");
    console.log(req.body)
    res.sendStatus(200);
    //Emit the event
    //io.emit("sessioncreate",req.body);
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


//declining an event
app.post("/events/no",(req,res)=>{
  const eid = req.body.eid;
  const id = req.body.id;
  console.log(id,eid, req.body);
  Event.findByIdAndUpdate(
    eid,
    { $pull: { invited: id } },
    () => console.log('User removed')
  );
  res.sendStatus(200);
})

//session profile
app.get("/sessions/:sid", (req, res) => {
  var sid = req.params.sid;
  console.log(sid);
  Session.findOne({ _id: sid }, (err, ses) => {
    User.find(
      { empid: { $in: ses.members } },
      '-mobile_no -emailid -_id -__v -location -password1 -password2 -aadhar',
      (err,users) => {
        const result = {
          name:ses.name,
          emps:users
        };
        return res.send(result)
      });
    console.log("Sessions Data Accessed");
  });
})

//Searching a message
app.post("/search",(req,res)=>{
    var sid = req.body.sid
    var msg = req.body.msg
    Messages.find({_id:sid,body:{$regex:msg}},(err,message)=>{
      res.send(message);
    })
    //res.sendStatus(200);
    //console.error(error);
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
  User.update({empid:id},
    { $push : { sessions: sid } },
    ()=>console.log('User profile updated')
  );
  res.sendStatus(200)
})


//find sessions the employee is invited to
app.get("/eventinvites/:id",(req,res)=>{
  var id=req.params.id
  Events.find({invited:id},(error,event)=>{
    res.send(event)
    console.log("Event Invites Accessed")
  })
})


app.get("/events/:empid",(req,res)=>{
  var empid = req.params.empid;
  console.log('empid:',empid)
  var d= new Date()
  var num_offset=5.5*60;
  d.setMinutes(d.getMinutes()+num_offset);
  console.log(d);
  Events.where('members').in([empid]).where('ends_at').gt(d)
  .exec((err, events) => {
    console.log('something', events)
    res.send(events)
    // events.forEach((event) => {
    //   event.remove();
    // })
  });
  // Events.find({members:id}(error,session)=>{
  //   res.send(session)
  //   console.log("Event members Accesed")
  // })
})

//accepting an event
app.post("/events/yes",(req,res)=>
{
  var eid = req.body.eid;
  var id=req.body.id;
  Events.findByIdAndUpdate(
    eid,
    { $pull: { invited: id } },
    () => console.log('User removed')
  );
  Events.findByIdAndUpdate(
    eid,
    { $push: { members: id } },
    () => console.log('User added')
  );
  User.update({empid:id},
    { $push : { events: eid } },
    ()=>console.log('User profile updated')
  );
  res.sendStatus(200)

})

//creating a socket connection
io.on("connection", (socket) => {
    console.log("Socket is connected...")

});

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
        User.update({empid:empid},
            { $pull:{sessions:sid}},
            ()=>console.log("User profile updated")
        )
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
  });
});

// getting a file and emitting it
app.post("/fileshare", (req, res) => {
  var js = new Files();
  //console.log(req.body)
  fs.writeFile(req.body.fileName, req.body.data, 'base64', function(err, data){
    if (err)
      console.log(err)
    else{
      fs.readFile(req.body.fileName,(err,data1)=>{
        if (err)
       console.log(err)
        else{
        js.sender = req.body.id
        js.sess_id = req.body.sid
        js.filename=req.body.fileName
        js.data = data1
        var d = new Date();
        var mum_offset = 5.5*60;
        d.setMinutes(d.getMinutes() + mum_offset);
        js.created_at=d;
        js.save();
        console.log("Write Done")
      }
     })
      io.emit("file",js)
    }
  })
})

/*
app.post("/fileshare", (req, res) => {
  var js = new Files();
  //js.save();
  var writerStream = fs.createWriteStream(req.body.fileName)
    writerStream.write(req.body.data,'base64');
    writerStream.end();

    writerStream.on('finish', function() {
      console.log("Write completed.");
   });
   writerStream.on('error', function(err){
    console.log(err.stack);
  });
    fs.readFile(req.body.fileName,(err,data1)=>{
        //js.sender = req.body.id
        //js.sess_id = req.body.sid
        js.filename=req.body.fileName
        js.data = data1
        var d = new Date();
        var mum_offset = 5.5*60;
        d.setMinutes(d.getMinutes() + mum_offset);
        js.created_at=d;
        js.save();
    })
      io.emit("file",js)
})*/

//fetching files from the dataset
app.get("/fileshare/:seid", (req, res) => {
    var sid=req.params.seid;
    Files.find({sess_id:sid}, (error, data) => {
        if(error)
          console.log(error);
        else{}
        res.send(data);
        console.log("Files Accessed");
      });
});

app.get("/export/:session_id", (req, res) => {
  return Messages.find({ sess_id: req.params.session_id }, '-_id -__v').exec()
    .then(function (docs) {
      const filename = `messages${Date.now()}.csv`;
      Messages.csvReadStream(docs)
        .pipe(fs.createWriteStream(filename))
        .on('close', () => {
          const filePath = path.join(__dirname, filename);
          const stat = fs.statSync(filePath);

          res.writeHead(200, {
            'Content-Type': 'text/csv',
            'Content-Length': stat.size
          });

          const readStream = fs.createReadStream(filePath);
          readStream.pipe(res);
        });
    });
});

app.get('/videochat/:room_name', (req, res) => {
  return res.render('videortc', { room_name: req.params.room_name });
});
//Define routes
app.use('/', routes);
app.use('/users', users);

//creating a server
var server = http.listen(3020, () => {
    console.log(new Date())
    console.log("Well done, now I am listening on ", server.address().port)
})
