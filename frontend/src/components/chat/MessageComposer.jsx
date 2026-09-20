import Button from '../common/Button.jsx'

function MessageComposer({
  value,
  disabled = false,
  placeholder = 'Write a message…',
  onChange,
  onSend,
}) {
  const canSend =
    !disabled && value.trim().length > 0

  function handleSubmit(event) {
    event.preventDefault()

    if (canSend) {
      onSend()
    }
  }

  function handleKeyDown(event) {
    if (
      event.key === 'Enter'
      && !event.shiftKey
    ) {
      event.preventDefault()

      if (canSend) {
        onSend()
      }
    }
  }

  return (
    <form
      className="message-composer"
      onSubmit={handleSubmit}
    >
      <textarea
        value={value}
        disabled={disabled}
        maxLength={4000}
        placeholder={placeholder}
        onChange={(event) =>
          onChange(event.target.value)
        }
        onKeyDown={handleKeyDown}
        aria-label="Message"
      />

      <Button
        type="submit"
        variant="send"
        disabled={!canSend}
        aria-label="Send message"
      >
        Send
      </Button>
    </form>
  )
}

export default MessageComposer
