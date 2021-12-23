export class BaseTranslator {
  translate(key) {
    if (!this.mapping) {
      this.mapping = this.getMapping()
    }
    return this.mapping[key] || key
  }

  getMapping() {
    throw new Error('sub class must override getMapping() method.')
  }

  /**
   * 添加其它翻译
   * @param anotherMapping 其它翻译映射
   */
  addMapping(anotherMapping) {
    throw new Error('sub class must override addMapping() method.')
  }
}
