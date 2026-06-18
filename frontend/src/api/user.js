import { $get, $post, $put } from './request'

export const userApi = {
  wxLogin: (data) => $post('/user/wx-login', data),
  getUserInfo: () => $get('/user/info'),
  updateProfile: (nickname, avatar, gender) => $put('/user/profile', { nickname, avatar, gender }),
  getNotifications: (page, pageSize) => $get('/notification', { page, pageSize }),
  getUnreadCount: () => $get('/notification/unread-count'),
  markAllRead: () => $put('/notification/read-all'),
}
