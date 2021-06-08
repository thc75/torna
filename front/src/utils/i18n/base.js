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
}
