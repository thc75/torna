import {
  convert_tree,
  create_response_example,
  get_effective_url,
  get_style_config, init_docInfo,
  StringBuilder
} from '@/utils/common'
import { isDubbo, isHttp, isShowRequestExample } from '@/utils/convert-common'
import { Enums } from '@/utils/enums'

export const word_wrapper = `
<html xmlns:v="urn:schemas-microsoft-com:vml"
    xmlns:o="urn:schemas-microsoft-com:office:office"
    xmlns:w="urn:schemas-microsoft-com:office:word"
    xmlns="http://www.w3.org/TR/REC-html40">
    <head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <style type="text/css">
        table
        {
            border-collapse: collapse;
            border: none;
            width: 100%;
        }
        td,tr
        {
            border: solid #CCC 1px;
            padding:3px;
            font-size:9pt;
        }
        .code-style{
            word-break: break-all;
            mso-highlight:rgb(252, 252, 252);
            padding-left: 5px; background-color: rgb(252, 252, 252); border: 1px solid rgb(225, 225, 232);
        }
    </style>
    <meta name="ProgId" content="Word.Document">
    <meta name="Generator" content="Microsoft Word 11">
    <meta name="Originator" content="Microsoft Word 11">
    <xml><w:WordDocument><w:View>Print</w:View></xml></head>
    <body>{body}</body></html>`

const WordUtil = {
  handleWordData(treeData, content, level = 1) {
    const appendHtml = (doc_info, level) => {
      init_docInfo(doc_info)
      content.append(WordUtil.toWord(doc_info, level))
    }
    treeData.forEach(docInfo => {
      const children = docInfo.children
      if (children && children.length > 0) {
        content.append(`<h${level}>${docInfo.name}</h${level}>`)
        this.handleWordData(children, content, level + 1)
      } else {
        appendHtml(docInfo, level)
      }
    })
  },
  toWordByData(docInfoList) {
    const treeData = convert_tree(docInfoList, '')
    const content = new StringBuilder()
    this.handleWordData(treeData, content)
    return content.toString()
  },
  toWord(docInfo, level = 2) {
    const sb = new StringBuilder()
    sb.append(`<h${level}>${docInfo.name}</h${level}>`)
    const appendCode = (str) => {
      sb.append('<table width="100%" class="code-style">')
      sb.append(`\n<pre><code class="language-json">\n${str}\n</code></pre>\n`)
      sb.append('</table>')
    }
    // 维护人
    docInfo.author && sb.append(`<p><strong>${$ts('maintainer')}：</strong>${docInfo.author}</p>`)
    // URL
    if (isHttp(docInfo)) {
      sb.append(`<p><strong>URL</strong></p>`)
      const debugEnvs = docInfo.debugEnvs || []
      if (debugEnvs.length > 0) {
        const ul = new StringBuilder('<ul>')
        docInfo.debugEnvs.forEach(hostConfig => {
          const baseUrl = hostConfig.url
          const url = get_effective_url(baseUrl, docInfo.url)
          ul.append(`<li>${hostConfig.name}: ${docInfo.httpMethod} ${url}</li>`)
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

    isHttp(docInfo) && sb.append(`<p><strong>ContentType：</strong>${docInfo.contentType}</p>`)

    if (isHttp(docInfo)) {
      if (docInfo.pathParams && docInfo.pathParams.length > 0) {
        sb.append(`<h4>${$ts('pathVariable')}</h4>`)
        sb.append(createTable(docInfo.pathParams, Enums.PARAM_STYLE.path))
      }
      if (docInfo.headerParams && docInfo.headerParams.length > 0) {
        sb.append(`<h4>${$ts('requestHeader')}</h4>`)
        sb.append(createTable(docInfo.headerParams, Enums.PARAM_STYLE.header))
      }
    }
    sb.append(`<h4>${$ts('requestParams')}</h4>`)
    if (docInfo.queryParams && docInfo.queryParams.length > 0) {
      sb.append('<h5>Query Parameter</h5>')
      sb.append(createTable(docInfo.queryParams, Enums.PARAM_STYLE.request))
    }
    if (docInfo.requestParams && docInfo.requestParams.length > 0) {
      sb.append('<h5>Body Parameter</h5>')
      sb.append(createTable(docInfo.requestParams, Enums.PARAM_STYLE.request))
    }
    if (isShowRequestExample(docInfo)) {
      sb.append(`<h4>${$ts('requestExample')}</h4>`)
      const requestExample = create_response_example(docInfo.requestParams)
      appendCode(JSON.stringify(requestExample, null, 4))
    }
    sb.append(`<h4>${$ts('responseParam')}</h4>`)
    sb.append(createTable(docInfo.responseParams, Enums.PARAM_STYLE.response))

    if (isHttp(docInfo)) {
      sb.append(`<h4>${$ts('responseExample')}</h4>`)
      const responseExample = create_response_example(docInfo.responseParams)
      appendCode(JSON.stringify(responseExample, null, 4))
    }

    sb.append(`<h4>${$ts('errorCode')}</h4>`)
    sb.append(createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code))
    return sb.toString()
  }
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
  const headerContent = `<thead>\n${createHeader(headers)}</thead>`
  const trContent = `<tbody>\n${createBody(params, rowConfig)}</tbody>`
  const tableContent = [
    '<table>\n',
    headerContent,
    trContent,
    '\n</table>'
  ]
  return tableContent.join('')
}

function createHeader(tds) {
  const thHtml = []
  tds.forEach(content => {
    thHtml.push(thWrapper(content))
  })
  return `<tr style="background-color: rgb(0, 136, 204); color: rgb(255, 255, 255);">\n${thHtml.join('')}\n</tr>`
}

const thWrapper = (content) => {
  return `<th style="text-align: left;"><strong>${content}</strong></th>\n`
}

function createBody(params, rowConfig, prefix = '', level = 1) {
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

function createBodyTr(tds, prefix, level) {
  if (level > 1) {
    const padding = []
    console.log('level:', level)
    console.log('cishu :', Math.pow(level, 2))
    for (let i = 1; i < level; i++) {
      padding.push('&nbsp;')
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
  return `<tr>\n${tdHtml.join('')}\n</tr>`
}

const tdWrapper = (content) => {
  return `<td style="text-align: left;">${content}</td>\n`
}
export default WordUtil
