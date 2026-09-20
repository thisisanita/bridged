const labels = {
  disconnected: 'Disconnected',
  connecting: 'Connecting…',
  connected: 'Connected',
  triage: 'Automated triage',
  waiting: 'Waiting for an agent',
  closed: 'Chat closed',
  error: 'Connection problem',
}

function ConnectionStatus({
  status = 'disconnected',
}) {
  return (
    <div
      className={`connection-status connection-status--${status}`}
      role="status"
    >
      <span
        className="connection-status__indicator"
        aria-hidden="true"
      />

      {labels[status] ?? status}
    </div>
  )
}

export default ConnectionStatus
