const Task = require('data.task')
const express = require('express')
const jwt = require('jsonwebtoken')
const mongoose = require('mongoose')
const { User } = require('../models/User')
const { Message } = require('../models/Message')
const { fetch, fetchOne, saveRecord } = require('../lib/mongoose')
const config = require('../config')

const router = express.Router()

router.get('/dashboard-meta', getMetaData)

function getMetaData (req, res) {
  const state = {}

  getUserCount(state)
  .chain(getMessagesCount)
  .fork(err => {
    return res.status(500).send(err)
  }, state => res.status(200).send(state))

  function getUserCount (state) {
    return new Task((rejected, resolved) => {
      User.count({}, (err, count) => {
        if (err) return rejected(err)
        return resolved(count)
      })
    })
    .map(count => Object.assign({}, state, {users: count}))
  }
  function getMessagesCount (state) {
    return new Task((rejected, resolved) => {
      Message.count({}, (err, count) => {
        if (err) return rejected(err)
        return resolved(count)
      })
    })
    .map(count => Object.assign({}, state, {messages: count}))
  }
}

module.exports = router
