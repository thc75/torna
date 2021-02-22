import axios from 'axios'
import { getToken } from '@/utils/auth'

const baseURL = process.env.VUE_APP_BASE_API || `${location.protocol}//${location.host}`

// 创建axios实例
const client = axios.create({
  baseURL: baseURL, // api 的 base_url
  timeout: 60000, // 请求超时时间,60秒
  headers: {
    post: {
      'Content-Type': 'application/json'
    }
  }
})
client.interceptors.request.use(config => {
  // 在发送请求之前做些什么
  Object.assign(config.headers, get_headers())
  return config
})

export function get_baseUrl() {
  return baseURL
}

export function get_full_url(uri) {
  if (!uri.startsWith('/')) {
    uri = '/' + uri
  }
  return baseURL + uri
}

/**
 * GET请求接口
 * @param uri uri
 * @param data 请求数据
 * @param callback 成功时回调
 * @param errorCallback 失败时回调
 */
export function get(uri, data, callback, errorCallback) {
  const that = this
  do_get.call(this, uri, data, response => {
    doResponse.call(that, response, callback, errorCallback)
  })
}

/**
 * post json, 请求接口
 * @param uri uri
 * @param data 请求数据
 * @param callback 成功时回调
 * @param errorCallback 失败时回调
 */
export function post(uri, data, callback, errorCallback) {
  const that = this
  do_post.call(this, uri, data, response => {
    doResponse.call(that, response, callback, errorCallback)
  })
}

/**
 * GET请求接口
 * @param uri uri
 * @param data 请求数据
 * @param callback 成功时回调，参数response
 */
export function do_get(uri, data, callback) {
  const that = this
  client.get(uri, {
    params: data
  })
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      that.$message.error('请求异常，请查看日志')
    })
}

/**
 * POST请求接口
 * @param uri uri
 * @param data 请求数据
 * @param callback 成功时回调，参数response
 */
export function do_post(uri, data, callback) {
  const that = this
  client.post(uri, data)
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      that.$message.error('请求异常，请查看日志')
    })
}

export function get_headers() {
  return {
    Authorization: get_token()
  }
}

function get_token() {
  const token = getToken() || ''
  return `Bearer ${token}`
}

export function request(method, uri, data, headers, isMultipart, callback) {
  if (isMultipart) {
    doMultipart.call(this, uri, data, headers, callback)
    return
  }
  const that = this
  const methodUpper = method.toUpperCase()
  const hasQuery = methodUpper === 'GET' || methodUpper === 'HEAD'
  const params = hasQuery ? data : null
  const postData = !hasQuery ? data : null
  axios.request({
    url: get_full_url(uri),
    method: method,
    headers: headers,
    params: params,
    data: postData
  })
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      that.$message.error('请求异常，请查看日志')
    })
}

export function doMultipart(uri, data, headers, callback) {
  const that = this
  const formData = new FormData()
  for (const name in data) {
    if (name === '__files__') {
      const fileConfigs = data[name]
      fileConfigs.forEach(fileConfig => {
        fileConfig.files.forEach(file => {
          formData.append(fileConfig.name, file)
        })
      })
    } else {
      formData.append(name, data[name])
    }
  }
  for (const name in data) {
    formData.append(name, data[name])
  }
  axios.post(get_full_url(uri), formData, {
    headers: headers
  })
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      that.$message.error('请求异常，请查看日志')
    })
}

/**
 *  文件必须放在public下面
 * @param path 相对于public文件夹路径，如文件在public/static/sign.md，填：static/sign.md
 * @param callback 回调函数，函数参数是文件内容
 */
export function get_file(path, callback) {
  const that = this
  axios.get(path)
    .then(function(response) {
      callback.call(that, response.data)
    })
}

function doResponse(response, callback, errorCallback) {
  // 成功
  if (response.status === 200) {
    const resp = response.data
    const code = resp.code || ''
    // 未登录
    if (code === '1000') {
      this.goLogin()
      return
    }
    if (code === '2000') {
      this.goSetPassword()
      return
    }
    if (code === '0') { // 成功
      callback && callback.call(this, resp)
    } else {
      this.$message.error(resp.msg || '请求异常，请查看日志')
      errorCallback && errorCallback.call(this, resp)
    }
  } else {
    console.error('error', response)
    this.$message.error('请求异常，请查看日志')
  }
}
