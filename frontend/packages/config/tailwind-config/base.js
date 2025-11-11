/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Dark mode colors (from V3 mockup screenshots)
        dark: {
          bg: {
            primary: '#0B0F19',    // Main background - dark navy
            secondary: '#151923',  // Sidebar/panels - lighter navy
            tertiary: '#1E2937',   // Cards/elevated surfaces
            hover: '#252E3D',      // Hover states
          },
          border: {
            primary: '#1E2937',    // Default borders - subtle
            secondary: '#2D3748',  // Lighter borders
            focus: '#3b82f6',      // Focus ring - blue
          },
          text: {
            primary: '#E8EAED',    // Main text - off-white
            secondary: '#9CA3AF',  // Secondary text - light gray
            muted: '#6B7280',      // Muted/disabled text - medium gray
            inverse: '#0B0F19',    // Text on light backgrounds
          },
        },
        // Light mode colors
        light: {
          bg: {
            primary: '#ffffff',    // Main background
            secondary: '#f9fafb',  // Sidebar/panels
            tertiary: '#f3f4f6',   // Cards/elevated surfaces
            hover: '#e5e7eb',      // Hover states
          },
          border: {
            primary: '#e5e7eb',    // Default borders
            secondary: '#d1d5db',  // Darker borders
            focus: '#3b82f6',      // Focus ring
          },
          text: {
            primary: '#111827',    // Main text
            secondary: '#6b7280',  // Secondary text
            muted: '#9ca3af',      // Muted/disabled text
            inverse: '#ffffff',    // Text on dark backgrounds
          },
        },
        // Semantic colors (same for both modes)
        primary: {
          DEFAULT: '#3b82f6',     // Blue accent
          hover: '#2563eb',
          active: '#1d4ed8',
          light: '#60a5fa',
          dark: '#1e40af',
        },
        secondary: {
          DEFAULT: '#8b5cf6',     // Purple (Smart Shelves, AI)
          hover: '#7c3aed',
          active: '#6d28d9',
          light: '#a78bfa',
          dark: '#5b21b6',
        },
        success: {
          DEFAULT: '#10b981',     // Green
          hover: '#059669',
          light: '#34d399',
        },
        warning: {
          DEFAULT: '#f59e0b',     // Orange
          hover: '#d97706',
          light: '#fbbf24',
        },
        error: {
          DEFAULT: '#ef4444',     // Red
          hover: '#dc2626',
          light: '#f87171',
        },
        info: {
          DEFAULT: '#06b6d4',     // Cyan
          hover: '#0891b2',
          light: '#22d3ee',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'Roboto',
          'Helvetica Neue',
          'Arial',
          'sans-serif',
        ],
        mono: [
          'JetBrains Mono',
          'Fira Code',
          'Menlo',
          'Monaco',
          'Courier New',
          'monospace',
        ],
      },
      fontSize: {
        'xs': ['0.75rem', { lineHeight: '1rem' }],
        'sm': ['0.875rem', { lineHeight: '1.25rem' }],
        'base': ['1rem', { lineHeight: '1.5rem' }],
        'lg': ['1.125rem', { lineHeight: '1.75rem' }],
        'xl': ['1.25rem', { lineHeight: '1.75rem' }],
        '2xl': ['1.5rem', { lineHeight: '2rem' }],
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }],
        '4xl': ['2.25rem', { lineHeight: '2.5rem' }],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '112': '28rem',
        '128': '32rem',
      },
      borderRadius: {
        'sm': '0.375rem',
        'DEFAULT': '0.5rem',
        'md': '0.5rem',
        'lg': '0.75rem',
        'xl': '1rem',
        '2xl': '1.5rem',
      },
      boxShadow: {
        'sm': '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        'DEFAULT': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'md': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        'lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'xl': '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        'inner': 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',
      },
      backdropBlur: {
        'xs': '2px',
        'sm': '4px',
        'DEFAULT': '8px',
        'md': '12px',
        'lg': '16px',
        'xl': '24px',
        '2xl': '40px',
      },
      animation: {
        'fade-in': 'fadeIn 0.2s ease-in-out',
        'slide-in': 'slideIn 0.2s ease-in-out',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideIn: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [],
};
