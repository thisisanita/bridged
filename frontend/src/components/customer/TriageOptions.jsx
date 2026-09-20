import Button from '../common/Button.jsx'
import SelectionButton from '../common/SelectionButton.jsx'

// Reused for both language and topic selection during local MVP triage.
function TriageOptions({
  label,
  options,
  selectedCode,
  disabled = false,
  onSelect,
  onConfirm,
}) {
  const canConfirm =
    !disabled && Boolean(selectedCode)

  return (
    <section className="triage-options">
      <div
        className="triage-options__choices"
        role="group"
        aria-label={label}
      >
        {options.map((option) => (
          <SelectionButton
            key={option.code}
            selected={selectedCode === option.code}
            disabled={disabled}
            title={option.description}
            onClick={() => onSelect(option.code)}
          >
            {option.label}
          </SelectionButton>
        ))}
      </div>

      <Button
        variant="send"
        disabled={!canConfirm}
        onClick={onConfirm}
        aria-label={`Send selected ${label}`}
      >
        ➜
      </Button>
    </section>
  )
}

export default TriageOptions
