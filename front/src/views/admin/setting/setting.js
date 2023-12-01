import { get, post } from '@/utils/http'
import { Message } from 'element-ui'

export function loadAdminConfig(localConfig, callback) {
  const keys = []
  for (const configKey in localConfig) {
    keys.push(localConfig[configKey].key)
  }
  get('/admin/setting/config/get', { keys: keys.join(',') }, resp => {
    const configs = resp.data.configs
    for (const config of configs) {
      for (const configKey in localConfig) {
        if (localConfig[configKey].key === config.key) {
          localConfig[configKey].value = config.value
          break
        }
      }
    }
    callback && callback()
  })
}

export function saveAdminConfig(config, noTip) {
  if (!Array.isArray(config)) {
    config = [config]
  }
  post('/admin/setting/config/update', config, () => {
    if (!noTip) {
      Message({
        message: $t('updateSuccess'),
        type: 'success'
      })
    }
  })
}
