import querystring from 'query-string'

const KEY = 'torna.language'

let storeLang

export function get_lang() {
  const query = location.search
  const queryParam = querystring.parse(query) || {}
  if (queryParam.lang) {
    return queryParam.lang
  }
  if (!storeLang) {
    const get = () => {
      const lang = localStorage.getItem(KEY) || getBrowserLanguage()
      if (lang.indexOf('zh') > -1) {
        return 'zh'
      } else if (lang.indexOf('en') > -1) {
        return 'en'
      } else {
        return lang
      }
    }
    storeLang = get()
  }
  return storeLang
}

function getBrowserLanguage() {
  const lang = navigator.language || navigator.userLanguage
  if (!lang || lang.indexOf('zh') > -1) {
    return 'zh'
  }
  if (lang.indexOf('en') > -1) {
    return 'en'
  }
  return 'zh'
}

export function set_lang(lang) {
  localStorage.setItem(KEY, lang)
}
