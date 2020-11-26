import Vue from 'vue'

const RoleCode = {
  VISITOR: 'visitor',
  DEV: 'dev',
  LEADER: 'leader'
}

Object.assign(Vue.prototype, {
  RoleCode: RoleCode,
  Perm: {
    U: 'u',
    R: 'r',
    D: 'd'
  }
})
