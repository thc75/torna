import { Enums } from './enums'
import {
  create_response_example,
  convert_tree,
  init_docInfo,
  StringBuilder,
  style_config,
  get_effective_url
} from './common'

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
        value = value ? '是' : '否'
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
    return '无'
  }
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
  convertModule(moduleDTO) {
    const docInfoList = moduleDTO.docInfoList
    const treeData = convert_tree(docInfoList)
    const markdown_content = new StringBuilder(`# ${moduleDTO.name}\n\n`)
    const appendMarkdown = (doc_info) => {
      init_docInfo(doc_info)
      const markdown = MarkdownUtil.toMarkdown(doc_info)
      markdown_content.append(markdown)
    }
    treeData.forEach(docInfo => {
      const children = docInfo.children
      const isFolder = children && children.length > 0
      if (isFolder) {
        markdown_content.append(`## ${docInfo.name}\n\n`)
        children.forEach(child => {
          appendMarkdown(child)
        })
      } else {
        appendMarkdown(docInfo)
      }
    })
    return markdown_content.toString()
  },
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
    append(`描述：${docInfo.description}`)
    append(`ContentType：${docInfo.contentType}`)
    append(`#### Path参数`)
    const pathParamsTable = createTable(docInfo.pathParams, Enums.PARAM_STYLE.path)
    append(pathParamsTable)

    append(`#### 请求Header`)
    const headerParamsTable = createTable(docInfo.headerParams, Enums.PARAM_STYLE.header)
    append(headerParamsTable)

    append(`#### 请求参数`)
    const requestParamsTable = createTable(docInfo.requestParams, Enums.PARAM_STYLE.request)
    append(requestParamsTable)

    append(`#### 响应参数`)
    const responseParamsTable = createTable(docInfo.responseParams, Enums.PARAM_STYLE.response)
    append(responseParamsTable)

    append(`#### 响应示例`)
    const responseExample = create_response_example(docInfo.responseParams)
    appendCode(JSON.stringify(responseExample, null, 4))

    append(`#### 错误码`)
    const errorCodeParamsTable = createTable(docInfo.errorCodeParams, Enums.PARAM_STYLE.code)
    append(errorCodeParamsTable)
    return builder.toString()
  }
}

export default MarkdownUtil
