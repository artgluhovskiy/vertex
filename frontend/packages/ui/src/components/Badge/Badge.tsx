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
  const baseStyles = 'inline-flex items-center justify-center font-medium';

  const sizeStyles = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-3 py-1 text-sm',
  };

  const variantStyles = {
    default: 'bg-light-bg-tertiary dark:bg-dark-bg-tertiary text-light-text-secondary dark:text-dark-text-secondary',
    primary: 'bg-primary/10 text-primary',
    success: 'bg-success/10 text-success',
    warning: 'bg-warning/10 text-warning',
    error: 'bg-error/10 text-error',
    purple: 'bg-secondary/10 text-secondary',
    pink: 'bg-accent/10 text-accent',
  };

  const roundedStyles = rounded ? 'rounded-full' : 'rounded';

  return (
    <span
      className={`${baseStyles} ${sizeStyles[size]} ${variantStyles[variant]} ${roundedStyles} ${className}`}
      {...props}
    >
      {children}
    </span>
  );
}
