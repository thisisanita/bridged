import { useState } from 'react'
import Button from '../common/Button.jsx'
import LoginFields from './LoginFields.jsx'
import { createDemoSession } from '../../services/api.js'

// This is a temporary demo identity check, not production authentication.
function GatewayPage({ onSessionReady }) {
  const [role, setRole] = useState('')
  const [userId, setUserId] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()

    if (!role || !userId) {
      setError('Choose a role and enter a user ID.')
      return
    }

    setError('')
    setIsSubmitting(true)

    try {
      const session = await createDemoSession(role, userId)
      onSessionReady(session)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="gateway-page">
      <section className="gateway-introduction">
        <div className="brand-mark">B</div>
        <p className="eyebrow">Bridged support platform</p>
        <h1>Customer support routing and messaging</h1>
        <p>
          A demonstration of automated chat assignment and
          real-time communication.
        </p>
      </section>

      <section className="gateway-panel">
        <form
          className="gateway-form"
          onSubmit={handleSubmit}
        >
          <h2>Enter Demo</h2>

          <LoginFields
            role={role}
            userId={userId}
            disabled={isSubmitting}
            onRoleChange={setRole}
            onUserIdChange={setUserId}
          />

          {error && (
            <p className="form-error" role="alert">
              {error}
            </p>
          )}

          <Button
            type="submit"
            disabled={isSubmitting || !role || !userId}
          >
            {isSubmitting ? 'Checking…' : 'Continue'}
          </Button>
        </form>
      </section>
    </main>
  )
}

export default GatewayPage
