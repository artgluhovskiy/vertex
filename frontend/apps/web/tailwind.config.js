import baseConfig from '@synapse/tailwind-config/base';
import typography from '@tailwindcss/typography';

/** @type {import('tailwindcss').Config} */
export default {
  ...baseConfig,
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  plugins: [typography],
};
