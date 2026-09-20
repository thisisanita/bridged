// One shared button keeps appearance and disabled behaviour consistent across
// the gateway, customer chat, and the future agent workspace.
function Button({
  children,
  type = 'button',
  variant = 'primary',
  className = '',
  ...props
}) {
  const classes = [
    'button',
    `button--${variant}`,
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <button
      type={type}
      className={classes}
      {...props}
    >
      {children}
    </button>
  )
}

export default Button
