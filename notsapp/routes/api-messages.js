const Task = require('data.task')
const express = require('express')
const jwt = require('jsonwebtoken')
const mongoose = require('mongoose')
const { User } = require('../models/User')
const { Message } = require('../models/Message')
const { fetch, fetchOne, saveRecord } = require('../lib/mongoose')
const config = require('../config')

const router = express.Router()

router.post('/messages', sendMessage)
router.get('/messages', viewMessages)


function sendMessage (req, res) {
  const state = { reqObj: req }

  checkAuthentication(state)
  .chain(validateInput)
  .map(getPayload)
  .chain(validateRecipient)
  .chain(saveMessage)
  .fork(err => {
    if (err.code && err.code == 'MII')
      return res.status(400).send({code: 'MII', message: 'Missing important information'})
    if (err.code && err.code == 'NAUTH')
      return res.status(400).send({code: 'NAUTH', message: 'Duplicate Mobile No'})
    if (err.code && err.code == 'IR')
      return res.status(400).send({code: 'IR', message: 'Invalid recipient'})
    return res.status(500).send(err)
  }, state => res.status(200).send(state.message))

  function checkAuthentication (state) {
    return new Task(function (rejected, resolved) {
      const token = state.reqObj.headers.jwt
      jwt.verify(token, config.secret, (err, decoded) => err ? rejected({code: 'NAUTH', err}) : resolved(decoded))
    })
    .map(decoded => Object.assign({}, state, {user: decoded}))
  }
  function validateInput (state) {
    if (!state.reqObj.body.to || !state.reqObj.body.body || !state.reqObj.body.body.text)
      return Task.rejected({code: 'MII'})
    return Task.of(state)
  }
  function getPayload (state) {
    return Object.assign({}, state, {payload: {
      to: state.reqObj.body.to,
      from: state.user.mobile_no,
      body: {
        text: state.reqObj.body.body.text
      }
    }})
  }
  function validateRecipient (state) {
    return ((state.payload.to == state.user.mobile_no) ? Task.rejected({code: 'IR'}) : Task.of(state))
    .chain(state => fetchOne(User, {mobile_no: state.payload.to}))
    .chain(user => user._id ? Task.of(state) : Task.rejected({code: 'IR'}))
  }
  function saveMessage (state) {
    return saveRecord(Message, state.payload)
    .map(message => Object.assign({}, state, {message}))
  }
}

function viewMessages (req, res) {
  const state = { reqObj: req }

  checkAuthentication(state)
  .chain(fetchLastMessage)
  .chain(fetchAllMessages)
  .fork(err => {
    if (err.code && err.code == 'MII')
      return res.status(400).send({code: 'MII', message: 'Missing important information'})
    if (err.code && err.code == 'NAUTH')
      return res.status(400).send({code: 'NAUTH', message: 'Duplicate Mobile No'})

    return res.status(500).send(err)
  }, state => res.status(200).send(state.messages))

  function checkAuthentication (state) {
    return new Task(function (rejected, resolved) {
      const token = state.reqObj.headers.jwt
      jwt.verify(token, config.secret, (err, decoded) => err ? rejected({code: 'NAUTH', err}) : resolved(decoded))
    })
    .map(decoded => Object.assign({}, state, {user: decoded}))
  }
  function fetchLastMessage (state) {
    return fetchOne(Message, {_id: mongoose.Types.ObjectId(state.reqObj.query.last_message)})
    .chain(message => message._id
      ? Task.of(Object.assign({}, state, {lastMessage: message}))
      : Task.of(state))
  }
  function fetchAllMessages (state) {
    return (state.lastMessage)
    ? fetch(Message, {created_at: {$gt: (state.lastMessage.created_at)}, $or: [
        {to: state.user.mobile_no},
        {from: state.user.mobile_no}
      ]})
    : fetch(Message, {$or: [
        {to: state.user.mobile_no},
        {from: state.user.mobile_no}
      ]})
    .map(messages => Object.assign({}, state, {messages: messages}))
  }
}
module.exports = router
