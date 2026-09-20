function formatCode(value) {
  return value?.toLowerCase().replaceAll('_', ' ')
}

// Presents every capacity-consuming chat returned by the existing open-chats
// endpoint; both ASSIGNED and ACTIVE therefore contribute to the counter.
function AgentChatList({ chats, selectedChatId, onSelect }) {
  return (
    <aside className="agent-chat-list">
      <div className="agent-chat-list__heading">
        <h2>Open chats</h2>
        <span className="count-badge">{chats.length}</span>
      </div>

      <div className="agent-chat-list__items">
        {chats.length === 0 && (
          <p className="agent-chat-list__empty">No open chats right now.</p>
        )}
        {chats.map((chat) => (
          <button
            type="button"
            key={chat.chatId}
            className={`agent-chat-card${
              selectedChatId === chat.chatId ? ' agent-chat-card--selected' : ''
            }`}
            onClick={() => onSelect(chat)}
          >
            <span className="agent-chat-card__topline">
              <strong>{chat.customerName}</strong>
              <span className={`status-pill status-pill--${chat.status.toLowerCase()}`}>
                {chat.status}
              </span>
            </span>
            <span>Chat #{chat.chatId}</span>
            <span>{formatCode(chat.topicSkill)} · {formatCode(chat.preferredLanguage)}</span>
          </button>
        ))}
      </div>
    </aside>
  )
}

export default AgentChatList
