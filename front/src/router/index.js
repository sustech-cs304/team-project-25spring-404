import Vue from 'vue'
import VueRouter from 'vue-router'
import Cookies from 'js-cookie'

// 懒加载引入组件
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Home = () => import('../views/Home.vue')
const Dashboard = () => import('../views/Dashboard.vue')
const TaskList = () => import('../views/task/TaskList.vue')
const TaskDetail = () => import('../views/task/TaskDetail.vue')
const TaskCreate = () => import('../views/task/TaskCreate.vue')
const GoalList = () => import('../views/goal/GoalList.vue')
const GoalDetail = () => import('../views/goal/GoalDetail.vue')
const GoalCreate = () => import('../views/goal/GoalCreate.vue')
const RecordList = () => import('../views/record/RecordList.vue')
const RecordCreate = () => import('../views/record/RecordCreate.vue')
const ReminderList = () => import('../views/reminder/ReminderList.vue')
const ReminderCreate = () => import('../views/reminder/ReminderCreate.vue')
const UserCenter = () => import('../views/user/UserCenter.vue')

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },
  {
    path: '/home',
    component: Home,
    redirect: '/dashboard',
    children: [
      { path: '/dashboard', component: Dashboard },
      { path: '/task/list', component: TaskList },
      { path: '/task/detail/:id', component: TaskDetail },
      { path: '/task/create', component: TaskCreate },
      { path: '/goal/list', component: GoalList },
      { path: '/goal/detail/:id', component: GoalDetail },
      { path: '/goal/create', component: GoalCreate },
      { path: '/record/list', component: RecordList },
      { path: '/record/create', component: RecordCreate },
      { path: '/reminder/list', component: ReminderList },
      { path: '/reminder/create', component: ReminderCreate },
      { path: '/user/center', component: UserCenter }
    ]
  }
]

const router = new VueRouter({
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  // 获取cookie中的userId
  const userId = Cookies.get('userId')
  // 如果访问的是登录页或注册页，直接放行
  if (to.path === '/login' || to.path === '/register') {
    return next()
  }
  // 如果未登录，跳转到登录页
  if (!userId) {
    return next('/login')
  }
  // 已登录，放行
  next()
})

export default router 