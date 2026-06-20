const BASE_URL = 'https://chileme-production.up.railway.app/api'

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
        console.error('请求失败:', JSON.stringify(err))
        uni.showToast({ title: '网络错误: ' + (err.errMsg || ''), icon: 'none', duration: 3000 })
        reject(err)
      }
    })
  })
}

export const $get = (url, params) => request(url, { data: params })
export const $post = (url, data) => request(url, { method: 'POST', data })
export const $put = (url, data) => request(url, { method: 'PUT', data })
export const $delete = (url, params) => request(url, { method: 'DELETE', data: params })

export const $upload = (url, filePath, name = 'file') => {
  return new Promise((resolve, reject) => {
    const token = getToken()
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name,
      header: token ? { 'Authorization': 'Bearer ' + token } : {},
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) resolve(data.data)
            else reject(new Error(data.msg))
          } catch (e) {
            reject(e)
          }
        } else {
          reject(new Error('上传失败'))
        }
      },
      fail: reject
    })
  })
}

export default { $get, $post, $put, $delete, $upload }
