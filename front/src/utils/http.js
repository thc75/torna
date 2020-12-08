import needle from 'needle'
import axios from 'axios'
import { getToken } from '@/utils/auth'

const baseURL = process.env.VUE_APP_BASE_API || `${location.protocol}//${location.host}`

// 创建axios实例
const client = axios.create({
  baseURL: baseURL, // api 的 base_url
  timeout: 60000 // 请求超时时间,60秒
})

export function getBaseUrl() {
  return baseURL
}

function getFullUrl(uri) {
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
  needle.request('GET', getFullUrl(uri), data, {
    // 设置header
    headers: get_headers()
  }, (error, response) => {
    doResponse.call(that, error, response, callback, errorCallback)
  })
}

function get_headers() {
  return {
    Authorization: get_token()
  }
}

function get_token() {
  const token = getToken() || ''
  return `Bearer ${token}`
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
  needle.request('POST', getFullUrl(uri), data, {
    // 指定这一句即可
    json: true,
    headers: get_headers()
  }, (error, response) => {
    doResponse.call(that, error, response, callback, errorCallback)
  })
}

export function request(method, uri, data, headers, isJson, isForm, files, callback) {
  // 如果是文件上传，使用axios，needle上传文件不完美，不支持一个name对应多个文件
  if (files && files.length > 0) {
    doMultipart.call(this, uri, data, files, headers, callback)
    return
  }
  const that = this
  if (isForm) {
    headers['Content-Type'] = 'application/x-www-form-urlencoded'
  }
  Object.assign(headers, get_headers())
  needle.request(method, baseURL + uri, data, {
    // 设置header
    headers: headers,
    json: isJson
  }, (error, response) => {
    callback.call(that, error, response)
  })
}

export function doMultipart(uri, data, files, headers, callback) {
  const that = this
  const formData = new FormData()
  files.forEach(fileConfig => {
    fileConfig.files.forEach(file => {
      formData.append(fileConfig.name, file)
    })
  })
  for (const name in data) {
    formData.append(name, data[name])
  }
  client.post(uri, formData, {
    headers: headers
  }).then(function(response) {
    callback.call(that, null, response)
  }).catch(function(error) {
    callback.call(that, error, null)
  })
}

/**
 *  文件必须放在public下面
 * @param path 相对于public文件夹路径，如文件在public/static/sign.md，填：static/sign.md
 * @param callback 回调函数，函数参数是文件内容
 */
export function getFile(path, callback) {
  const that = this
  axios.get(path)
    .then(function(response) {
      callback.call(that, response.data)
    })
}

/**
 * ajax请求，并下载文件
 * @param uri 请求path
 * @param params 请求参数，json格式
 * @param filename 文件名称
 */
function downloadFile(uri, params, filename) {
  client.post(uri, {
    data: encodeURIComponent(JSON.stringify(params)),
    access_token: getToken()
  }).then(response => {
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', filename)
    document.body.appendChild(link)
    link.click()
  })
}

function doResponse(error, response, callback, errorCallback) {
  // 成功
  if (!error && response.statusCode === 200) {
    const resp = response.body
    const code = resp.code || ''
    // 未登录
    if (code === '1000') {
      this.goLogin()
      return
    }
    if (code === '0') { // 成功
      callback && callback.call(this, resp)
    } else {
      this.$message.error(resp.msg || '请求异常，请查看日志')
      errorCallback && errorCallback.call(this, resp)
    }
  } else {
    this.$message.error('请求异常，请查看日志')
  }
}
