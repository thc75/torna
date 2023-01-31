import Vue from 'vue'
import VueI18n from 'vue-i18n'

import ElementLocale from 'element-ui/lib/locale'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import { get_lang } from '@/utils/i18n/common'
import { format_string } from '@/utils/common'
import mappingEn from '@/utils/i18n/languages/en-us'
import mappingZh from '@/utils/i18n/languages/zh-cn'

Vue.use(VueI18n)

const i18n = new VueI18n({
  locale: get_lang(),
  messages: {
    'zh': { ...zhLocale, ...mappingZh },
    'en': { ...enLocale, ...mappingEn }
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
