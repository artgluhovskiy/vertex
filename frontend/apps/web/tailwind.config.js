const baseConfig = require('@synapse/tailwind-config/base');

/** @type {import('tailwindcss').Config} */
module.exports = {
  ...baseConfig,
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  plugins: [require('@tailwindcss/typography')],
};
