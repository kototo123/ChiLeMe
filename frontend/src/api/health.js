import { $get, $post } from './request'

export const healthApi = {
  addRecord: (data) => $post('/health/record', data),
  getRecords: () => $get('/health/records'),
}
