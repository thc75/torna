import { Enums } from './enums'
import {
  convert_tree,
  create_response_example,
  get_effective_url,
  init_docInfo,
  StringBuilder,
  get_style_config
} from './common'

import { isDubbo, isHttp, isShowRequestExample } from './convert-common'

const BLANK = '&nbsp;'

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
    const padding = []
    for (let i = 1; i < Math.pow(level, 2); i++) {
      padding.push(BLANK)
    }
    prefix = padding.join('') + prefix
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
        value = value ? $ts('yes') : $ts('no')
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
    return $ts('empty')
  }
  const rowConfig = get_style_config()[style + '']
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
  toHtmlByData(docInfoList, title) {
    title = title || $ts('document')
    const treeData = convert_tree(docInfoList)
    // 一级标题
    const content = new StringBuilder(`<h1>${title}</h1>`)
    const appendHtml = (doc_info) => {
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
          appendHtml(child)
        })
      } else {
        appendHtml(docInfo)
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
      sb.append(`\n<pre class="code-block">\n${str}\n</pre>\n`)
    }
    sb.append('<div class="doc-item">')
      .append(`<h3 id="${docInfo.id}">${link(docInfo.id, docInfo.name)}</h3>`)
    // 维护人
    if (docInfo.author) {
      sb.append(`<p><strong>${$ts('maintainer')}：</strong>${docInfo.author}</p>`)
    }
    // URL
    if (isHttp(docInfo)) {
      sb.append(`<p><strong>URL</strong></p>`)
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
    } else if (isDubbo(docInfo)) {
      sb.append(`<p><strong>${$ts('method')}：</strong>${docInfo.url}</p>`)
    }
    // 描述
    sb.append(`<p><strong>${$ts('description')}：</strong>${docInfo.description}</p>`)

    if (isHttp(docInfo)) {
      sb.append(`<p><strong>ContentType：</strong>${docInfo.contentType}</p>`)
    }

    if (isHttp(docInfo)) {
      if (docInfo.pathParams && docInfo.pathParams.length > 0) {
        sb.append(`<h4>${$ts('pathVariable')}</h4>`)
        const pathParamsTable = createTable(docInfo.pathParams, Enums.PARAM_STYLE.path)
        sb.append(pathParamsTable)
      }
      if (docInfo.headerParams && docInfo.headerParams.length > 0) {
        sb.append(`<h4>${$ts('requestHeader')}</h4>`)
        const headerParamsTable = createTable(docInfo.headerParams, Enums.PARAM_STYLE.header)
        sb.append(headerParamsTable)
      }
    }

    sb.append(`<h4>${$ts('requestParams')}</h4>`)
    if (docInfo.queryParams && docInfo.queryParams.length > 0) {
      sb.append('<h5>Query Parameter</h5>')
      const queryParamsTable = createTable(docInfo.queryParams, Enums.PARAM_STYLE.request)
      sb.append(queryParamsTable)
    }
    if (docInfo.requestParams && docInfo.requestParams.length > 0) {
      sb.append('<h5>Body Parameter</h5>')
      const requestParamsTable = createTable(docInfo.requestParams, Enums.PARAM_STYLE.request)
      sb.append(requestParamsTable)
    }

    if (isShowRequestExample(docInfo)) {
      sb.append(`<h4>${$ts('requestExample')}</h4>`)
      const requestExample = create_response_example(docInfo.requestParams)
      appendCode(JSON.stringify(requestExample, null, 4))
    }

    sb.append(`<h4>${$ts('responseParam')}</h4>`)
    const responseParamsTable = createTable(docInfo.responseParams, Enums.PARAM_STYLE.response)
    sb.append(responseParamsTable)

    if (isHttp(docInfo)) {
      sb.append(`<h4>${$ts('responseExample')}</h4>`)
      const responseExample = create_response_example(docInfo.responseParams)
      appendCode(JSON.stringify(responseExample, null, 4))
    }

    sb.append(`<h4>${$ts('errorCode')}</h4>`)
    const errorCodeParamsTable = createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code)
    sb.append(errorCodeParamsTable)
    sb.append('</div>') // doc-item end
    return sb.toString()
  }
}

export default HtmlUtil
