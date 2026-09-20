import Button from '../common/Button.jsx'
import ChatWindow from '../chat/ChatWindow.jsx'
import StartChatPanel from './StartChatPanel.jsx'
import { useCustomerChat } from '../../hooks/useCustomerChat.js'

// The page owns presentation; useCustomerChat owns API calls and lifecycle
// state so the UI components remain small and reusable.
function CustomerSupportPage({ session, onExit }) {
  const chatState = useCustomerChat(session)

  return (
    <main className="workspace-page">
      <header className="workspace-header">
        <div className="brand">
          <span className="brand-mark">B</span>
          <span>Bridged</span>
        </div>
        <div className="workspace-user">
          <span>{session.displayName}</span>
          <Button variant="text" onClick={onExit}>Exit demo</Button>
        </div>
      </header>

      <div className="workspace-content">
        {chatState.phase === 'idle' ? (
          <StartChatPanel
            displayName={session.displayName}
            isStarting={chatState.isWorking}
            onStart={chatState.startChat}
          />
        ) : (
          <ChatWindow session={session} chatState={chatState} />
        )}

        {chatState.phase === 'idle' && chatState.error && (
          <p className="form-error page-error" role="alert">
            {chatState.error}
          </p>
        )}
      </div>
    </main>
  )
}

export default CustomerSupportPage
