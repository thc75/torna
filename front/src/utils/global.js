/*
注册全局方法
 */
import Vue from 'vue'
import { getToken, removeToken } from './auth'
import { get, post, getBaseUrl, getFile, doGet } from './http'

const SPACE_ID_KEY = 'torna-spaceid'
const TORNA_FROM = 'torna-from'
const typeConfig = [
  'string',
  'number',
  'boolean',
  'array',
  'object',
  'file'
]

const baseTypeConfig = [
  'string',
  'number',
  'boolean'
]

let paramIdGen = 0

Object.assign(Vue.prototype, {
  /**
   * GET请求接口
   * @param uri uri
   * @param data 请求数据
   * @param callback 成功时回调
   * @param errorCallback 失败时回调
   */
  get: function(uri, data, callback, errorCallback) {
    get.call(this, uri, data, callback, errorCallback)
  },
  /**
   * get请求，自定义callback
   * @param uri
   * @param data
   * @param callback
   */
  doGet(uri, data, callback) {
    doGet.call(this, uri, data, callback)
  },
  /**
   * 请求接口
   * @param uri uri
   * @param data 请求数据
   * @param callback 成功时回调
   * @param errorCallback 失败时回调
   */
  post: function(uri, data, callback, errorCallback) {
    post.call(this, uri, data, callback, errorCallback)
  },
  getBaseUrl() {
    return getBaseUrl()
  },
  getUserId() {
    const token = getToken()
    if (token && token.indexOf(':') > -1) {
      return token.split(':')[0]
    }
    return ''
  },
  isSelf(userId) {
    return this.getUserId() === userId
  },
  /**
   *  文件必须放在public下面
   * @param path 相对于public文件夹路径，如文件在public/static/sign.md，填：static/sign.md
   * @param callback 回调函数，函数参数是文件内容
   */
  getFile: function(path, callback) {
    getFile.call(this, path, callback)
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
  getTypeConfig() {
    return typeConfig
  },
  getBaseTypeConfig() {
    return baseTypeConfig
  },
  /**
   * 重置表单
   * @param formName 表单元素的ref
   */
  resetForm(formName) {
    const frm = this.$refs[formName]
    frm && frm.resetFields()
  },
  logout: function(url) {
    this.get('/system/logout', {}, resp => {}, resp => {})
    this.goLogin(url)
  },
  goHome() {
    this.goRoute('/dashboard')
  },
  goBack() {
    this.$router.go(-1)
  },
  goLogin(url) {
    removeToken()
    // this.$router.replace({ path: `/login` })
    url = url || this.$route.fullPath
    if (url.indexOf('login?redirect') === -1) {
      this.$router.push({ path: `/login?redirect=${url}` })
    }
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
  doCreateResponseExample: function(params) {
    const responseJson = {}
    params.forEach(row => {
      let val
      // 如果有子节点
      if (row.children && row.children.length > 0) {
        const childrenValue = this.doCreateResponseExample(row.children)
        // 如果是数组
        if (row.type.toLowerCase() === 'array') {
          val = [childrenValue]
        } else {
          val = childrenValue
        }
      } else {
        // 单值
        val = row.example
      }
      responseJson[row.name] = val
    })
    return responseJson
  },
  setSpaceId(id) {
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
  setSessionAttr(key, val) {
    if (val) {
      sessionStorage.setItem(key, val)
    }
  },
  getSessionAttr(key) {
    return sessionStorage.getItem(key)
  },
  setCurrentProject(project) {
    this.$store.state.settings.currentProject = project
  },
  setCurrentSpace(space) {
    this.$store.state.settings.currentSpace = space
  },
  setFrom(from) {
    this.setSessionAttr(TORNA_FROM, encodeURIComponent(JSON.stringify(from)))
  },
  getFrom() {
    try {
      const from = this.getSessionAttr(TORNA_FROM)
      const jsonStr = decodeURIComponent(from)
      return JSON.parse(jsonStr)
    } catch (e) {
      console.error(e)
      return ''
    }
  },
  setCurrentInfo(space, project) {
    this.setCurrentSpace(space)
    this.setCurrentProject(project)
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
      if (!spaceId && data.length > 0) {
        spaceId = data[0].id
      }
      callback && callback.call(this, data, spaceId)
    })
  },
  loadEnumItem(enumId) {
    return new Promise((resolve, reject) => {
      this.get('/doc/enum/item/list', { enumId: enumId }, resp => {
        resolve(resp.data)
      }, resp => {
        reject(resp)
      })
    })
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
