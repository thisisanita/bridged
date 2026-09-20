import { useEffect, useRef } from 'react'
import MessageBubble from './MessageBubble.jsx'

function MessageList({
  messages,
  currentUserId,
  otherSenderLabel,
}) {
  const messageListRef = useRef(null)

  useEffect(() => {
    const messageList = messageListRef.current

    if (messageList) {
      // Scroll only this message container. scrollIntoView() also scrolled the
      // surrounding chat window, which hid its chat ID and connection header.
      messageList.scrollTop = messageList.scrollHeight
    }
  }, [messages])

  return (
    <div
      ref={messageListRef}
      className="message-list"
      aria-live="polite"
    >
      {messages.length === 0 ? (
        <p className="message-list__empty">
          No messages yet.
        </p>
      ) : (
        messages.map((message) => (
          <MessageBubble
            key={message.messageId ?? message.localId}
            message={message}
            currentUserId={currentUserId}
            otherSenderLabel={otherSenderLabel}
          />
        ))
      )}
    </div>
  )
}

export default MessageList
