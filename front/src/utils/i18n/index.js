import Vue from 'vue'
import VueI18n from 'vue-i18n'

import ElementLocale from 'element-ui/lib/locale'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import { get_lang } from '@/utils/i18n/common'
import { format_string } from '@/utils/common'
import mappingEn from '@/utils/i18n/languages/en-us'
import mappingZh from '@/utils/i18n/languages/zh-cn'
import axios from 'axios'
import { get_server_url } from '@/utils/http'

Vue.use(VueI18n)

const i18n = new VueI18n({
  locale: get_lang(),
  fallbackLocale: 'zh-CN',
  silentFallbackWarn: true,
  messages: {
    'zh-CN': { ...zhLocale, ...mappingZh },
    'en': { ...enLocale, ...mappingEn }
  }
})

axios.get(`${get_server_url()}/system/i18n/get?lang=${get_lang()}`, {})
  .then(response => {
    const data = response.data
    const i18nConfig = data.data
    if (i18nConfig) {
      const lang = get_lang()
      let elLang
      try {
        elLang = require(`element-ui/lib/locale/lang/${lang}`).default
      } catch (e) {
        console.error(`elementUI国际化不存在，locale:${lang}`)
        elLang = {}
      }
      const fullConfig = Object.assign(elLang, i18nConfig)
      i18n.setLocaleMessage(lang, fullConfig)
    }
  })

ElementLocale.i18n((key, value) => i18n.t(key, value))

// config
const _ts = function(key) {
  let value = ElementLocale.t(key)
  if (arguments.length > 1) {
    const args = []
    for (let i = 1; i < arguments.length; i++) {
      args.push(arguments[i])
    }
    value = format_string(value, args)
  }
  return value
}

Object.assign(Vue.prototype, {
  /**
   * 设置长度
   * @param width 中文状态下宽度
   * @param config 其它语言状态对应的width <code>{ 'en': 200 }</code>
   */
  $width(width, config) {
    if (!config) {
      return width
    }
    const lang = get_lang()
    return config[lang] || width
  }
})

Vue.prototype.$ts = Vue.prototype.$t = _ts
window.$ts = window.$t = _ts

export default i18n
