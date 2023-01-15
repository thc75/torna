import Vue from 'vue'

Object.assign(Vue.prototype, {
  loadAdminConfig(localConfig) {
    const keys = []
    for (const configKey in localConfig) {
      keys.push(localConfig[configKey].key)
    }
    this.get('/admin/setting/config/get', { keys: keys.join(',') }, resp => {
      const configs = resp.data.configs
      for (const config of configs) {
        for (const configKey in localConfig) {
          if (localConfig[configKey].key === config.key) {
            localConfig[configKey].value = config.value
            break
          }
        }
      }
    })
  },
  saveAdminConfig(config) {
    if (!Array.isArray(config)) {
      config = [config]
    }
    this.post('/admin/setting/config/update', config, () => {
      this.tipSuccess($ts('updateSuccess'))
    })
  }
})
