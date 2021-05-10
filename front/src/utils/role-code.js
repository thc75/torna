import Vue from 'vue'

const Role = {
  /** 访客 */
  guest: 'guest',
  /** 开发者 */
  dev: 'dev',
  /** 管理员 */
  admin: 'admin'
}

Object.assign(Vue.prototype, {
  Role: Role,
  getProjectRoleCodeConfig() {
    return [
      { label: this.$ts('visitor'), code: 'guest' },
      { label: this.$ts('developer'), code: 'dev' },
      { label: this.$ts('projectAdmin'), code: 'admin' }
    ]
  },
  getSpaceRoleCodeConfig() {
    return [
      { label: this.$ts('visitor'), code: 'guest' },
      { label: this.$ts('developer'), code: 'dev' },
      { label: this.$ts('spaceAdmin'), code: 'admin' }
    ]
  },
  getSpaceRoleName(roleCode) {
    for (const item of this.getSpaceRoleCodeConfig()) {
      if (item.code === roleCode) {
        return item.label
      }
    }
    return ''
  },
  getProjectRoleName(roleCode) {
    for (const item of this.getProjectRoleCodeConfig()) {
      if (item.code === roleCode) {
        return item.label
      }
    }
    return ''
  }
})
