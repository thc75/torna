import { Enums } from './enums'
import {
  convert_tree,
  create_response_example,
  init_docInfo,
  StringBuilder,
  style_config,
  get_effective_url
} from './common'

const thWrapper = (content) => {
  return `<th>${content}</th>`
}

const tdWrapper = (content) => {
  return `<td>${content}</td>`
}

function createHeader(tds) {
  const thHtml = []
  tds.forEach(content => {
    thHtml.push(thWrapper(content))
  })
  return `<tr>${thHtml.join('')}<tr>`
}

function createBodyTr(tds, prefix, level) {
  if (level > 1) {
    for (let i = 1; i < level; i++) {
      prefix = '&nbsp;&nbsp;' + prefix
    }
  }
  const tdHtml = []
  tds.forEach((content, index) => {
    if (index === 0) {
      content = prefix + content
    }
    tdHtml.push(tdWrapper(content))
  })
  return `<tr>${tdHtml.join('')}<tr>`
}

function createBody(params, rowConfig, prefix, level) {
  if (!prefix) {
    prefix = ''
  }
  if (!level) {
    level = 1
  }
  let rows = []
  for (const param of params) {
    const tds = []
    for (const config of rowConfig) {
      let value = param[config.prop]
      if (config.prop === 'required') {
        value = value ? '是' : '否'
      }
      tds.push(value)
    }
    rows.push(createBodyTr(tds, prefix, level))
    if (param.children && param.children.length > 0) {
      const childrenRows = createBody(param.children, rowConfig, '└ ', level + 1)
      rows = rows.concat(childrenRows)
    }
  }
  return rows.join('\n')
}

function createTable(params, style) {
  if (!params || params.length === 0) {
    return '无'
  }
  const rowConfig = style_config[style + '']
  if (!rowConfig) {
    return ''
  }
  const headers = rowConfig.map(config => config.label)
  const headerContent = `<thead>${createHeader(headers)}</thead>`
  const trContent = `<tbody>${createBody(params, rowConfig)}</tbody>`
  const tableContent = [
    '<table class="pure-table pure-table-bordered">',
    headerContent,
    trContent,
    '</table>'
  ]
  return tableContent.join('')
}

const HtmlUtil = {
  convertModule(moduleDTO) {
    const docInfoList = moduleDTO.docInfoList
    const treeData = convert_tree(docInfoList)
    // 一级标题
    const content = new StringBuilder(`<h1>${moduleDTO.name}</h1>`)
    const appendMarkdown = (doc_info) => {
      init_docInfo(doc_info)
      const html = HtmlUtil.toHtml(doc_info)
      content.append(html)
    }
    treeData.forEach(docInfo => {
      const children = docInfo.children
      const isFolder = children && children.length > 0
      if (isFolder) {
        // 二级标题
        content.append(`<h2>${docInfo.name}</h2>`)
        children.forEach(child => {
          appendMarkdown(child)
        })
      } else {
        appendMarkdown(docInfo)
      }
    })
    return content.toString()
  },
  toHtml(docInfo) {
    const sb = new StringBuilder()
    const link = (id, name) => {
      return `<a class="link" href="#${id}">&nbsp;${name}</a>`
    }
    const appendCode = (str) => {
      sb.append(`\n<pre>\n${str}\n</pre>\n`)
    }
    sb.append('<div class="doc-item">')
      .append(`<h3 id="${docInfo.id}">${link(docInfo.id, docInfo.name)}</h3>`)
      .append(`<p><strong>URL</strong></p>`)
    const debugEnvs = docInfo.debugEnvs || []
    if (debugEnvs.length > 0) {
      const ul = new StringBuilder('<ul>')
      docInfo.debugEnvs.forEach(hostConfig => {
        const baseUrl = hostConfig.configValue
        const url = get_effective_url(baseUrl, docInfo.url)
        ul.append(`<li>${hostConfig.configKey}: ${docInfo.httpMethod} ${url}</li>`)
      })
      ul.append('</ul>')
      sb.append(ul.toString())
    } else {
      sb.append(`<span>${docInfo.httpMethod} ${docInfo.url}</span>`)
    }
    sb.append(`<p><strong>描述：</strong>${docInfo.description}</p>`)
      .append(`<p><strong>ContentType：</strong>${docInfo.contentType}</p>`)
    sb.append('<h4>Path参数</h4>')
    const pathParamsTable = createTable(docInfo.pathParams, Enums.PARAM_STYLE.path)
    sb.append(pathParamsTable)
    sb.append('<h4>请求Header</h4>')
    const headerParamsTable = createTable(docInfo.headerParams, Enums.PARAM_STYLE.header)
    sb.append(headerParamsTable)

    sb.append('<h4>请求参数</h4>')
    const requestParamsTable = createTable(docInfo.requestParams, Enums.PARAM_STYLE.request)
    sb.append(requestParamsTable)

    sb.append('<h4>响应参数</h4>')
    const responseParamsTable = createTable(docInfo.responseParams, Enums.PARAM_STYLE.response)
    sb.append(responseParamsTable)

    sb.append('<h4>响应示例</h4>')
    const responseExample = create_response_example(docInfo.responseParams)
    appendCode(JSON.stringify(responseExample, null, 4))

    sb.append('<h4>错误码</h4>')
    const errorCodeParamsTable = createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code)
    sb.append(errorCodeParamsTable)
    sb.append('</div>') // doc-item end
    return sb.toString()
  }
}

export default HtmlUtil
