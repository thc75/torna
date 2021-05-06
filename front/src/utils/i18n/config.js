import Vue from 'vue'
import { Translator as TranslatorEnUs } from '@/utils/i18n/languages/en-us'
import { Translator as TranslatorZhCN } from '@/utils/i18n/languages/zh-cn'
import { BaseTranslator } from '@/utils/i18n/base'
import { get_lang } from '@/utils/i18n/common'

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

Object.assign(Vue.prototype, {
  $ts(key) {
    return delegate.translate(key)
  }
})
