import querystring from 'query-string'

const KEY = 'torna.language'
const LANG_ZH_CN = 'zh-CN'

let storeLang

export function get_lang() {
  const query = location.search
  const queryParam = querystring.parse(query) || {}
  if (queryParam.lang) {
    return formatLang(queryParam.lang)
  }
  if (!storeLang) {
    const get = () => {
      const lang = localStorage.getItem(KEY)
      if (!lang) {
        return LANG_ZH_CN
      } else {
        return lang
      }
    }
    storeLang = get()
  }
  return formatLang(storeLang)
}

function formatLang(lang) {
  return lang === 'zh' ? LANG_ZH_CN : lang
}

export function set_lang(lang) {
  lang = formatLang(lang)
  localStorage.setItem(KEY, lang)
}
