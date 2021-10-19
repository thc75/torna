/*
注册全局方法
 */
import Vue from 'vue'
import {getToken, removeToken} from './auth'
import {do_get, get, get_baseUrl, get_file, post} from './http'
import {
  convert_tree,
  create_response_example,
  get_requestUrl,
  init_docInfo,
  init_docInfo_complete_view,
  init_docInfo_view,
  is_ding_talk
} from './common'
import {format_json} from '@/utils/format'
import {Enums} from './enums'
import {add_init} from './init'

const SPACE_ID_KEY = 'torna.spaceid'
const TORNA_FROM = 'torna.from'
const TORNA_PROJECT_CONFIG = 'torna.project.'

const pageSizeConfig = [5, 10, 20, 50, 100]

const typeConfig = [
  'string',
  'number',
  'boolean',
  'object',
  'array',
  'num_array',
  'str_array',
  'file',
  'file[]',
  'enum'
]

const baseTypeConfig = [
  'string',
  'number',
  'boolean'
]

let next_id = 1

let server_config

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
  do_get(uri, data, callback) {
    do_get.call(this, uri, data, callback)
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
  getEnums() {
    return Enums
  },
  getBaseUrl() {
    return get_baseUrl()
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
    get_file.call(this, path, callback)
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
    this.$confirm(msg, this.$ts('tip'), {
      confirmButtonText: this.$ts('ok'),
      cancelButtonText: this.$ts('cancel'),
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
    this.$alert(msg, title || this.$ts('tip'), {
      confirmButtonText: this.$ts('ok'),
      callback: action => {
        callback && callback.call(that, action)
      }
    })
  },
  /**
   * 新窗口打开
   * @param path 路由path
   */
  openLink(path) {
    // 如果是钉钉应用，不支持新窗口打开
    if (is_ding_talk()) {
      this.$router.push({ path: path })
    } else {
      // 新窗口打开
      const routeData = this.$router.resolve({ path: path })
      window.open(routeData.href, '_blank')
    }
  },
  nextId() {
    return next_id++
  },
  getTypeConfig() {
    return typeConfig
  },
  getPageSizeConfig() {
    return pageSizeConfig
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
    this.goLogin(url)
  },
  goHome() {
    this.goRoute('/dashboard')
  },
  goBack() {
    this.$router.go(-1)
  },
  /**
   * 获取服务端配置
   * @return 返回Promise
   */
  pmsConfig() {
    if (server_config) {
      return new Promise((resolve, reject) => {
        resolve(server_config)
      })
    }
    return new Promise((resolve, reject) => {
      get('/system/viewConfig', {}, resp => {
        server_config = resp.data
        resolve(resp.data)
      })
    })
  },
  loadEnumData(moduleId, callback) {
    this.get('/doc/enum/info/baselist', { moduleId: moduleId }, resp => {
      callback.call(this, resp.data)
    })
  },
  goLogin(url) {
    removeToken()
    // this.$router.replace({ path: `/login` })
    url = url || this.$route.fullPath
    if (url.indexOf('login?redirect') === -1) {
      this.$router.push({ path: `/login?redirect=${url}` })
    }
  },
  goSetPassword() {
    this.goRoute('/setPassword')
  },
  goRoute: function(path) {
    this.$router.push({ path: path })
  },
  initDocInfo(data) {
    return init_docInfo(data)
  },
  initDocInfoView(data) {
    return init_docInfo_view(data)
  },
  initDocInfoCompleteView(data) {
    return init_docInfo_complete_view(data)
  },
  /**
   * array转tree，必须要有id,parentId属性
   * @param arr 数组
   * @param parentId 父节点id，第一次调用传0
   * @returns {Array} 返回树array
   */
  convertTree: function(arr, parentId) {
    return convert_tree(arr, parentId)
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
  getRequestUrl(item) {
    return get_requestUrl(item)
  },
  /**
   * 导入参数
   * @param params 响应参数数组
   * @param json 当前json
   */
  doImportParam: function(params, json) {
    for (const name in json) {
      const value = json[name]
      let row = this.findRow(params, name)
      const isExist = row !== null
      if (!isExist) {
        row = this.getParamNewRow(name, value)
        this.pmsNextOrderIndex(params).then(order => {
          row.orderIndex = order
        })
      }
      row.example = value
      // 如果有子节点
      if (this.isObject(value)) {
        row.type = 'object'
        row.example = ''
        const children = row.children
        this.doImportParam(children, value)
        children.forEach(child => { child.parentId = row.id })
      } else if (this.isArray(value)) {
        row.type = 'array'
        row.example = ''
        const oneVal = value.length === 0 ? {} : value[0]
        if (this.isObject(oneVal)) {
          const children = row.children
          this.doImportParam(children, oneVal)
          children.forEach(child => { child.parentId = row.id })
        } else {
          row.example = JSON.stringify(value)
        }
      } else {
        // 单值
        row.type = typeof value
        row.example = String(value)
      }
      if (!isExist) {
        params.push(row)
      }
    }
  },
  findRow: function(params, name) {
    for (let i = 0; i < params.length; i++) {
      const r = params[i]
      if (r.name === name) {
        return r
      }
    }
    return null
  },
  getParamNewRow: function(name, value) {
    if (value === undefined) {
      value = ''
    }
    return {
      id: this.nextId(),
      name: name || '',
      type: 'string',
      required: 1,
      description: '',
      enumContent: '',
      maxLength: 64,
      example: value,
      isDeleted: 0,
      isNew: true,
      children: []
    }
  },
  doCreateResponseExample: function(params) {
    return create_response_example(params)
  },
  setSpaceId(id) {
    this.setAttr(SPACE_ID_KEY, id)
  },
  getSpaceId() {
    return this.getAttr(SPACE_ID_KEY)
  },
  setAttr: function(key, val) {
    if (val === undefined) {
      val = ''
    }
    localStorage.setItem(key, val + '')
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
  getCurrentProject() {
    return this.$store.state.settings.currentProject
  },
  /**
   * 返回项目首页地址
   * @param projectId 项目id
   * @param query url后面的参数，a=1&b=2
   * @returns {string} 返回地址
   */
  getProjectHomeUrl(projectId, query) {
    let url = `/project/doc/${projectId}`
    if (query) {
      if (!query.startsWith('?')) {
        query = '?' + query
      }
      url = url + query
    }
    return url
  },
  /**
   * 前往项目首页
   * @param projectId 项目id
   */
  goProjectHome(projectId) {
    this.goRoute(this.getProjectHomeUrl(projectId))
  },
  setCurrentSpace(space) {
    if (space) {
      this.setSpaceId(space.id)
    }
    this.$store.state.settings.currentSpace = space
  },
  getSpace() {
    return this.$store.state.settings.currentSpace
  },
  /**
   * 设置项目配置
   * @param projectId 项目id
   * @param config json对象，{ moduleId: 'xx', sidebar: 1 }
   */
  setProjectConfig(projectId, config) {
    const configObj = this.getProjectConfig(projectId)
    Object.assign(configObj, config)
    this.setAttr(this.getProjectConfigKey(projectId), JSON.stringify(configObj))
  },
  /**
   * 获取project配置
   * @param projectId
   * @returns object，json对象，{ moduleId: 'xx', sidebar: 1 }
   */
  getProjectConfig(projectId) {
    const key = this.getProjectConfigKey(projectId)
    const configStr = this.getAttr(key) || '{}'
    return JSON.parse(configStr)
  },
  getProjectConfigKey(projectId) {
    return TORNA_PROJECT_CONFIG + projectId
  },
  setTitle(title) {
    document.title = title
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
    this.get('/space/listNormal', {}, resp => {
      const data = resp.data
      let spaceId = ''
      const cacheId = this.getSpaceId()
      if (data.length > 0) {
        for (const space of data) {
          if (cacheId === space.id) {
            spaceId = cacheId
            break
          }
        }
        if (!spaceId) {
          spaceId = data[0].id
        }
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
  loadSpaceMember() {
    const searchData = {
      spaceId: this.getSpaceId()
    }
    return new Promise(resolve => {
      this.get('/space/member/all', searchData, resp => {
        resolve.call(this, resp.data)
      })
    })
  },
  formatJson(jsonObject) {
    return format_json(jsonObject)
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
      return index
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
    command && command()
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
  },
  addInit(fn) {
    add_init.call(this, fn)
  },
  hasNoParentAndChildren(row) {
    const children = row.children
    const noChildren = !children || children.length === 0
    return !row.parentId && noChildren
  },
  deepCopy(obj) {
    return JSON.parse(JSON.stringify(obj))
  },
  searchRow(search, rows, searchHandler, folderHandler) {
    if (!folderHandler) {
      folderHandler = (row) => {
        return row.isFolder === 1
      }
    }
    const ret = []
    for (const row of rows) {
      // 找到分类
      if (folderHandler(row)) {
        if (searchHandler(search, row)) {
          ret.push(row)
        } else {
          // 分类名字没找到，需要从子文档中找
          const children = row.children || [];
          const searchedChildren = this.searchRow(search, children, searchHandler, folderHandler);
          // 如果子文档中有
          if (searchedChildren && searchedChildren.length > 0) {
            const rowCopy = Object.assign({}, row)
            rowCopy.children = searchedChildren
            ret.push(rowCopy)
          }
        }
      } else {
        // 不是分类且被找到
        if (searchHandler(search, row)) {
          ret.push(row)
        }
      }
    }
    return ret
  },
  async pmsNextOrderIndex(children) {
    const config = await this.pmsConfig()
    if (!children || children.length === 0) {
      return config.initOrder
    }
    let max = config.initOrder
    children.forEach(row => {
      max = Math.max(row.orderIndex, max)
    })
    return max + 10
  }
})

const formatMoney = function(cellValue) {
  return '￥' + (cellValue / 100).toFixed(2)
}
