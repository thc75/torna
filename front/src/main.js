import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// locale
import i18n from '@/utils/i18n/index'
import '@/utils/i18n/config'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control
import '@/utils/global' // 自定义全局js
import '@/utils/routeUtil'
import '@/utils/role-code'
import '@/utils/perm'

import UmyUi from 'umy-ui'
import '@/styles/umy-ui.scss'
Vue.use(UmyUi)

/**
 * If you don't want to use mock-server
 * you want to use mockjs for request interception
 * you can execute:
 *
 * import { mockXHR } from '../mock'
 * mockXHR()
 */

Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  el: '#app',
  i18n,
  router,
  store,
  render: h => h(App)
})
