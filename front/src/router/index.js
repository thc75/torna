import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'
import LayoutView from '@/layout_view'
import LayoutUserCenter from '@/layout/index_user'
import LayoutSpace from '@/layout/index_space'
import LayoutProject from '@/layout/index_project'
import LayoutAdmin from '@/layout_admin'
import LayoutShare from '@/layout_share'
import LayoutShow from '@/layout_show'

Vue.use(Router)

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '首页', icon: 'dashboard' }
    }]
  },
  {
    path: '/login',
    component: () => import('@/views/common/login'),
    meta: { title: '用户登录' },
    hidden: true
  },
  {
    path: '/setPassword',
    component: () => import('@/views/common/setPassword'),
    meta: { title: '设置密码' },
    hidden: true
  },
  {
    path: '/reg',
    component: () => import('@/views/common/reg'),
    meta: { title: '用户注册' },
    hidden: true
  },
  {
    path: '/openapi',
    name: 'openapi',
    component: () => import('@/views/doc/OpenApiDoc'),
    meta: { 'title': 'OpenAPI文档' },
    hidden: true
  },
  {
    'path': '/help',
    'name': 'Help',
    component: () => import('@/views/help/index'),
    'meta': { 'title': '使用手册' },
    'hidden': true
  },
  {
    path: '/updatePassword',
    component: Layout,
    children: [{
      path: '/',
      name: 'UpdatePassword',
      component: () => import('@/views/common/updatePassword'),
      meta: { title: '修改密码' },
      hidden: true
    }]
  },
  {
    path: '/space', // 必须/开头
    component: LayoutSpace,
    name: 'Space',
    children: [
      {
        path: 'project/:spaceId(\\w+)',
        name: 'SpaceProject',
        component: () => import('@/views/space/index_project'),
        meta: { title: '项目列表' }
      },
      {
        path: 'compose/:spaceId(\\w+)',
        name: 'SpaceComposeProject',
        component: () => import('@/views/space/index_compose_project'),
        meta: { title: '项目列表' }
      },
      {
        path: 'info/:spaceId(\\w+)',
        name: 'SpaceInfo',
        component: () => import('@/views/space/index_info'),
        meta: { title: '空间信息' }
      },
      {
        path: 'member/:spaceId(\\w+)',
        name: 'SpaceMember',
        component: () => import('@/views/space/index_member'),
        meta: { title: '空间成员' }
      },
      {
        path: 'openuser/:spaceId(\\w+)',
        name: 'SpaceOpenuser',
        component: () => import('@/views/space/index_openuser'),
        meta: { title: '开放用户' }
      }
    ]
  },
  {
    path: '/project', // 必须/开头
    component: LayoutProject,
    name: 'Project',
    children: [
      {
        path: 'doc/:projectId(\\w+)',
        name: 'ProjectDoc',
        component: () => import('@/views/project/index_doc')
      },
      {
        path: 'info/:projectId(\\w+)',
        name: 'ProjectInfo',
        component: () => import('@/views/project/index_info')
      },
      {
        path: 'member/:projectId(\\w+)',
        name: 'ProjectMember',
        component: () => import('@/views/project/index_member')
      }
    ]
  },
  {
    path: '/doc', // 必须/开头
    component: LayoutProject,
    name: 'Doc',
    children: [
      {
        path: 'edit/:moduleId(\\w+)/:docId(\\w+)',
        name: 'Edit',
        hidden: true,
        component: () => import('@/views/doc/edit/index'),
        meta: { title: '编辑文档' }
      },
      {
        path: 'new/:moduleId(\\w+)/:parentId(\\w+)',
        name: 'New',
        hidden: true,
        component: () => import('@/views/doc/edit/index'),
        meta: { title: '新建文档' }
      },
      {
        path: 'copy/:moduleId(\\w+)/:copyId(\\w+)',
        name: 'NewCopy',
        hidden: true,
        component: () => import('@/views/doc/edit/index'),
        meta: { title: '复制文档' }
      },
      {
        path: 'new/:moduleId(\\w+)',
        name: 'New2',
        hidden: true,
        component: () => import('@/views/doc/edit/index'),
        meta: { title: '新建文档' }
      }
    ]
  },
  // user
  {
    path: '/user', // 必须/开头
    component: LayoutUserCenter,
    name: 'User',
    children: [
      {
        path: '/',
        name: 'UserInfo',
        hidden: true,
        component: () => import('@/views/user'),
        meta: { title: '基本设置' }
      },
      {
        path: 'subscribe/doc',
        name: 'SubscribeDoc',
        hidden: true,
        component: () => import('@/views/user/subscribe/doc'),
        meta: { title: '关注文档' }
      },
      {
        path: 'message',
        name: 'Message',
        hidden: true,
        component: () => import('@/views/user/message'),
        meta: { title: '我的消息' }
      },
      {
        path: 'systemSetting',
        name: 'SystemSetting',
        hidden: true,
        component: () => import('@/views/common/systemSetting'),
        meta: { title: '系统设置' }
      }
    ]
  },
  // admin
  {
    path: '/admin', // 必须/开头
    component: LayoutAdmin,
    name: 'Admin',
    children: [
      {
        path: 'users',
        name: 'Users',
        hidden: true,
        component: () => import('@/views/admin/user/index'),
        meta: { title: '用户管理' }
      },
      {
        path: 'setting',
        name: 'Setting',
        hidden: true,
        component: () => import('@/views/admin/setting/index'),
        meta: { title: '系统设置' }
      }
    ]
  },
  // 浏览模式
  {
    path: '/view', // 必须/开头
    component: LayoutView,
    name: 'View',
    meta: { title: '文档', icon: 'example' },
    children: [
      {
        path: '/',
        name: 'ViewHome',
        component: () => import('@/views/view/index')
      },
      {
        path: ':docId(\\w+)',
        name: 'ViewDoc',
        component: () => import('@/views/view/index')
      }
    ]
  },
  // 分享模式
  {
    path: '/share', // 必须/开头
    component: LayoutShare,
    name: 'Share',
    meta: { title: '文档', icon: 'example' },
    children: [
      {
        path: ':shareId(\\w+)',
        name: 'ShareHome',
        component: () => import('@/views/share/index')
      },
      {
        path: ':shareId(\\w+)/:docId(\\w+)',
        name: 'ShareHome2',
        component: () => import('@/views/share/index')
      }
    ]
  },
  // 聚合模式
  {
    path: '/show', // 必须/开头
    component: LayoutShow,
    name: 'Show',
    meta: { title: '文档', icon: 'example' },
    children: [
      {
        path: ':showId(\\w+)',
        name: 'ShowHome',
        component: () => import('@/views/show/index')
      },
      {
        path: ':showId(\\w+)/:docId(\\w+)',
        name: 'ShowHome2',
        component: () => import('@/views/show/index')
      }
    ]
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
