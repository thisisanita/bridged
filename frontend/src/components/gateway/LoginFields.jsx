import SelectionButton from '../common/SelectionButton.jsx'

function LoginFields({
  role,
  userId,
  disabled,
  onRoleChange,
  onUserIdChange,
}) {
  const loginHint = role === 'CUSTOMER'
    ? 'Demo customer login: User ID 1'
    : role === 'AGENT'
      ? 'Use the assigned agent User ID shown in the customer chat.'
      : 'Choose a role to see a demo login example.'

  return (
    <div className="login-fields">
      <fieldset
        className="role-selector"
        disabled={disabled}
      >
        <legend>Choose your role</legend>

        <div className="role-options">
          <SelectionButton
            selected={role === 'CUSTOMER'}
            onClick={() => onRoleChange('CUSTOMER')}
          >
            Customer
          </SelectionButton>

          <SelectionButton
            selected={role === 'AGENT'}
            onClick={() => onRoleChange('AGENT')}
          >
            Agent
          </SelectionButton>
        </div>
      </fieldset>

      <label className="form-field">
        User ID
        <input
          type="number"
          min="1"
          required
          disabled={disabled}
          value={userId}
          onChange={(event) =>
            onUserIdChange(event.target.value)
          }
          placeholder="Enter your seeded user ID"
        />
        <small className="form-field__hint">{loginHint}</small>
      </label>
    </div>
  )
}

export default LoginFields
