// REST history and STOMP can deliver the same saved message. The database
// messageId is used to keep only one copy, while local triage messages use localId.
export function mergeMessages(currentMessages, incomingMessages) {
  const messagesById = new Map()

  for (const message of [
    ...currentMessages,
    ...incomingMessages,
  ]) {
    const key = message.messageId != null
      ? `server:${message.messageId}`
      : `local:${message.localId}`

    messagesById.set(key, message)
  }

  return [...messagesById.values()].sort((first, second) => {
    return new Date(first.createdAt) - new Date(second.createdAt)
  })
}
