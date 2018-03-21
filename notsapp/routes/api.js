const Task = require('data.task')
const express = require('express')
const jwt = require('jsonwebtoken')
const { User } = require('../models/User')
const { fetchOne, saveRecord } = require('../lib/mongoose')
const config = require('../config')

const router = express.Router()

router.post('/users', registerUser)
router.post('/login', loginUser)
router.get('/me', returnMyData)

function registerUser (req, res) {
  const state = { reqObj: req }

  validateInput(state)
  .map(getPayload)
  .chain(checkDuplicateNo)
  .chain(saveUser)
  .fork(err => {
    if (err.code && err.code == 'MII')
      return res.status(400).send({code: 'MII', message: 'Missing important information'})
    if (err.code && err.code == 'DMN')
      return res.status(400).send({code: 'DMN', message: 'Duplicate Mobile No'})

    return res.status(500).send(err)
  }, state => res.status(200).send(state.user))

  function validateInput (state) {
    if (!state.reqObj.body.name || !state.reqObj.body.mobile_no || !state.reqObj.body.password)
      return Task.rejected({code: 'MII'})
    return Task.of(state)
  }
  function getPayload (state) {
    return Object.assign({}, state, {payload: state.reqObj.body})
  }
  function checkDuplicateNo (state) {
    return fetchOne(User, {mobile_no: state.reqObj.body.mobile_no})
    .chain(user => user._id ? Task.rejected({code: 'DMN'}) : Task.of(state))
  }
  function saveUser (state) {
    return saveRecord(User, state.payload)
    .map(savedUser => Object.assign({}, state, {user: savedUser}))
  }
}

function loginUser (req, res) {
  const state = { reqObj: req }

  validateInput(state)
  .map(getPayload)
  .chain(tryLoggingIn)
  .chain(generateToken)
  .fork(err => {
    if (err.code && err.code == 'MII')
      return res.status(400).send({code: 'MII', message: 'Missing important information'})
    if (err.code && err.code == 'MPM')
      return res.status(400).send({code: 'MPM', message: 'Mobile No and Password do not match'})

    return res.status(500).send(err)
  }, state => res.status(200).send(state.token))

  function validateInput (state) {
    if (!state.reqObj.body.mobile_no || !state.reqObj.body.password)
      return Task.rejected({code: 'MII'})
    return Task.of(state)
  }
  function getPayload (state) {
    return Object.assign({}, state, {payload: state.reqObj.body})
  }
  function tryLoggingIn (state) {
    return fetchOne(User, state.payload)
    .chain(user => user ? Task.of(Object.assign({}, state, {user})) : Task.rejected({code: 'MPM'}))
  }
  function generateToken (state) {
    return Task.of(jwt.sign(state.user, config.secret))
    .map(token => Object.assign({}, state, {token: token}))
  }
}

function returnMyData (req, res) {
  const state = { reqObj: req }

  checkAuthentication(state)
  .chain(fetchMyData)
  .fork(err => {
    if (err.code && err.code == 'NAUTH')
      return res.status(400).send({code: 'NAUTH', message: 'Authentication failed'})

    return res.status(500).send(err)
  }, state => res.status(200).send(state.user))

  function checkAuthentication (state) {
    return new Task(function (rejected, resolved) {
      const token = state.reqObj.headers.jwt
      jwt.verify(token, config.secret, (err, decoded) => err ? rejected({code: 'NAUTH', err}) : resolved(decoded))
    })
    .map(decoded => Object.assign({}, state, {decoded}))
  }
  function fetchMyData (state) {
    return fetchOne(User, {mobile_no: state.decoded.mobile_no})
    .map(user => {
      delete user.password
      return user
    })
    .map(user => Object.assign({}, state, {user}))
  }
}

module.exports = router
