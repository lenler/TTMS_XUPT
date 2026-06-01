import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/admin/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true,
      },
      '/customer/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true,
      },
    },
  },
})
