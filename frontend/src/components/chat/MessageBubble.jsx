function MessageBubble({
  message,
  currentUserId,
  otherSenderLabel = 'Support agent',
}) {
  const isOwnMessage =
    message.senderUserId === currentUserId

  const senderLabel = isOwnMessage
    ? 'You'
    : message.senderType === 'BOT'
      ? 'Bridged Assistant'
      : otherSenderLabel

  const time = message.createdAt
    ? new Date(message.createdAt).toLocaleTimeString(
        [],
        {
          hour: '2-digit',
          minute: '2-digit',
        },
      )
    : ''

  return (
    <article
      className={
        isOwnMessage
          ? 'message-bubble message-bubble--own'
          : 'message-bubble'
      }
    >
      <span className="message-bubble__sender">
        {senderLabel}
      </span>

      <p>{message.content}</p>

      {time && (
        <time className="message-bubble__time">
          {time}
        </time>
      )}
    </article>
  )
}

export default MessageBubble
