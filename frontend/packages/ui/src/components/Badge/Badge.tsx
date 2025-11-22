import { HTMLAttributes, ReactNode } from 'react';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'error' | 'purple' | 'pink';
  size?: 'sm' | 'md';
  rounded?: boolean;
  children: ReactNode;
}

export function Badge({
  variant = 'default',
  size = 'sm',
  rounded = true,
  className = '',
  children,
  ...props
}: BadgeProps) {
  return (
    <span
      className={`badge badge-${variant} badge-${size} ${rounded ? 'badge-rounded' : ''} ${className}`}
      {...props}
    >
      {/* TODO: Implement badge variants and colors */}
      {children}
    </span>
  );
}
