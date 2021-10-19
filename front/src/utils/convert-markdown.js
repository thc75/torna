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

const split_char = ' | '

function joinHeader(arr) {
  const len = arr.length
  const splitArr = []
  for (let i = 0; i < len; i++) {
    splitArr.push('---')
  }
  return createTr(arr.join(split_char)) + '\n' + createTr(splitArr.join(split_char))
}

function createTr(row, prefix, level) {
  if (level > 1) {
    for (let i = 1; i < level; i++) {
      prefix = '  ' + prefix
    }
  }
  if (!prefix) {
    prefix = ''
  }
  return `| ${prefix}${row} |`
}

function createTrContent(params, rowConfig, prefix, level) {
  if (!prefix) {
    prefix = ''
  }
  if (!level) {
    level = 1
  }
  let rows = []
  for (const param of params) {
    const row = []
    for (const config of rowConfig) {
      let value = param[config.prop]
      if (config.prop === 'required') {
        value = value ? $ts('yes') : $ts('no')
      }
      row.push(value)
    }
    rows.push(createTr(row.join(split_char), prefix, level))
    if (param.children && param.children.length > 0) {
      const childrenRows = createTrContent(param.children, rowConfig, '└ ', level + 1)
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
  const header = rowConfig.map(config => config.label)
  const headerContent = joinHeader(header) + '\n'
  const trContent = createTrContent(params, rowConfig)
  const tableContent = [headerContent, trContent]
  return tableContent.join('')
}

const MarkdownUtil = {

  appendMarkdown(doc_info, markdown_content) {
    init_docInfo(doc_info)
    const markdown = MarkdownUtil.toMarkdown(doc_info)
    markdown_content.append(markdown)
  },
  doMarkdownByData(treeData, markdown_content) {
    treeData.forEach(docInfo => {
      const children = docInfo.children
      if (docInfo.isFolder === 1) {
        markdown_content.append(`## ${docInfo.name}\n\n`)
        this.doMarkdownByData(children, markdown_content)
      } else {
        MarkdownUtil.appendMarkdown(docInfo, markdown_content)
      }
    })
    return markdown_content
  },
  toMarkdownByData(docInfoList, title) {
    title = title || $ts('document')
    const treeData = convert_tree(docInfoList)
    const markdown_content = new StringBuilder(`# ${title}\n\n`)
    MarkdownUtil.doMarkdownByData(treeData, markdown_content)
    return markdown_content.toString()
  },
  // toMarkdownByData(docInfoList, title) {
  //   title = title || $ts('document')
  //   const treeData = convert_tree(docInfoList)
  //   const markdown_content = new StringBuilder(`# ${title}\n\n`)
  //   const appendMarkdown = (doc_info) => {
  //     init_docInfo(doc_info)
  //     const markdown = MarkdownUtil.toMarkdown(doc_info)
  //     markdown_content.append(markdown)
  //   }
  //   treeData.forEach(docInfo => {
  //     const children = docInfo.children
  //     const isFolder = children && children.length > 0
  //     if (isFolder) {
  //       markdown_content.append(`## ${docInfo.name}\n\n`)
  //       children.forEach(child => {
  //         appendMarkdown(child)
  //       })
  //     } else {
  //       appendMarkdown(docInfo)
  //     }
  //   })
  //   return markdown_content.toString()
  // },
  toMarkdown(docInfo) {
    const builder = new StringBuilder()
    const append = (str) => {
      builder.append(`\n${str}\n`)
    }
    const appendCode = (str) => {
      const codeWrap = '```'
      builder.append(`\n${codeWrap}\n${str}\n${codeWrap}\n`)
    }
    append(`### ${docInfo.name}`)
    if (docInfo.author) {
      append(`${$ts('maintainer')}：${docInfo.author}`)
    }
    if (isHttp(docInfo)) {
      append(`#### URL`)
      const debugEnvs = docInfo.debugEnvs || []
      if (debugEnvs.length > 0) {
        const ul = new StringBuilder()
        docInfo.debugEnvs.forEach(hostConfig => {
          const baseUrl = hostConfig.configValue
          const url = get_effective_url(baseUrl, docInfo.url)
          ul.append(`- ${hostConfig.configKey}: \`${docInfo.httpMethod}\` ${url}\n`)
        })
        append(ul.toString())
      } else {
        append(`- \`${docInfo.httpMethod}\` ${docInfo.url}`)
      }
    } else if (isDubbo(docInfo)) {
      append(`${$ts('method')}：${docInfo.url}`)
    }
    append(`${$ts('description')}：${docInfo.description}`)

    if (isHttp(docInfo)) {
      append(`ContentType：\`${docInfo.contentType}\``)
      if (docInfo.pathParams && docInfo.pathParams.length > 0) {
        append(`#### ${$ts('pathVariable')}`)
        const pathParamsTable = createTable(docInfo.pathParams, Enums.PARAM_STYLE.path)
        append(pathParamsTable)
      }
      if (docInfo.headerParams && docInfo.headerParams.length > 0) {
        append(`#### ${$ts('requestHeader')}`)
        const headerParamsTable = createTable(docInfo.headerParams, Enums.PARAM_STYLE.header)
        append(headerParamsTable)
      }
    }

    append(`#### ${$ts('requestParams')}`)
    if (docInfo.queryParams && docInfo.queryParams.length > 0) {
      append(`##### Query Parameter`)
      const queryParamsTable = createTable(docInfo.queryParams, Enums.PARAM_STYLE.request)
      append(queryParamsTable)
    }
    if (docInfo.requestParams && docInfo.requestParams.length > 0) {
      append(`##### Body Parameter`)
      const requestParamsTable = createTable(docInfo.requestParams, Enums.PARAM_STYLE.request)
      append(requestParamsTable)
    }

    if (isShowRequestExample(docInfo)) {
      append(`#### ${$ts('requestExample')}`)
      const requestExample = create_response_example(docInfo.requestParams)
      appendCode(JSON.stringify(requestExample, null, 4))
    }

    append(`#### ${$ts('responseParam')}`)
    const responseParamsTable = createTable(docInfo.responseParams, Enums.PARAM_STYLE.response)
    append(responseParamsTable)

    if (isHttp(docInfo)) {
      append(`#### ${$ts('responseExample')}`)
      const responseExample = create_response_example(docInfo.responseParams)
      appendCode(JSON.stringify(responseExample, null, 4))
    }

    append(`#### ${$ts('errorCode')}`)
    const errorCodeParamsTable = createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code)
    append(errorCodeParamsTable)
    return builder.toString()
  }
}

export default MarkdownUtil
