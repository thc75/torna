import Vue from 'vue'

let userPerm = {
  roleData: {},
  isAdmin: false
}

Object.assign(Vue.prototype, {
  setPerm(perm) {
    userPerm = perm
  },
  isAdmin() {
    return userPerm && userPerm.isAdmin
  },
  /**
   * 是否拥有角色
   * @param key 操作key
   * @param roleCode 角色名称，可以是字符串，也可以是字符串数组
   * @returns {boolean}
   */
  hasRole(key, roleCode) {
    if (userPerm.isAdmin) {
      return true
    }
    if (!userPerm || !roleCode) {
      return false
    }
    const role = userPerm.roleData[key]
    if (Array.isArray(roleCode)) {
      return roleCode.filter(row => row === role).length > 0
    }
    return role === roleCode
  }
})
