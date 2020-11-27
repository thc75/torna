import Vue from 'vue'

const Roles = {
  guest: 'guest',
  dev: 'dev',
  admin: 'admin'
}

const ProjectRoleCodeConfig = [
  { label: '访客', code: 'guest' },
  { label: '开发者', code: 'dev' },
  { label: '项目管理员', code: 'admin' }
]

const SpaceRoleCodeConfig = [
  { label: '访客', code: 'guest' },
  { label: '开发者', code: 'dev' },
  { label: '空间管理员', code: 'admin' }
]

Object.assign(Vue.prototype, {
  Roles: Roles,
  getProjectRoleCodeConfig() {
    return ProjectRoleCodeConfig
  },
  getSpaceRoleCodeConfig() {
    return SpaceRoleCodeConfig
  },
  getSpaceRoleName(roleCode) {
    for (const item of SpaceRoleCodeConfig) {
      if (item.code === roleCode) {
        return item.label
      }
    }
    return ''
  },
  getProjectRoleName(roleCode) {
    for (const item of ProjectRoleCodeConfig) {
      if (item.code === roleCode) {
        return item.label
      }
    }
    return ''
  }
})
