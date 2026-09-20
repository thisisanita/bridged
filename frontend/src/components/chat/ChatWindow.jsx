import ConnectionStatus from './ConnectionStatus.jsx'
import MessageComposer from './MessageComposer.jsx'
import MessageList from './MessageList.jsx'
import TriageOptions from '../customer/TriageOptions.jsx'

// The backend currently returns an agentId with chat details, while the demo
// gateway asks for userId. This seed-only map makes that distinction visible
// during the demo and must be removed when real authentication is introduced.
const DEMO_AGENT_USER_IDS = {
  1: 3,
  2: 4,
  3: 5,
}

// Combines the same conversation history with a footer that changes as the
// chat moves through language, topic, waiting, and ACTIVE phases.
function ChatWindow({ session, chatState }) {
  const {
    phase, chat, messages, languages, skills, selectedLanguage,
    selectedSkill, draft, connectionStatus, error, isWorking,
    setSelectedLanguage, setSelectedSkill, setDraft, confirmLanguage,
    confirmTopic, sendMessage,
  } = chatState

  const status = phase === 'closed'
    ? 'closed'
    : phase === 'waiting'
    ? 'waiting'
    : phase === 'active' ? connectionStatus : 'triage'

  const heading = phase === 'active' && chat?.assignedAgentName
    ? chat.assignedAgentName
    : 'Bridged Assistant'

  const assignedAgentUserId =
    DEMO_AGENT_USER_IDS[chat?.assignedAgentId]

  return (
    <section className="chat-window">
      <header className="chat-window__header">
        <div>
          <p className="eyebrow">Support chat #{chat?.chatId}</p>
          <h2>{heading}</h2>
        </div>
        <ConnectionStatus status={status} />
      </header>

      {chat?.assignedAgentName && assignedAgentUserId && (
        <p className="demo-agent-note">
          Demo: Assigned to {chat.assignedAgentName}. Sign in as Agent using
          User ID {assignedAgentUserId}.
        </p>
      )}

      {error && <p className="chat-error" role="alert">{error}</p>}

      <MessageList messages={messages} currentUserId={session.userId} />

      <footer className="chat-window__footer">
        {phase === 'language' && (
          <TriageOptions
            label="language" options={languages}
            selectedCode={selectedLanguage} onSelect={setSelectedLanguage}
            disabled={isWorking}
            onConfirm={confirmLanguage}
          />
        )}
        {phase === 'topic' && (
          <TriageOptions
            label="topic" options={skills} selectedCode={selectedSkill}
            disabled={isWorking} onSelect={setSelectedSkill}
            onConfirm={confirmTopic}
          />
        )}
        {phase === 'waiting' && (
          <p className="waiting-note">
            You can leave this window open while we connect you.
          </p>
        )}
        {phase === 'active' && (
          <MessageComposer
            value={draft} disabled={connectionStatus !== 'connected'}
            onChange={setDraft} onSend={sendMessage}
          />
        )}
        {phase === 'closed' && (
          <p className="waiting-note">
            This conversation has ended. Thank you for contacting Bridged.
          </p>
        )}
      </footer>
    </section>
  )
}

export default ChatWindow
