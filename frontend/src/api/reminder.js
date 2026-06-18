import { $get, $put } from './request'

export const reminderApi = {
  getSetting: () => $get('/reminder'),
  updateSetting: (data) => $put('/reminder', data),
}
