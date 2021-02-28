import Vue from 'vue'
import store from '@/store'
import { run_init } from './init'

function setUserPerm(data) {
  store.state.user.perms = data
}

function is_super_admin() {
  const userPerm = getUserPerm()
  return userPerm && userPerm.isSuperAdmin
}

function getUserPerm() {
  return store.state.user.perms
}

function has_role(key, roleCode) {
  if (is_super_admin()) {
    return true
  }
  const userPerm = getUserPerm()
  if (!userPerm || !roleCode) {
    return false
  }
  const role = userPerm.roleData[key]
  if (Array.isArray(roleCode)) {
    return roleCode.filter(row => row === role).length > 0
  }
  return role === roleCode
}

Object.assign(Vue.prototype, {
  /**
   * 初始化权限
   */
  initPerm() {
    this.get('/user/perm/get', {}, resp => {
      setUserPerm(resp.data)
      this.$nextTick(() => {
        run_init.call(this)
      })
    })
  },
  isSuperAdmin() {
    return is_super_admin()
  },
  /**
   * 是否拥有角色
   * <el-button v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])">操作</el-button>
   * @param key 操作key
   * @param roleCode 角色名称，可以是字符串，也可以是字符串数组
   * @returns {boolean}
   */
  hasRole(key, roleCode) {
    return has_role(key, roleCode)
  }
})
