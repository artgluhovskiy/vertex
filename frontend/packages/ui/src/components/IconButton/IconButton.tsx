import { ButtonHTMLAttributes, ReactNode } from 'react';

export interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  icon: ReactNode;
  tooltip?: string;
  active?: boolean;
}

export function IconButton({
  icon,
  tooltip,
  active = false,
  className = '',
  ...props
}: IconButtonProps) {
  return (
    <button
      className={`icon-button ${active ? 'icon-button-active' : ''} ${className}`}
      title={tooltip}
      {...props}
    >
      {/* TODO: Implement icon button with tooltip support */}
      {icon}
    </button>
  );
}
