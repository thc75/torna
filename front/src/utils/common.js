/**
 * 构建返回结果例子
 * @param params 返回结果定义
 * @returns {{}} 返回json对象
 */
export function create_response_example(params) {
  const responseJson = {}
  params.forEach(row => {
    let val
    // 如果有子节点
    if (row.children && row.children.length > 0) {
      const childrenValue = create_response_example(row.children)
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
}

export function get_requestUrl(item) {
  let url = item.url
  if (!url) {
    return ''
  }
  if (url.startsWith('/')) {
    url = url.substring(1)
  }
  let baseUrl = item.baseUrl
  if (baseUrl && baseUrl.endsWith('/')) {
    baseUrl = baseUrl.substring(0, baseUrl.length)
  }
  return `${baseUrl}/${url}`
}

/**
 * array转tree，必须要有id,parentId属性
 * @param arr 数组
 * @param parentId 父节点id，第一次调用传0
 * @returns {Array} 返回树array
 */
export function convert_tree(arr, parentId) {
  if (!parentId) {
    parentId = ''
  }
  if (!arr) {
    return []
  }
  // arr是返回的数据parentId父id
  const temp = []
  const treeArr = arr
  treeArr.forEach(item => {
    if (item.parentId === parentId) {
      // 递归调用此函数
      item.children = convert_tree(treeArr, item.id)
      temp.push(item)
    }
  })
  return temp
}

export function init_docInfo(data) {
  data.requestParams = convert_tree(data.requestParams)
  data.responseParams = convert_tree(data.responseParams)
  return data
}

export function StringBuilder(str) {
  this.arr = []
  if (str) {
    this.arr.push(str)
  }
}
StringBuilder.prototype = {
  append(str) {
    this.arr.push(str)
    return this
  },
  toString() {
    return this.arr.join('')
  }
}

export function format_string(pattern, args) {
  let result = pattern
  for (const key in args) {
    const reg = new RegExp('({' + key + '})', 'g')
    result = result.replace(reg, args[key])
  }
  return result
}

/**
 * 下载文本内容
 * @param filename 文件名
 * @param text 文本内容
 */
export function download_text(filename, text) {
  const element = document.createElement('a')
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text))
  element.setAttribute('download', filename)

  element.style.display = 'none'
  document.body.appendChild(element)

  element.click()

  document.body.removeChild(element)
}

export const style_config = {
  '0': [
    { prop: 'name', label: '名称' },
    { prop: 'required', label: '必须' },
    { prop: 'description', label: '描述' },
    { prop: 'example', label: '示例值' }
  ],
  '1': [
    { prop: 'name', label: '名称' },
    { prop: 'required', label: '必须' },
    { prop: 'description', label: '描述' },
    { prop: 'example', label: '示例值' }
  ],
  '2': [
    { prop: 'name', label: '名称' },
    { prop: 'type', label: '类型' },
    { prop: 'required', label: '必须' },
    { prop: 'maxLength', label: '最大长度' },
    { prop: 'description', label: '描述' },
    { prop: 'example', label: '示例值' }
  ],
  '3': [
    { prop: 'name', label: '名称' },
    { prop: 'type', label: '类型' },
    { prop: 'required', label: '必须' },
    { prop: 'maxLength', label: '最大长度' },
    { prop: 'description', label: '描述' },
    { prop: 'example', label: '示例值' }
  ],
  '4': [
    { prop: 'name', label: '错误码' },
    { prop: 'description', label: '错误描述' },
    { prop: 'example', label: '解决方案' }
  ]
}
