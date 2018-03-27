var express = require('express');
var router = express.Router();

router.get('/', ensureAuthenticated, function(req, res){
    res.render('dash');
    console.log('this is index page')
});

function ensureAuthenticated(req, res, next){
    if(req.isAuthenticated()){
        return next();
    }
    res.redirect('/users/login');
   // res.render('dash');
}

module.exports = router;