import Button from '../common/Button.jsx'
import ConnectionStatus from '../chat/ConnectionStatus.jsx'
import MessageComposer from '../chat/MessageComposer.jsx'
import MessageList from '../chat/MessageList.jsx'
import AgentChatList from './AgentChatList.jsx'
import { useAgentWorkspace } from '../../hooks/useAgentWorkspace.js'

function AgentWorkspacePage({ session, onExit }) {
  const workspace = useAgentWorkspace(session)
  const chat = workspace.selectedChat

  return (
    <main className="workspace-page">
      <header className="workspace-header">
        <div className="brand"><span className="brand-mark">B</span><span>Bridged</span></div>
        <div className="agent-header-details">
          <strong>{session.displayName}</strong>
          <span>Open chats: {workspace.openChats.length}</span>
          <Button variant="text" onClick={onExit}>Exit demo</Button>
        </div>
      </header>

      <div className="agent-workspace">
        <AgentChatList
          chats={workspace.openChats}
          selectedChatId={chat?.chatId}
          onSelect={workspace.selectChat}
        />

        <section className="agent-conversation">
          {!chat ? (
            <div className="agent-empty-state">
              <h1>Select an open chat</h1>
              <p>Assigned and active customer chats appear on the left.</p>
            </div>
          ) : (
            <>
              <header className="agent-conversation__header">
                <div>
                  <p className="eyebrow">Chat #{chat.chatId}</p>
                  <h2>{chat.customerName}</h2>
                </div>
                {chat.status === 'ACTIVE' ? (
                  <ConnectionStatus status={workspace.connectionStatus} />
                ) : <span className="status-pill status-pill--assigned">ASSIGNED</span>}
              </header>

              {workspace.error && <p className="chat-error" role="alert">{workspace.error}</p>}

              {chat.status === 'ASSIGNED' ? (
                <div className="accept-chat-panel">
                  <h3>Ready to help {chat.customerName}?</h3>
                  <p>Accept this chat to open live messaging.</p>
                  <Button disabled={workspace.isWorking} onClick={workspace.acceptSelectedChat}>
                    {workspace.isWorking ? 'Accepting…' : 'Accept chat'}
                  </Button>
                </div>
              ) : (
                <>
                  <MessageList
                    messages={workspace.messages}
                    currentUserId={session.userId}
                    otherSenderLabel="Customer"
                  />
                  <footer className="agent-conversation__footer">
                    <MessageComposer
                      value={workspace.draft}
                      disabled={workspace.connectionStatus !== 'connected'}
                      onChange={workspace.setDraft}
                      onSend={workspace.sendMessage}
                    />
                    <Button
                      variant="secondary"
                      disabled={workspace.isWorking}
                      onClick={workspace.closeSelectedChat}
                    >
                      Close chat
                    </Button>
                  </footer>
                </>
              )}
            </>
          )}
        </section>
      </div>
    </main>
  )
}

export default AgentWorkspacePage
