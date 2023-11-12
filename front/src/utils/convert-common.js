import {Enums} from '@/utils/enums'

export function isHttp(docInfo) {
  return docInfo.type === Enums.DOC_TYPE.HTTP
}

export function isDubbo(docInfo) {
  return docInfo.type === Enums.DOC_TYPE.DUBBO
}

export function isCustom(docInfo) {
  return docInfo.type === Enums.DOC_TYPE.CUSTOM
}

export function isMarkdown(docInfo) {
  return docInfo.type === Enums.DOC_TYPE.MARKDOWN
}


export function isShowRequestExample(docInfo) {
  return isHttp(docInfo) && docInfo.contentType && docInfo.contentType.toLowerCase().indexOf('json') > -1
}
