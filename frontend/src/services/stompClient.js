import { Client } from '@stomp/stompjs'
import { createClientMessageId } from '../utils/createClientMessageId.js'

// Creates one STOMP connection for one active chat. PostgreSQL stores messages;
// this client only subscribes to and publishes live delivery events.
export function createChatStompClient({
  chatId,
  userId,
  onMessage,
  onConnected,
  onStatusChange,
  onError,
}) {
  const websocketProtocol =
    window.location.protocol === 'https:' ? 'wss' : 'ws'

  const client = new Client({
    brokerURL:
      `${websocketProtocol}://${window.location.host}/ws`,

    // If the connection drops, STOMP waits five seconds and reconnects.
    reconnectDelay: 5000,

    onConnect: () => {
      onStatusChange('connected')

      // Subscribing means this browser receives every saved message broadcast
      // by Spring to this specific chat destination.
      client.subscribe(`/topic/chats/${chatId}`, (frame) => {
        try {
          onMessage(JSON.parse(frame.body))
        } catch {
          onError('A live message could not be read.')
        }
      })

      // History is requested only after the live subscription exists. If a
      // new message arrives during the REST request, mergeMessages removes the
      // duplicate while ensuring that no message falls into a timing gap.
      onConnected?.()
    },

    onWebSocketClose: () => {
      onStatusChange('disconnected')
    },

    onWebSocketError: () => {
      onStatusChange('error')
      onError('The live chat connection failed.')
    },

    onStompError: (frame) => {
      onStatusChange('error')
      onError(frame.headers.message ?? 'A messaging error occurred.')
    },
  })

  return {
    connect() {
      onStatusChange('connecting')
      client.activate()
    },

    disconnect() {
      return client.deactivate()
    },

    send(content) {
      if (!client.connected) {
        throw new Error('The live chat is not connected.')
      }

      // The frontend-generated UUID lets the backend recognize a retry and
      // avoid inserting the same customer message twice.
      client.publish({
        destination:
          `/app/chats/${chatId}/users/${userId}/messages`,
        body: JSON.stringify({
          clientMessageId: createClientMessageId(),
          content,
        }),
      })
    },
  }
}
