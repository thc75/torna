import {get} from './http'
import JSZip from 'jszip'
import HtmlUtil from '@/utils/convert-html'
import MarkdownUtil from '@/utils/convert-markdown'
import {convert_tree, download_text, format_string, init_docInfo} from '@/utils/common'
import {saveAs} from 'file-saver'

function export_all_in_one(moduleId, filenameHandler, contentHandler) {
  get('/module/detail', { moduleId: moduleId }, resp => {
    const moduleDTO = resp.data
    const content = contentHandler(moduleDTO)
    download_text(`${filenameHandler(moduleDTO)}`, content)
  })
}

function export_single_page(docInfo, filenameHandler, contentHandler) {
  const content = contentHandler(docInfo)
  download_text(`${filenameHandler(docInfo)}`, content)
}

/**
 * 导出多页面
 * @param moduleId
 * @param filenameHandler 构建文件名回调，参数：docInfo
 * @param contentHandler 构建内容回调，参数：docInfo
 */
function do_export_multi(moduleId, filenameHandler, contentHandler) {
  const zip = new JSZip()
  get('/module/detail', { moduleId: moduleId }, resp => {
    const moduleDTO = resp.data
    const docInfoList = moduleDTO.docInfoList
    const treeData = convert_tree(docInfoList)
    const appendFile = (zip, doc_info) => {
      init_docInfo(doc_info)
      const markdown = contentHandler(doc_info)
      zip.file(`${filenameHandler(doc_info)}`, markdown)
    }
    treeData.forEach(docInfo => {
      const children = docInfo.children
      const isFolder = children && children.length > 0
      if (isFolder) {
        // 创建文件夹
        const folderZip = zip.folder(docInfo.name)
        children.forEach(child => {
          // 文件放入文件夹中
          appendFile(folderZip, child)
        })
      } else {
        appendFile(zip, docInfo)
      }
    })
    // 下载
    zip.generateAsync({ type: 'blob' }).then(function(content) {
      const zipFile = `${moduleDTO.name}-${new Date().getTime()}.zip`
      // see FileSaver.js
      saveAs(content, zipFile)
    })
  })
}

const html_wrapper = `
<html>
<head>
<title>{title}</title>
<style>
table {
    border-collapse: collapse;
    border-spacing: 0;
}

td,th {
    padding: 0;
}

.pure-table {
    border-collapse: collapse;
    border-spacing: 0;
    empty-cells: show;
    border: 1px solid #DCDFE6;
}

.pure-table caption {
    color: #000;
    font: italic 85%/1 arial,sans-serif;
    padding: 1em 0;
    text-align: center;
}

.pure-table td,.pure-table th {
    border-left: 1px solid #DCDFE6;
    border-width: 0 0 0 1px;
    font-size: inherit;
    margin: 0;
    overflow: visible;
    padding: .5em 1em;
}

.pure-table th {
    border-bottom: 1px solid #DCDFE6;
}

.pure-table thead {
    background-color: #f3f3f3;
    color: #000;
    text-align: left;
    vertical-align: bottom;
}

.pure-table td {
    background-color: transparent;
}

.pure-table-bordered td {
    border-bottom: 1px solid #DCDFE6;
}

.pure-table-bordered tbody>tr:last-child>td {
    border-bottom-width: 0;
}

.code-block { font-size: 14px;color: #606266;font-weight: normal; background-color: #fafafa; padding: 5px;}
</style>
</head>
<body>
{body}
</body>
</html>
`

const ExportUtil = {
  exportMarkdownSinglePage(docInfo) {
    export_single_page(docInfo, (docInfo) => {
      return `${docInfo.name}-${new Date().getTime()}.md`
    }, (docInfo) => {
      return MarkdownUtil.toMarkdown(docInfo)
    })
  },
  exportMarkdownAllInOne(moduleId) {
    export_all_in_one(moduleId, (moduleDTO) => {
      return `${moduleDTO.name}-${new Date().getTime()}.md`
    }, MarkdownUtil.convertModule)
  },
  exportMarkdownMultiPages(moduleId) {
    do_export_multi(moduleId, (moduleDTO) => {
      return `${moduleDTO.name}.md`
    }, MarkdownUtil.toMarkdown)
  },
  exportHtmlSinglePage(docInfo) {
    export_single_page(docInfo, (docInfo) => {
      return `${docInfo.name}-${new Date().getTime()}.html`
    }, (docInfo) => {
      const content = HtmlUtil.toHtml(docInfo)
      return format_string(html_wrapper, {
        title: docInfo.name,
        body: content
      })
    })
  },
  exportHtmlAllInOne(moduleId) {
    export_all_in_one(moduleId, (moduleDTO) => {
      return `${moduleDTO.name}-${new Date().getTime()}.html`
    }, (moduleDTO) => {
      const content = HtmlUtil.convertModule(moduleDTO)
      return format_string(html_wrapper, {
        title: moduleDTO.name,
        body: content
      })
    })
  },
  exportHtmlMultiPages(moduleId) {
    do_export_multi(moduleId, (moduleDTO) => {
      return `${moduleDTO.name}.html`
    }, (doc_info) => {
      const content = HtmlUtil.toHtml(doc_info)
      return format_string(html_wrapper, {
        title: doc_info.name,
        body: content
      })
    })
  }
}

export default ExportUtil
