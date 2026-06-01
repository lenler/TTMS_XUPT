import { http } from './http'

export const studioApi = {
  list: (name = '') => http.get('/studios', { params: { name } }),
  create: (payload) => http.post('/studios', payload),
  update: (id, payload) => http.put(`/studios/${id}`, payload),
  remove: (id) => http.delete(`/studios/${id}`)
}

export const playApi = {
  list: (name = '') => http.get('/plays', { params: { name } }),
  create: (payload) => http.post('/plays', payload)
}

export const scheduleApi = {
  list: (playId) => http.get('/schedules', { params: { playId } }),
  create: (payload) => http.post('/schedules', payload),
  generateTickets: (id) => http.post(`/schedules/${id}/tickets`)
}

export const ticketApi = {
  list: (scheduleId) => http.get('/tickets', { params: { scheduleId } }),
  checkIn: (id) => http.post(`/tickets/${id}/check-in`)
}

export const saleApi = {
  placeOrder: (payload) => http.post('/sales/orders', payload),
  pay: (id, payload) => http.post(`/sales/${id}/payments`, payload),
  refund: (id) => http.post(`/sales/${id}/refund`)
}
