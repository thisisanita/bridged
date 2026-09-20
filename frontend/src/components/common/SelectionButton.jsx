import Button from './Button.jsx'

function SelectionButton({
  selected = false,
  children,
  ...props
}) {
  return (
    <Button
      {...props}
      variant="selection"
      aria-pressed={selected}
    >
      {children}
    </Button>
  )
}

export default SelectionButton
