<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <div slot="header">
            <span>欢迎使用</span>
          </div>
          <div class="welcome-content">
            <h2>欢迎使用训练任务管理系统，{{ userInfo ? userInfo.nickname || userInfo.username : '用户' }}</h2>
            <p>通过本系统，您可以方便地管理您的训练任务、设定目标、记录进度，并接收训练提醒。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 数据统计卡片 -->
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="data-content">
              <div class="data-title">训练任务</div>
              <div class="data-value">{{ statistics.taskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon" style="background-color: #67C23A;">
              <i class="el-icon-s-flag"></i>
            </div>
            <div class="data-content">
              <div class="data-title">训练目标</div>
              <div class="data-value">{{ statistics.goalCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon" style="background-color: #E6A23C;">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="data-content">
              <div class="data-title">完成记录</div>
              <div class="data-value">{{ statistics.recordCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon" style="background-color: #F56C6C;">
              <i class="el-icon-alarm-clock"></i>
            </div>
            <div class="data-content">
              <div class="data-title">提醒设置</div>
              <div class="data-value">{{ statistics.reminderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 待办任务 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>待办任务</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToTaskList">查看更多</el-button>
          </div>
          <el-table :data="taskList" style="width: 100%" v-loading="taskLoading">
            <el-table-column prop="taskName" label="任务名称"></el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getTaskStatusType(scope.row.status)">{{ getTaskStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 目标进度 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>目标进度</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToGoalList">查看更多</el-button>
          </div>
          <el-table :data="goalList" style="width: 100%" v-loading="goalLoading">
            <el-table-column prop="goalName" label="目标名称"></el-table-column>
            <el-table-column label="进度" width="180">
              <template slot-scope="scope">
                <el-progress :percentage="calculateProgress(scope.row)" :format="percentageFormat"></el-progress>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getGoalStatusType(scope.row.status)">{{ getGoalStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近提醒 -->
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>最近提醒</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToReminderList">查看更多</el-button>
          </div>
          <el-table :data="reminderList" style="width: 100%" v-loading="reminderLoading">
            <el-table-column prop="remindContent" label="提醒内容"></el-table-column>
            <el-table-column prop="remindTime" label="提醒时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.remindTime) }}
              </template>
            </el-table-column>
            <el-table-column label="关联任务" width="180">
              <template slot-scope="scope">
                {{ getTaskName(scope.row.taskId) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 0 ? 'info' : 'success'">
                  {{ scope.row.status === 0 ? '未提醒' : '已提醒' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      // 统计数据
      statistics: {
        taskCount: 0,
        goalCount: 0,
        recordCount: 0,
        reminderCount: 0
      },
      // 任务列表
      taskList: [],
      taskLoading: false,
      // 目标列表
      goalList: [],
      goalLoading: false,
      // 提醒列表
      reminderList: [],
      reminderLoading: false
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.getStatistics()
    this.getTaskList()
    this.getGoalList()
    this.getReminderList()
  },
  methods: {
    // 获取统计数据
    getStatistics() {
      // 模拟数据
      this.statistics = {
        taskCount: 5,
        goalCount: 3,
        recordCount: 8,
        reminderCount: 4
      }
    },
    // 获取任务列表
    getTaskList() {
      this.taskLoading = true
      this.$http.get('/task/list').then(res => {
        if (res.data.code === 200) {
          this.taskList = res.data.data.slice(0, 5) // 只显示前5条
          this.$store.commit('setTaskList', res.data.data)
        }
      }).finally(() => {
        this.taskLoading = false
      })
    },
    // 获取目标列表
    getGoalList() {
      this.goalLoading = true
      this.$http.get('/goal/list').then(res => {
        if (res.data.code === 200) {
          this.goalList = res.data.data.slice(0, 5) // 只显示前5条
          this.$store.commit('setGoalList', res.data.data)
        }
      }).finally(() => {
        this.goalLoading = false
      })
    },
    // 获取提醒列表
    getReminderList() {
      this.reminderLoading = true
      this.$http.get('/reminder/list').then(res => {
        if (res.data.code === 200) {
          this.reminderList = res.data.data.slice(0, 5) // 只显示前5条
          this.$store.commit('setReminderList', res.data.data)
        }
      }).finally(() => {
        this.reminderLoading = false
      })
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    // 计算目标进度
    calculateProgress(goal) {
      if (!goal.targetValue || goal.targetValue === 0) return 0
      const progress = (goal.currentValue / goal.targetValue) * 100
      return Math.min(progress, 100)
    },
    // 格式化百分比
    percentageFormat(percentage) {
      return percentage.toFixed(0) + '%'
    },
    // 获取任务状态类型
    getTaskStatusType(status) {
      const types = ['info', 'primary', 'success', 'danger']
      return types[status] || 'info'
    },
    // 获取任务状态文本
    getTaskStatusText(status) {
      const texts = ['未开始', '进行中', '已完成', '已取消']
      return texts[status] || '未知'
    },
    // 获取目标状态类型
    getGoalStatusType(status) {
      const types = ['primary', 'success', 'info']
      return types[status] || 'info'
    },
    // 获取目标状态文本
    getGoalStatusText(status) {
      const texts = ['进行中', '已完成', '已放弃']
      return texts[status] || '未知'
    },
    // 获取任务名称
    getTaskName(taskId) {
      const task = this.$store.getters.taskList.find(t => t.id === taskId)
      return task ? task.taskName : '未知任务'
    },
    // 跳转到任务列表
    goToTaskList() {
      this.$router.push('/task/list')
    },
    // 跳转到目标列表
    goToGoalList() {
      this.$router.push('/goal/list')
    },
    // 跳转到提醒列表
    goToReminderList() {
      this.$router.push('/reminder/list')
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  padding: 10px;
}

.data-card {
  margin-bottom: 20px;
}

.data-item {
  display: flex;
  align-items: center;
}

.data-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #409EFF;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 30px;
  color: #fff;
  margin-right: 15px;
}

.data-content {
  flex: 1;
}

.data-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.el-progress {
  margin-top: 10px;
}
</style> 