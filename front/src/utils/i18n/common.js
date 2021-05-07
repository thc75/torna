const KEY = 'torna.language'

export function get_lang() {
  const lang = localStorage.getItem(KEY) || navigator.language
  if (lang.indexOf('zh') > -1) {
    return 'zh'
  } else if (lang.indexOf('en') > -1) {
    return 'en'
  } else {
    return lang
  }
}

export function set_lang(lang) {
  localStorage.setItem(KEY, lang)
}
