import { $get, $post, $put, $upload } from './request'

export const userApi = {
  wxLogin: (data) => $post('/user/wx-login', data),
  getUserInfo: () => $get('/user/info'),
  updateProfile: (nickname, avatar, gender) => $put('/user/profile', { nickname, avatar, gender }),
  uploadAvatar: (filePath) => $upload('/upload/avatar', filePath, 'file'),
  getNotifications: (page, pageSize) => $get('/notification', { page, pageSize }),
  getUnreadCount: () => $get('/notification/unread-count'),
  markAllRead: () => $put('/notification/read-all'),
}
