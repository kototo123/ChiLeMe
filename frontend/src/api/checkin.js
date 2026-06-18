import { $get, $post } from './request'

export const checkinApi = {
  checkIn: (data) => $post('/checkin', data),
  useBreakCard: () => $post('/checkin/break-card'),
  getToday: () => $get('/checkin/today'),
  getCalendar: (year, month) => $get('/checkin/calendar', { year, month }),
  getHistory: (page, pageSize) => $get('/checkin/history', { page, pageSize }),
}
