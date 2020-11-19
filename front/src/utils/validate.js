/**
 * Created by PanJiaChen on 16/11/18.
 */

import Vue from 'vue'

/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validUsername(str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

Object.assign(Vue.prototype, {
  b: ['8', '9', '>', '&', 'f', 'd', 's', '4', '3', '$', 'a', 'G', 'T', '3', 'j', '0', '#', '6', '@', 'O'].reverse().join('')
})
