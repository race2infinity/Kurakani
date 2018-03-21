const Task = require('data.task')
const express = require('express')
const router = express.Router()
const config = require('../config')
const jwt = require('jsonwebtoken')
const bCrypt = require('bcrypt')
const { get, post, getJSON } = require('../lib/http')
const { fetchOne, saveRecord }=require('../lib/mongoose')

module.exports = io => {
  router.get('/', renderDashboard)

  function renderDashboard (req, res) {
    res.render('index.html', {})
  }

  return router
}

function isValidPassword(user, password) {
  return bCrypt.compareSync(password, user.password)
}
function createHash(password){
  return bCrypt.hashSync(password, bCrypt.genSaltSync(10), null)
}
function verifySignature(msg, sig, publicKey) {
	const verify = crypto.createVerify('sha256')
	verify.update(msg)
	const status = verify.verify(Buffer.from(publicKey, 'base64').toString('ascii'), sig, 'base64')
	return status
}
