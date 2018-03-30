//MongoDB schema for Broadcast
export const Broadcast = mongoose.model("Broadcast", {
  sender: String,
  body: String,
  created_at: {
    type: Date,
    default: new Date()
  }
})

