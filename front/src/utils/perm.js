import Vue from 'vue'

let userPerm = {
  permData: {},
  roleData: {},
  isAdmin: false
}

Object.assign(Vue.prototype, {
  setPerm(perm) {
    userPerm = perm
  },
  hasPermission(key, perm) {
    if (userPerm.isAdmin) {
      return true
    }
    if (!userPerm || !perm) {
      return false
    }
    const perms = userPerm.permData[key] || []
    return perms.filter(p => p === perm).length > 0
  },
  hasRole(key, roleCode) {
    if (userPerm.isAdmin) {
      return true
    }
    if (!userPerm || !roleCode) {
      return false
    }
    // console.log(key)
    // console.log(userPerm.roleData)
    const role = userPerm.roleData[key]
    if (Array.isArray(roleCode)) {
      return roleCode.filter(row => row === role).length > 0
    }
    return role === roleCode
  }
})
