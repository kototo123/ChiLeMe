import { defineStore } from 'pinia'
import { userApi } from '@/api/user'
import { $post } from '@/api/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.userInfo?.id,
  },

  actions: {
    async autoLogin() {
      if (this.isLoggedIn) return
      try {
        let code = 'dev_' + Date.now()
        try {
          const loginRes = await uni.login({ provider: 'weixin' })
          code = loginRes.code || code
        } catch (loginErr) {
          console.warn('uni.login fail, use mock code', loginErr)
        }
        const data = await $post('/user/wx-login', {
          code,
          nickname: '用户' + Math.random().toString(36).slice(2, 6),
          avatar: '',
          gender: 0
        })
        this.setLoginData(data)
      } catch (e) {
        console.error('auto login failed', e)
      }
    },

    setLoginData(data) {
      this.token = data.token
      this.userInfo = data.user
      uni.setStorageSync('token', data.token)
      uni.setStorageSync('userInfo', JSON.stringify(data.user))
    },

    logout() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.reLaunch({ url: '/pages/index/index' })
    },

    async refreshUserInfo() {
      try {
        const info = await userApi.getUserInfo()
        this.userInfo = info
        uni.setStorageSync('userInfo', JSON.stringify(info))
      } catch (e) {
        console.error('refresh user info failed', e)
      }
    }
  }
})
