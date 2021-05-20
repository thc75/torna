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

window.$ts = _ts
