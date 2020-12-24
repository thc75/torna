import { Enums } from './enums'

const split_char = ' | '

function getRequestUrl(item) {
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

function joinHeader(arr) {
  const len = arr.length
  const splitArr = []
  for (let i = 0; i < len; i++) {
    splitArr.push('---')
  }
  return createTr(arr.join(split_char)) + '\n' + createTr(splitArr.join(split_char))
}

function createTr(row, prefix) {
  if (!prefix) {
    prefix = ''
  }
  return `| ${prefix}${row} |`
}

const style_config = {
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

function createTrContent(params, rowConfig, prefix) {
  let rows = []
  for (const param of params) {
    const row = []
    for (const config of rowConfig) {
      let value = param[config.prop]
      if (config.prop === 'required') {
        value = value ? '是' : '否'
      }
      row.push(value)
    }
    rows.push(createTr(row.join(split_char), prefix))
    if (param.children && param.children.length > 0) {
      const childrenRows = createTrContent(param.children, rowConfig, '  └ ')
      rows = rows.concat(childrenRows)
    }
  }
  return rows.join('\n')
}

function createTable(params, style) {
  const rowConfig = style_config[style + '']
  if (!rowConfig) {
    return ''
  }
  const header = rowConfig.map(config => config.label)
  const headerContent = joinHeader(header) + '\n'
  const trContent = createTrContent(params, rowConfig)
  const tableContent = [headerContent, trContent]
  return tableContent.join('')
}

const MarkdownUtil = {
  toMarkdown(docInfo) {
    const html = []
    const append = (str) => {
      html.push(`\n${str}\n`)
    }
    append(`# ${docInfo.name}`)
    append(`URL：${docInfo.httpMethod} ${getRequestUrl(docInfo)}`)
    append(`描述：${docInfo.description}`)
    append(`ContentType：${docInfo.contentType}`)
    append(`## Path参数`)
    const pathParamsTable = createTable(docInfo.pathParams, Enums.PARAM_STYLE.path)
    append(pathParamsTable)

    append(`## 请求Header`)
    const headerParamsTable = createTable(docInfo.headerParams, Enums.PARAM_STYLE.header)
    append(headerParamsTable)

    append(`## 请求参数`)
    const requestParamsTable = createTable(docInfo.requestParams, Enums.PARAM_STYLE.request)
    append(requestParamsTable)

    append(`## 响应参数`)
    const responseParamsTable = createTable(docInfo.responseParams, Enums.PARAM_STYLE.response)
    append(responseParamsTable)

    append(`## 错误码`)
    const errorCodeParamsTable = createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code)
    append(errorCodeParamsTable)
    return html.join('')
  }
}

export default MarkdownUtil
