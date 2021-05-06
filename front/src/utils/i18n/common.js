export function get_lang() {
  const lang = localStorage.getItem('torna.language') || navigator.language
  if (lang.indexOf('zh') > -1) {
    return 'zh'
  } else if (lang.indexOf('en') > -1) {
    return 'en'
  } else {
    return 'zh'
  }
}
