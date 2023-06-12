import querystring from 'query-string'

const KEY = 'torna.language'
const LANG_ZH_CN = 'zh-CN'

export function get_lang() {
  return localStorage.getItem(KEY) || getLangFrmQuery()
}

function getLangFrmQuery() {
  const query = location.search
  const queryParam = querystring.parse(query) || {}
  if (queryParam.lang) {
    return formatLang(queryParam.lang)
  }
  return ''
}

function formatLang(lang) {
  return lang === 'zh' ? LANG_ZH_CN : lang
}

export function set_lang(lang) {
  lang = formatLang(lang)
  localStorage.setItem(KEY, lang)
}
