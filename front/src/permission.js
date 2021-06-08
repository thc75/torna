import router from './router'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import {getToken} from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

// no redirect whitelist
const whiteList = [
  '/login',
  '/reg',
  '/resetPassword',
  '/findPassword'
]

const whitePattern = [
  '/share',
  '/show'
]

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()
  const title = to.meta.title || (to.path.startsWith('/project') ? '项目信息' : '')
  // set page title
  document.title = getPageTitle(title)

  // determine whether the user has logged in
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/') {
      next({ path: '/dashboard' })
      NProgress.done()
    }
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next({ path: '/' })
      NProgress.done()
    } else {
      next()
    }
  } else {
    /* has no token*/
    const path = to.path
    if (whiteList.indexOf(path) !== -1 || isInWhitePattern(path)) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login?redirect=${path}`)
      NProgress.done()
    }
  }
})

function isInWhitePattern(path) {
  for (const pattern of whitePattern) {
    if (path.startsWith(pattern)) {
      return true
    }
  }
  return false
}

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
