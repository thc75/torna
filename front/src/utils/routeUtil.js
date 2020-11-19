import { initRoutes } from '@/router'
import Vue from 'vue'

Object.assign(Vue.prototype, {
  initMenu(projects, callback) {
    const menus = [{
      path: '/',
      name: 'Dashboard0',
      component: 'Layout',
      redirect: '/dashboard',
      children: [{
        path: 'dashboard',
        name: 'Dashboard',
        component: 'dashboard/index',
        meta: { title: '首页', component: 'dashboard/index', showOne: true, affix: true }
      }]
    }]
    const projectItem = []
    menus.push({
      path: 'project',
      name: 'Project',
      component: 'Layout',
      meta: { title: '项目列表', open: true },
      children: projectItem
    })
    projects.forEach((row, index) => {
      const item = {
        path: `info?id=${row.id}`,
        name: `Info${index}`,
        component: 'project/index',
        meta: { title: row.name, api: true }
      }
      projectItem.push(item)
    })
    const routes = initRoutes(menus)
    this.$store.state.settings.routes = routes
    callback && callback.call(this, routes)
  }
})

