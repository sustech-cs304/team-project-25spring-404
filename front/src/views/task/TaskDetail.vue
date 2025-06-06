<template>
  <div class="task-detail-container">
    <el-card v-loading="loading">
      <div slot="header">
        <span>任务详情</span>
        <div class="operation-btns">
          <el-button type="primary" size="small" @click="handleEdit" style="margin-right: 10px;">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete">删除</el-button>
        </div>
      </div>
      
      <el-row :gutter="20" v-if="task">
        <el-col :span="24">
          <div class="task-title">
            <h2>{{ task.taskName }}</h2>
            <el-tag :type="getStatusTag(task.status)" class="task-status">
              {{ getStatusText(task.status) }}
            </el-tag>
          </div>
        </el-col>
        
        <el-col :span="24">
          <div class="info-item">
            <span class="label">任务描述：</span>
            <span class="content">{{ task.taskDesc || '暂无描述' }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">开始时间：</span>
            <span class="content">{{ formatDate(task.startTime) }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">结束时间：</span>
            <span class="content">{{ formatDate(task.endTime) || '无限期' }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">重复类型：</span>
            <span class="content">
              <el-tag :type="getRepeatTypeTag(task.repeatType)" size="small">
                {{ getRepeatTypeText(task.repeatType) }}
              </el-tag>
            </span>
          </div>
        </el-col>
        
        <el-col :span="12" v-if="task.repeatType === 2">
          <div class="info-item">
            <span class="label">重复天数：</span>
            <span class="content">{{ formatRepeatDays(task.repeatDays) }}</span>
          </div>
        </el-col>
        
        <el-col :span="24">
          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="content">{{ formatDate(task.createTime) }}</span>
          </div>
        </el-col>
        
        <el-col :span="24" v-if="task.updateTime !== task.createTime">
          <div class="info-item">
            <span class="label">更新时间：</span>
            <span class="content">{{ formatDate(task.updateTime) }}</span>
          </div>
        </el-col>
      </el-row>
      
      <div class="empty-data" v-else>
        <el-empty description="暂无任务数据"></el-empty>
      </div>
      
      <div class="action-buttons" v-if="task">
        <el-button type="success" @click="completeTask" :disabled="task.status === 2" v-if="task.status !== 3">
          {{ task.status === 2 ? '已完成' : '标记为已完成' }}
        </el-button>
        <el-button type="info" @click="cancelTask" :disabled="task.status === 3" v-if="task.status !== 2">
          {{ task.status === 3 ? '已取消' : '取消任务' }}
        </el-button>
      </div>
    </el-card>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除任务"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这个任务吗？删除后无法恢复！</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TaskDetail',
  data() {
    return {
      task: null,
      loading: false,
      deleteDialogVisible: false
    }
  },
  created() {
    this.getTaskDetail()
  },
  methods: {
    // 获取任务详情
    getTaskDetail() {
      const taskId = this.$route.params.id
      if (!taskId) {
        this.$message.error('任务ID不能为空')
        return
      }
      
      this.loading = true
      this.$http.get(`/task/${taskId}`).then(res => {
        if (res.data.code === 200) {
          this.task = res.data.data
        } else {
          this.$message.error(res.data.msg || '获取任务详情失败')
        }
      }).catch(() => {
        this.$message.error('获取任务详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    // 获取状态标签类型
    getStatusTag(status) {
      const tags = ['info', 'primary', 'success', 'danger']
      return tags[status] || 'info'
    },
    // 获取状态文本
    getStatusText(status) {
      const texts = ['未开始', '进行中', '已完成', '已取消']
      return texts[status] || '未知'
    },
    // 获取重复类型标签
    getRepeatTypeTag(type) {
      const tags = ['', 'success', 'warning']
      return tags[type] || ''
    },
    // 获取重复类型文本
    getRepeatTypeText(type) {
      const texts = ['不重复', '每天', '每周']
      return texts[type] || '未知'
    },
    // 格式化重复天数
    formatRepeatDays(days) {
      if (!days) return '无'
      
      const dayMap = {
        '0': '周日',
        '1': '周一',
        '2': '周二',
        '3': '周三',
        '4': '周四',
        '5': '周五',
        '6': '周六'
      }
      
      return days.split(',').map(day => dayMap[day]).join('、')
    },
    // 编辑任务
    handleEdit() {
      this.$router.push({
        path: '/task/create',
        query: { id: this.task.id }
      })
    },
    // 删除任务
    handleDelete() {
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      this.$http.delete(`/task/${this.task.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.$router.push('/task/list')
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
      })
    },
    // 标记为已完成
    completeTask() {
      const taskData = { ...this.task, status: 2 }
      this.updateTaskStatus(taskData)
    },
    // 取消任务
    cancelTask() {
      const taskData = { ...this.task, status: 3 }
      this.updateTaskStatus(taskData)
    },
    // 更新任务状态
    updateTaskStatus(taskData) {
      this.loading = true
      this.$http.put('/task', taskData).then(res => {
        if (res.data.code === 200) {
          this.$message.success('更新任务状态成功')
          this.getTaskDetail()
          // 更新store中的任务
          this.$store.commit('updateTask', taskData)
        } else {
          this.$message.error(res.data.msg || '更新任务状态失败')
        }
      }).catch(() => {
        this.$message.error('更新任务状态失败')
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.task-detail-container {
  padding: 20px;
}

.operation-btns {
  float: right;
}

.task-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.task-title h2 {
  margin: 0;
  font-size: 18px;
}

.task-status {
  margin-left: 10px;
}

.info-item {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  margin-right: 5px;
  color: #606266;
}

.content {
  color: #333;
}

.action-buttons {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
  text-align: center;
}

.empty-data {
  padding: 30px 0;
}
</style> 