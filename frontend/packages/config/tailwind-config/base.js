/** @type {import('tailwindcss').Config} */
module.exports = {
  theme: {
    extend: {
      colors: {
        // Background colors (from mockup)
        bg: {
          primary: '#1a1816',
          secondary: '#0f0d0b',
          card: 'rgba(26,24,22,0.9)',
          overlay: 'rgba(30,28,26,0.85)',
        },
        // Text colors
        text: {
          primary: '#f5f3f0',
          secondary: '#e8e4e0',
          muted: '#d4cec8',
          disabled: '#a8a29e',
        },
        // Accent colors (Teal/Cyan)
        accent: {
          primary: '#14b8a6',
          secondary: '#0d9488',
          light: '#5eead4',
          subtle: '#8bd4c8',
        },
        // Border colors
        border: {
          primary: 'rgba(139,195,186,0.12)',
          hover: 'rgba(139,195,186,0.2)',
          focus: 'rgba(20,184,166,0.6)',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'sans-serif',
        ],
      },
      backdropBlur: {
        glass: '30px',
      },
    },
  },
  plugins: [],
};
