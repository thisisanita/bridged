import Button from '../common/Button.jsx'

// Customer landing state shown before a new TRIAGE chat is created.
function StartChatPanel({
  displayName,
  isStarting,
  onStart,
}) {
  return (
    <section className="start-chat-panel">
      <p className="eyebrow">Customer support</p>
      <h1>Good to see you, {displayName}.</h1>
      <p className="start-chat-panel__intro">
        Answer two quick questions and we will connect
        you with an agent who has the right expertise.
      </p>

      <div className="support-card">
        <div>
          <h2>Chat with our support team</h2>
          <p>
            Select your preferred language and the topic
            you need help with.
          </p>
        </div>

        <Button
          disabled={isStarting}
          onClick={onStart}
        >
          {isStarting ? 'Starting…' : 'Start a new chat'}
        </Button>
      </div>
    </section>
  )
}

export default StartChatPanel
