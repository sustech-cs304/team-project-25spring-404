import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 用户信息
    userInfo: null,
    // 任务列表
    taskList: [],
    // 目标列表
    goalList: [],
    // 提醒列表
    reminderList: []
  },
  mutations: {
    // 设置用户信息
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo
    },
    // 清除用户信息
    clearUserInfo(state) {
      state.userInfo = null
    },
    // 设置任务列表
    setTaskList(state, taskList) {
      state.taskList = taskList
    },
    // 添加任务
    addTask(state, task) {
      state.taskList.push(task)
    },
    // 更新任务
    updateTask(state, task) {
      const index = state.taskList.findIndex(item => item.id === task.id)
      if (index !== -1) {
        state.taskList.splice(index, 1, task)
      }
    },
    // 删除任务
    deleteTask(state, taskId) {
      const index = state.taskList.findIndex(item => item.id === taskId)
      if (index !== -1) {
        state.taskList.splice(index, 1)
      }
    },
    // 设置目标列表
    setGoalList(state, goalList) {
      state.goalList = goalList
    },
    // 添加目标
    addGoal(state, goal) {
      state.goalList.push(goal)
    },
    // 更新目标
    updateGoal(state, goal) {
      const index = state.goalList.findIndex(item => item.id === goal.id)
      if (index !== -1) {
        state.goalList.splice(index, 1, goal)
      }
    },
    // 删除目标
    deleteGoal(state, goalId) {
      const index = state.goalList.findIndex(item => item.id === goalId)
      if (index !== -1) {
        state.goalList.splice(index, 1)
      }
    },
    // 设置提醒列表
    setReminderList(state, reminderList) {
      state.reminderList = reminderList
    },
    // 添加提醒
    addReminder(state, reminder) {
      state.reminderList.push(reminder)
    },
    // 更新提醒
    updateReminder(state, reminder) {
      const index = state.reminderList.findIndex(item => item.id === reminder.id)
      if (index !== -1) {
        state.reminderList.splice(index, 1, reminder)
      }
    },
    // 删除提醒
    deleteReminder(state, reminderId) {
      const index = state.reminderList.findIndex(item => item.id === reminderId)
      if (index !== -1) {
        state.reminderList.splice(index, 1)
      }
    }
  },
  actions: {
    // 获取用户信息
    getUserInfo({ commit }) {
      return new Promise((resolve, reject) => {
        Vue.prototype.$http.get('/user/info').then(res => {
          const { data } = res.data
          commit('setUserInfo', data)
          resolve(data)
        }).catch(err => {
          reject(err)
        })
      })
    }
  },
  getters: {
    // 获取用户信息
    userInfo: state => state.userInfo,
    // 获取任务列表
    taskList: state => state.taskList,
    // 获取目标列表
    goalList: state => state.goalList,
    // 获取提醒列表
    reminderList: state => state.reminderList
  },
  modules: {
  }
}) 