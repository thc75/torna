import Vue from 'vue'
import { Translator as TranslatorEnUs } from '@/utils/i18n/languages/en-us'
import { Translator as TranslatorZhCN } from '@/utils/i18n/languages/zh-cn'
import { BaseTranslator } from '@/utils/i18n/base'
import { get_lang } from '@/utils/i18n/common'
import { format_string } from '@/utils/common'

class Delegate extends BaseTranslator {
  constructor() {
    super()
    let delegate
    switch (get_lang()) {
      case 'zh-Hans-CN':
      case 'zh-CN':
      case 'zh':
        delegate = new TranslatorZhCN()
        break
      case 'en-US':
      case 'en':
        delegate = new TranslatorEnUs()
        break
      default:
        delegate = new TranslatorZhCN()
    }
    this.delegate = delegate
  }
  translate(key) {
    return this.delegate.translate(key)
  }
  addMapping(anotherMapping) {
    this.delegate.addMapping(anotherMapping)
  }
}

const delegate = new Delegate()

const _ts = function(key) {
  let value = delegate.translate(key)
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
  const lang = get_lang()
  const mapping = {}
  for (const name in configMapping) {
    const config = configMapping[name]
    const value = config[lang] || name
    mapping[name] = value
  }
  delegate.addMapping(mapping)
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

Vue.prototype.$ts = _ts
Vue.prototype.$addI18n = _addI18n

window.$ts = _ts

/**
 * key为名称，value分为各个语言对应的翻译
 * { 'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' } }
 * @type {_addI18n}
 */
window.$addI18n = _addI18n
