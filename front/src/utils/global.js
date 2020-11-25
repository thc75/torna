/*
注册全局方法
 */
import Vue from 'vue'
import { getToken, removeToken } from './auth'
// import ClipboardJS from 'clipboard'
import needle from 'needle'
import axios from 'axios'

const baseURL = process.env.VUE_APP_BASE_API || `${location.protocol}//${location.host}`
const OPC_USER_TYPE_KEY = 'torna-user-type'
const SPACE_ID_KEY = 'torna-spaceid'

let paramIdGen = 0

// 创建axios实例
const client = axios.create({
  baseURL: baseURL, // api 的 base_url
  timeout: 60000 // 请求超时时间,60秒
})

Object.assign(Vue.prototype, {
  /**
   * GET请求接口
   * @param uri uri
   * @param data 请求数据
   * @param callback 成功时回调
   * @param errorCallback 失败时回调
   */
  get: function(uri, data, callback, errorCallback) {
    const that = this
    needle.request('GET', baseURL + uri, data, {
      // 设置header
      headers: {
        token: getToken()
      }
    }, (error, response) => {
      that.doResponse(error, response, callback, errorCallback)
    })
  },
  /**
   * 请求接口
   * @param uri uri
   * @param data 请求数据
   * @param callback 成功时回调
   * @param errorCallback 失败时回调
   */
  post: function(uri, data, callback, errorCallback) {
    const that = this
    needle.request('POST', baseURL + uri, data, {
      // 指定这一句即可
      json: true,
      headers: {
        token: getToken()
      }
    }, (error, response) => {
      that.doResponse(error, response, callback, errorCallback)
    })
  },
  request(method, uri, data, headers, isJson, isForm, files, callback) {
    // 如果是文件上传，使用axios，needle上传文件不完美，不支持一个name对应多个文件
    if (files && files.length > 0) {
      this.doMultipart(uri, data, files, headers, callback)
      return
    }
    const that = this
    if (isForm) {
      headers['Content-Type'] = 'application/x-www-form-urlencoded'
    }
    needle.request(method, baseURL + uri, data, {
      // 设置header
      headers: headers,
      json: isJson
    }, (error, response) => {
      callback.call(that, error, response)
    })
  },
  doMultipart(uri, data, files, headers, callback) {
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
  },
  doResponse(error, response, callback, errorCallback) {
    // 成功
    if (!error && response.statusCode === 200) {
      const resp = response.body
      const code = resp.code
      // 未登录
      if (code === '9') {
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
  },
  loadRole: function(callback) {
    this.get('isp.role.list', {}, resp => {
      callback && callback.call(this, resp.data)
    })
  },
  /**
   *  文件必须放在public下面
   * @param path 相对于public文件夹路径，如文件在public/static/sign.md，填：static/sign.md
   * @param callback 回调函数，函数参数是文件内容
   */
  getFile: function(path, callback) {
    axios.get(path)
      .then(function(response) {
        callback.call(this, response.data)
      })
  },
  /**
   * ajax请求，并下载文件
   * @param uri 请求path
   * @param params 请求参数，json格式
   * @param filename 文件名称
   */
  downloadFile: function(uri, params, filename) {
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
  },
  /**
   * tip，使用方式：this.tip('操作成功')，this.tip('错误', 'error')
   * @param msg 内容
   * @param type success / info / warning / error
   */
  tip: function(msg, type) {
    this.$message({
      message: msg,
      type: type || 'success'
    })
  },
  tipSuccess: function(msg) {
    this.tip(msg, 'success')
  },
  tipError: function(msg) {
    this.tip(msg, 'error')
  },
  tipInfo: function(msg) {
    this.tip(msg, 'info')
  },
  /**
   * 提醒框
   * @param msg 消息
   * @param okHandler 成功回调
   * @param cancelHandler
   */
  confirm: function(msg, okHandler, cancelHandler) {
    const that = this
    this.$confirm(msg, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      okHandler.call(that)
    }).catch(() => {
      cancelHandler && cancelHandler.call(that)
    })
  },
  /**
   * 提示框
   * <pre>
   * this.alert('注册成功', '提示', function() {
      this.goRoute(`/login`)
     })
   * </pre>
   * @param msg
   * @param title
   * @param callback
   */
  alert: function(msg, title, callback) {
    const that = this
    this.$alert(msg, title || '提示', {
      confirmButtonText: '确定',
      callback: action => {
        callback && callback.call(that, action)
      }
    })
  },
  /**
   * 重置表单
   * @param formName 表单元素的ref
   */
  resetForm(formName) {
    const frm = this.$refs[formName]
    frm && frm.resetFields()
  },
  logout: function() {
    this.get('/portal/common/logout', {}, resp => {}, resp => {})
    this.goLogin()
  },
  goHome() {
    this.goRoute('/dashboard')
  },
  goLogin() {
    removeToken()
    // this.$router.replace({ path: `/login` })
    this.$router.push(`/login?redirect=${this.$route.fullPath}`)
  },
  goRoute: function(path) {
    this.$router.push({ path: path })
  },
  /**
   * array转tree，必须要有id,parentId属性
   * @param arr 数组
   * @param parentId 父节点id，第一次调用传0
   * @returns {Array} 返回树array
   */
  convertTree: function(arr, parentId) {
    if (!parentId) {
      parentId = ''
    }
    if (!arr) {
      return []
    }
    // arr是返回的数据parentId父id
    const temp = []
    const treeArr = arr
    treeArr.forEach((item, index) => {
      if (item.parentId === parentId) {
        // 递归调用此函数
        treeArr[index].children = this.convertTree(treeArr, treeArr[index].id)
        temp.push(treeArr[index])
      }
    })
    return temp
  },
  /**
   * 将树转换成行，convertTree的反操作
   * @param treeData
   * @returns {[]}
   */
  unConvertTree(treeData) {
    let ret = []
    for (let i = 0; i < treeData.length; i++) {
      let arr = []
      const data = treeData[i]
      arr.push(data)
      if (data.children && data.children.length > 0) {
        const childrenData = this.unConvertTree(data.children)
        arr = arr.concat(childrenData)
      }
      ret = ret.concat(arr)
    }
    return ret
  },
  getParamNewRow: function(name, value) {
    return {
      id: paramIdGen++,
      name: name || '',
      type: 'string',
      required: 1,
      description: '',
      enumContent: '',
      maxLength: 64,
      example: value || '',
      isDeleted: 0,
      isNew: true,
      children: []
    }
  },
  setSpaceId(id) {
    this.$store.state.settings.spaceId = id
    this.setAttr(SPACE_ID_KEY, id)
  },
  getSpaceId() {
    return this.getAttr(SPACE_ID_KEY)
  },
  setAttr: function(key, val) {
    if (val) {
      localStorage.setItem(key, val + '')
    }
  },
  getAttr: function(key) {
    return localStorage.getItem(key)
  },
  setUserType: function(type) {
    this.setAttr(OPC_USER_TYPE_KEY, type)
  },
  /**
   * 触发事件
   * @param eventName
   * @param val
   */
  fireEvent(eventName, val) {
    this.$store.state.event[eventName] = val
  },
  loadSpaceData(callback) {
    this.get('/space/list', {}, resp => {
      const data = resp.data
      let spaceId = ''
      const cacheId = this.getSpaceId()
      if (cacheId) {
        spaceId = cacheId
      }
      // 没有选中就选择第一个
      if (!spaceId && data[0].length > 0) {
        spaceId = data[0].id
      }
      callback && callback.call(this, data, spaceId)
    })
  },
  /**
   * 是否是admin
   * @returns {boolean}
   */
  isAdmin: function() {
    return true
  },
  cellStyleSmall: function() {
    return { padding: '5px 0' }
  },
  headCellStyleSmall: function() {
    return { padding: '5px 0' }
  },
  loadSpaceMember(searchData) {
    searchData.spaceId = this.getSpaceId()
    return new Promise(resolve => {
      this.get('/space/member/search', searchData, resp => {
        resolve(resp.data)
      })
    })
  },
  formatHost(obj) {
    let url = obj.url
    if (url) {
      url = obj.url.trim()
      if (url.endsWith('/')) {
        url = url.substring(0, url.length - 1)
      }
      obj.url = url
    }
  },
  removeRow: function(arr, id) {
    let index = -1
    for (let i = 0; i < arr.length; i++) {
      if (arr[i].id === id) {
        index = i
        break
      }
    }
    // 找到元素，删除
    if (index >= 0) {
      arr.splice(index, 1)
    } else {
      // 如果没有找到，则查找子节点
      for (let i = 0; i < arr.length; i++) {
        const children = arr[i].children
        if (children) {
          this.removeRow(children, id)
        }
      }
    }
  },
  /**
   * 验证表格
   * @param arr 表格内容
   * @param refPrefixArr ref前缀数组
   * @returns {[]}
   */
  validateTable: function(arr, refPrefixArr) {
    let promiseArr = []
    for (let i = 0; i < arr.length; i++) {
      const row = arr[i]
      if (row.isDeleted) {
        continue
      }
      const id = row.id
      refPrefixArr.forEach(refPrefix => {
        promiseArr.push(this.$refs[refPrefix + id].validate())
      })
      const children = arr[i].children
      if (children && children.length > 0) {
        const childrenPromiseArr = this.validateTable(children, refPrefixArr)
        promiseArr = promiseArr.concat(childrenPromiseArr)
      }
    }
    return promiseArr
  },
  parseJSON: function(str, callback, errorCallback) {
    let isJson = false
    if (typeof str === 'string') {
      try {
        const obj = JSON.parse(str)
        isJson = (typeof obj === 'object') && obj
        if (isJson) {
          callback.call(this, obj)
        }
      } catch (e) {
        isJson = false
      }
    }
    if (!isJson) {
      errorCallback.call(this)
    }
  },
  handleCommand: function(command) {
    command()
  },
  isObject: function(obj) {
    return Object.prototype.toString.call(obj) === '[object Object]'
  },
  isArray: function(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]'
  },
  formatterMoney: function(row, column, cellValue, index) {
    return formatMoney(cellValue)
  },
  formatMoney: function(cellValue) {
    return formatMoney(cellValue)
  },
  formatDate: function(time) {
    const y = time.getFullYear()
    const m = time.getMonth() + 1
    const d = time.getDate()
    const h = time.getHours()
    const mm = time.getMinutes()
    const s = time.getSeconds()
    return `${y}-${this._add0(m)}-${this._add0(d)} ${this._add0(h)}:${this._add0(mm)}:${this._add0(s)}`
  },
  _add0: function(m) {
    return m < 10 ? '0' + m : m
  }
})

const formatMoney = function(cellValue) {
  return '￥' + (cellValue / 100).toFixed(2)
}
