/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6',
        secondary: '#10B981',
        warning: '#F59E0B',
        danger: '#EF4444',
        background: '#FFF4F4',
        text: '#4A2D2D',
        pink: {
          400: '#FF85A2',
          500: '#F472B6'
        }
      },
      fontFamily: {
        sans: ['Noto Sans SC', 'sans-serif'],
        playful: ['ZCOOL KuaiLe', 'cursive']
      },
      borderRadius: {
        'playful': '2rem',
        'xl': '1.2rem',
        '2xl': '1.5rem',
        '3xl': '2rem',
        '4xl': '2.5rem'
      },
      boxShadow: {
        'playful': '6px 6px 0px black',
        'playful-sm': '4px 4px 0px black',
        'playful-lg': '8px 8px 0px black'
      }
    },
  },
  plugins: [],
}
