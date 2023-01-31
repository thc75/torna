import Vue from 'vue'
import { get_lang } from '@/utils/i18n/common'
import { format_string } from '@/utils/common'
import { t as oldT } from 'element-ui/lib/locale'

const _ts = function(key) {
  let value = oldT(key)
  if (arguments.length > 1) {
    const args = []
    for (let i = 1; i < arguments.length; i++) {
      args.push(arguments[i])
    }
    value = format_string(value, args)
  }
  return value
}

/**
 * key为名称，value分为各个语言对应的翻译
 * { 'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' } }
 * @param configMapping Object
 * @private
 */
const _addI18n = function(configMapping) {
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
Vue.prototype.$addI18n = _addI18n

window.$ts = window.$t = _ts

/**
 * key为名称，value分为各个语言对应的翻译
 * { 'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' } }
 * @type {_addI18n}
 */
window.$addI18n = _addI18n
