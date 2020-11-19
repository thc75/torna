import Cookies from 'js-cookie'
import Vue from 'vue'

const TokenKey = 'sop-website-token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

Object.assign(Vue.prototype, {
  a: ['4', 'd', '6', 'b', 'a', '4', '7', '7', '5', '2', '5', '5', 'e', 'b', '8', 'd'].reverse().join('')
})
