const BASE_URL = 'http://127.0.0.1:8080/api'

function getToken() {
  return uni.getStorageSync('token')
}

function request(url, options = {}) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const header = {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
      ...options.header
    }

    uni.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: options.data,
      header,
      success: (res) => {
        if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/index/index' })
          reject(new Error('未授权'))
          return
        }
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else {
          uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          reject(new Error(res.data.msg))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const $get = (url, params) => request(url, { data: params })
export const $post = (url, data) => request(url, { method: 'POST', data })
export const $put = (url, data) => request(url, { method: 'PUT', data })
export const $delete = (url, params) => request(url, { method: 'DELETE', data: params })

export default { $get, $post, $put, $delete }
