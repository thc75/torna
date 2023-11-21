import { get, post } from '@/utils/http'

/**
 * 获取应用配置
 * @param moduleId
 * @param keys Array ['aa', 'bb']
 * @param callback
 */
export function listModuleConfig(moduleId, keys, callback) {
  get('/module/setting/list', { moduleId: moduleId, key: keys.join(',') }, resp => {
    callback(resp.data)
  })
}

/**
 * 保存应用配置
 * @param configs | { moduleId: 'xx', items: [{ configKey: '', configValue: '', description: '' }] }
 * @param callback
 */
export function saveModuleConfig(configs, callback) {
  post('/module/setting/update', configs, resp => {
    callback && callback(resp.data)
  })
}
