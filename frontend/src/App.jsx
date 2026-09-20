import { useState } from 'react'
import AgentWorkspacePage from './components/agent/AgentWorkspacePage.jsx'
import CustomerSupportPage from './components/customer/CustomerSupportPage.jsx'
import GatewayPage from './components/gateway/GatewayPage.jsx'
import './App.css'

function App() {
  // The demo session intentionally lives only in memory. Refreshing or exiting
  // returns to the gateway until real authentication is implemented.
  const [session, setSession] = useState(null)

  if (!session) {
    return <GatewayPage onSessionReady={setSession} />
  }

  if (session.role === 'CUSTOMER') {
    return (
      <CustomerSupportPage
        session={session}
        onExit={() => setSession(null)}
      />
    )
  }

  return (
    <AgentWorkspacePage
      session={session}
      onExit={() => setSession(null)}
    />
  )
}

export default App
