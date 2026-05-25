import { defineStore } from 'pinia'

export const useSessionStore = defineStore('session', {
  state: () => ({
    currentUser: { name: '系统管理员', roles: ['ADMIN'] },
    permissions: ['studio:manage', 'play:manage', 'sale:manage', 'finance:view']
  })
})
