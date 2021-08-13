import router from './routers'
import store from '@/store'
import Config from '@/settings'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {
  getToken
} from '@/utils/auth'
import {
  buildMenus
} from '@/api/system/menu'
import {
  filterAsyncRouter
} from '@/store/modules/permission'

NProgress.configure({
  showSpinner: false
})

// no redirect
const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - ' + Config.title
  }
  NProgress.start()
  if (getToken()) {
    if (to.path === '/login') {
      next({
        path: '/'
      })
      NProgress.done()
    } else {
      if (store.getters.roles.length === 0) {
        store.dispatch('GetInfo').then(() => {
          loadMenus(next, to)
        }).catch(() => {
          store.dispatch('LogOut').then(() => {
            location.reload()
          })
        })
      } else if (store.getters.loadMenus) {
        store.dispatch('updateLoadMenus')
        loadMenus(next, to)
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirct=${to.fullPath}`)
      NProgress.done
    }
  }
})

export const loadMenus = (next, to) => {
  buildMenus().then(res => {
    const sdata = JSON.parse(JSON.stringify(res))
    const rdata = JSON.parse(JSON.stringify(res))
    const sidebarRouters = filterAsyncRouter(sdata)
    const rewriteRouters = filterAsyncRouter(rdata, true)
    rewriteRouters.push({
      path: '*',
      redirect: '/404',
      hidden: true
    })
    store.dispatch('SetSidebarRouters', sidebarRouters)
  })
}

router.afterEach(() => {
  NProgress.done()
})
