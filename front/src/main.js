import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/css/global.css'
import axios from 'axios'

Vue.config.productionTip = false

// 使用ElementUI
Vue.use(ElementUI)

// 配置axios
axios.defaults.baseURL = '/api'
axios.defaults.withCredentials = true // 携带cookie
// 响应拦截器
axios.interceptors.response.use(
  response => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    if (response.status === 200) {
      return Promise.resolve(response)
    } else {
      return Promise.reject(response)
    }
  },
  error => {
    if (error.response && error.response.status) {
      switch (error.response.status) {
        // 401: 未登录
        case 401:
          router.replace({
            path: '/login',
            query: { redirect: router.currentRoute.fullPath }
          })
          break
        // 403: token过期
        case 403:
          // 清除token
          localStorage.removeItem('token')
          // 跳转登录页面，并将要浏览的页面fullPath传过去
          router.replace({
            path: '/login',
            query: { redirect: router.currentRoute.fullPath }
          })
          break
        // 404请求不存在
        case 404:
          // 提示资源不存在
          break
        // 其他错误
        default:
          // 提示其他错误
          break
      }
      return Promise.reject(error.response)
    }
  }
)

// 挂载到Vue实例
Vue.prototype.$http = axios

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app') 