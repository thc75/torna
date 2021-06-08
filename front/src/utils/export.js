import JSZip from 'jszip'
import HtmlUtil from '@/utils/convert-html'
import MarkdownUtil from '@/utils/convert-markdown'
import { convert_tree, download_text, format_string, init_docInfo } from '@/utils/common'
import { saveAs } from 'file-saver'

function export_single_page(docInfo, filenameHandler, contentHandler) {
  const content = contentHandler(docInfo)
  download_text(`${filenameHandler(docInfo)}`, content)
}

/**
 * 导出多页面
 * @param docInfoList docInfoList 没有转换tree
 * @param filenameHandler 构建文件名回调，参数：docInfo
 * @param contentHandler 构建内容回调，参数：docInfo
 */
function do_export_multi_docs(docInfoList, filenameHandler, contentHandler) {
  const zip = new JSZip()
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
    const zipFile = `export-${new Date().getTime()}.zip`
    // see FileSaver.js
    saveAs(content, zipFile)
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
  /**
   * 导出一个页面为markdown
   * @param docInfo
   */
  exportMarkdownSinglePage(docInfo) {
    export_single_page(docInfo, (docInfo) => {
      return `${docInfo.name}-${new Date().getTime()}.md`
    }, (docInfo) => {
      return MarkdownUtil.toMarkdown(docInfo)
    })
  },
  /**
   * 导出一个页面为html
   * @param docInfo
   */
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
  /**
   * 导出全部markdown为单页
   * @param docInfoList docInfoList
   */
  exportMarkdownAllInOne(docInfoList) {
    const content = MarkdownUtil.toMarkdownByData(docInfoList)
    download_text(`export-${new Date().getTime()}.md`, content)
  },
  /**
   * 导出全部markdown为多页
   * @param docInfoList docInfoList
   */
  exportMarkdownMultiPages(docInfoList) {
    do_export_multi_docs(docInfoList, (docInfo) => {
      return `${docInfo.name}.md`
    }, MarkdownUtil.toMarkdown)
  },
  /**
   * 导出全部Html为单页
   * @param docInfoList docInfoList
   */
  exportHtmlAllInOne(docInfoList) {
    const content = HtmlUtil.toHtmlByData(docInfoList)
    const html = format_string(html_wrapper, {
      title: '文档',
      body: content
    })
    download_text(`export-${new Date().getTime()}.html`, html)
  },
  /**
   * 导出全部Html为多页
   * @param docInfoList docInfoList
   */
  exportHtmlMultiPages(docInfoList) {
    do_export_multi_docs(docInfoList, (docInfo) => {
      return `${docInfo.name}.html`
    }, (docInfo) => {
      const content = HtmlUtil.toHtml(docInfo)
      return format_string(html_wrapper, {
        title: docInfo.name,
        body: content
      })
    })
  }
}

export default ExportUtil
