import Vue from 'vue'
import VueI18n from 'vue-i18n'

import ElementLocale from 'element-ui/lib/locale'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import { get_lang } from '@/utils/i18n/common'

Vue.use(VueI18n)

const i18n = new VueI18n({
  locale: get_lang(),
  messages: {
    'zh': { ...zhLocale },
    'en': { ...enLocale }
  }
})
ElementLocale.i18n((key, value) => i18n.t(key, value))

export default i18n
