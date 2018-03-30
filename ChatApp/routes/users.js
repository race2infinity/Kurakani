var express = require('express');
var router = express.Router();
var mongojs = require('mongojs');
var db = mongojs('dummydata', ['admin']);
var bcrypt = require('bcryptjs');
var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;

//Login Page - Get
router.get('/login', function(req, res){
    res.render('login');
});

//Register Page -Get
router.get('/admin', function(req, res){
    res.render('admin');
});

//Dashboard
router.get('/dash', function(req, res){
    res.render('dash');
});

//Dashboard - POST
router.post('/dash', function(req, res){
    var Dept_name = req.body.Deptname;
    var Dept_location = req.body.location;
    var admin_email = req.body.email;
    var Dept_id = req.body.DeptID;

    req.checkBody('Dept_name', 'Department Name is not mentioned').notEmpty();
    req.checkBody('Dept_location', 'Department location is not mentioned').notEmpty();
    req.checkBody('admin_email', 'Department Name is not mentioned').isEmail();
    req.checkBody('Dept_id', 'Department ID is not mentioned').notEmpty();

    var errors = req.validationErrors();

    if(errors) {
        console.log('Department form has errors...');
        res.render('dash', {
            errors: errors,
            Dept_name: Deptname,
            Dept_location: location,
            admin_email: email,
            Dept_id: DeptID
        });
    } else {
        //if()
    }
});

router.post('/admin', passport.authenticate('local', {
    successRedirect: 'admin_dash', failureRedirect: '/users/admin', failureFlash: true}),
    function(req, res) {
        console.log('Authentication successful');
        res.render('admin_dasht');        
});

passport.serializeUser(function(user, done){
    done(null, user._id);
});

passport.deserializeUser(function(id, done) {
    db.admin.findOne({_id: mongojs.ObjectId(id)}, function(err,user) {
        done(err, user); 
    });
});

passport.use(new LocalStrategy(function(username, password, done){
    db.admin.findOne({empid: username}, function(err, user) {
            if(err) {
                return done(err);
            }
            if(!user){
                return done(null, false, {message: 'Incorrect Username'});
            }
            if(!user.password) {
                return done(null, false, { message: 'Incorrect password.'});
            }
            return done(null, user);
            
            /*bcrypt.compare(password, user.password, function(err, isMatch){
                if(err) {
                    return done(err);
                }
                if(isMatch){
                    return done(null, user);
                }else {
                    return done(null, false,{message: 'Incorrect Password'})
                }
            });*/
    });
}));

router.post('/login', passport.authenticate('local', {
    successRedirect: 'dash', failureRediect: '/users/login', failureFlash: 'Invalid Username or Password' }), 
    function(req, res){
        console.log('Authentication successful');
        res.render('dash');  
});

router.get('/logout', function(req, res){
    req.logout();
    req.flash('success', 'You have logged out');
    req.redirect('/users/login');
})

module.exports = router;
