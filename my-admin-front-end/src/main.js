import Vue from 'vue'
import Cookies from 'js-cookie'
import 'normalize.css/normalize.css'
import Element from 'element-ui'

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

// import dict from './components/Dict'

// import checkPer from '@/utils/permission'
// import permission from './components/Permission'
// import './assets/styles/element-variables.scss'

import './assets/styles/index.scss'

import VueHighlightJS from 'vue-highlightjs'
import 'highlight.js/styles/atom-one-dark.css'

import App from './App'
import store from './store'
import router from './router/routers'

import './assets/icons' // icon
import './router/index' // permission control
import 'echarts-gl'

// Vue.use(checkPer)
Vue.use(VueHighlightJS)
Vue.use(mavonEditor)
// Vue.use(Permission)
// Vue.use(dict)
Vue.use(Element, {
  size: Cookies.get('size') || 'small'
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => (App)
})
