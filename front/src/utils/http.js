import axios from 'axios'
import { getToken } from '@/utils/auth'
import qs from 'qs'
import { Message } from 'element-ui'

/**
 * 获取浏览器上面的根地址，到端口，后面没有/
 * @returns {string} 返回浏览器地址，如：http://xxx:7700
 */
function getBrowserUrl() {
  const url = location.href.toString()
  let baseUrl = url.split('#')[0]
  if (baseUrl.endsWith('/')) {
    baseUrl = baseUrl.substring(0, baseUrl.length - 1)
  }
  return baseUrl
}

const frontURL = getBrowserUrl()
const serverUrl = process.env.VUE_APP_BASE_API || frontURL

// 创建axios实例
const client = axios.create({
  baseURL: serverUrl, // api 的 base_url
  timeout: 600000, // 请求超时时间,600秒
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

export function get_base_url() {
  return frontURL
}

export function get_server_url() {
  return serverUrl
}

export function get_full_url(uri) {
  if (!uri.startsWith('/')) {
    uri = '/' + uri
  }
  return serverUrl + uri
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
  client.get(formatUri(uri), {
    params: data
  })
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      Message({
        message: '请求异常，请查看日志',
        type: 'error'
      })
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
  client.post(formatUri(uri), data)
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      console.error('error', error)
      Message({
        message: '请求异常，请查看日志',
        type: 'error'
      })
    })
}

function formatUri(uri) {
  return uri.startsWith('/') ? uri.substring(1) : uri
}

export function get_headers() {
  return {
    token: get_token()
  }
}

export function get_token() {
  return getToken() || ''
}

export function request(method, url, params, data, headers, isMultipart, callback, errorCall) {
  if (isMultipart) {
    doMultipart.call(this, url, params, data, headers, callback, errorCall)
    return
  }
  const that = this
  const config = {
    url: url,
    method: method,
    headers: headers,
    params: params,
    data: data,
    paramsSerializer: args => {
      return qs.stringify(args, { indices: false })
    },
    responseType: 'arraybuffer'
  }
  const targetHeaders = headers['target-headers']
  // 如果有目标头
  const realHeaders = targetHeaders ? JSON.parse(targetHeaders) : headers
  const contentType = realHeaders['Content-Type'] || realHeaders['content-type']
  // 如果是模拟表单提交
  if (contentType && contentType.toLowerCase().indexOf('x-www-form-urlencoded') > -1) {
    config.transformRequest = [function(data) {
      return qs.stringify(data, { indices: false })
    }]
  }
  axios.request(config)
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      errorCall && errorCall.call(that, error)
    })
}

export function doMultipart(url, params, data, headers, callback, errorCall) {
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
  axios.post(url, formData, {
    headers: headers,
    params: params
  })
    .then(response => {
      callback.call(that, response)
    })
    .catch(error => {
      errorCall && errorCall.call(that, error)
    })
}

export function download(uri, params, errorCall) {
  const that = this
  const config = {
    url: formatUri(uri),
    method: 'get',
    headers: get_headers(),
    params: params,
    paramsSerializer: args => {
      return qs.stringify(args, { indices: false })
    },
    responseType: 'blob'
  }
  client.request(config).then(response => {
    console.log(response)
    downloadFile(response)
  }).catch(error => {
    errorCall && errorCall.call(that, error)
  })
}

function downloadFile(response) {
  const headers = response.headers
  const contentType = getHeaderValue(headers, 'Content-Type') || ''
  const contentDisposition = getHeaderValue(headers, 'Content-Disposition') || ''
  if (contentType.indexOf('stream') > -1 || contentDisposition.indexOf('attachment') > -1) {
    const filename = getDispositionFilename(contentDisposition)
    const buffer = response.data
    const blob = new Blob([buffer])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', decodeURIComponent(filename))
    document.body.appendChild(link)
    link.click()
  }
}

function getHeaderValue(headers, key) {
  for (const k in headers) {
    if (k.toLowerCase() === key.toLowerCase()) {
      return headers[k]
    }
  }
  return null
}

function getDispositionFilename(disposition) {
  const dispositionArr = disposition.split(';')
  for (let i = 0; i < dispositionArr.length; i++) {
    const item = dispositionArr[i].trim()
    // filename=xx.xls
    if (item.toLowerCase().startsWith('filename')) {
      const result = item.split('=')
      if (result && result.length > 1) {
        let filename = result[1]
        if (filename.startsWith('\'') || filename.startsWith('\"')) {
          filename = filename.substring(1)
        }
        if (filename.endsWith('\'') || filename.endsWith('\"')) {
          filename = filename.substring(0, filename.length - 1)
        }
        return filename
      }
      return ''
    }
  }
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
      // code=100为业务报错
      const msg = code === '100' ? resp.msg : '请求异常，请查看日志'
      Message({
        message: msg,
        type: 'error'
      })
      errorCallback && errorCallback.call(this, resp)
    }
  } else {
    console.error('error', response)
    Message({
      message: '请求异常，请查看日志',
      type: 'error'
    })
  }
}
