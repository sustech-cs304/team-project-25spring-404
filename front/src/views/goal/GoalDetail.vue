<template>
  <div class="goal-detail-container">
    <el-card v-loading="loading">
      <div slot="header">
        <span>目标详情</span>
        <div class="operation-btns">
          <el-button type="primary" size="small" @click="handleEdit" style="margin-right: 10px;">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete">删除</el-button>
        </div>
      </div>
      
      <el-row :gutter="20" v-if="goal">
        <el-col :span="24">
          <div class="goal-header">
            <h2>{{ goal.goalName }}</h2>
            <el-tag :type="getStatusTag(goal.status)" class="goal-status">
              {{ getStatusText(goal.status) }}
            </el-tag>
          </div>
        </el-col>
        
        <el-col :span="24">
          <div class="progress-section">
            <el-progress :percentage="calculateProgress(goal)" :format="percentageFormat" :status="getProgressStatus(goal)"></el-progress>
            <div class="progress-text">
              <span>{{ goal.currentValue }} / {{ goal.targetValue }} {{ goal.unit }}</span>
            </div>
          </div>
        </el-col>
        
        <el-col :span="24">
          <div class="info-item">
            <span class="label">目标描述：</span>
            <span class="content">{{ goal.goalDesc || '暂无描述' }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">开始日期：</span>
            <span class="content">{{ formatDate(goal.startDate) }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">截止日期：</span>
            <span class="content">{{ formatDate(goal.endDate) || '无限期' }}</span>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="content">{{ formatDateTime(goal.createTime) }}</span>
          </div>
        </el-col>
        
        <el-col :span="12" v-if="goal.updateTime !== goal.createTime">
          <div class="info-item">
            <span class="label">更新时间：</span>
            <span class="content">{{ formatDateTime(goal.updateTime) }}</span>
          </div>
        </el-col>
        
        <el-col :span="24">
          <div class="record-section">
            <div class="record-header">
              <h3>进度记录</h3>
              <el-button type="primary" size="small" @click="showAddRecordDialog">添加记录</el-button>
            </div>
            
            <el-table :data="recordList" style="width: 100%" size="small">
              <el-table-column prop="recordValue" label="记录值" width="120">
                <template slot-scope="scope">
                  {{ scope.row.recordValue }} {{ goal.unit }}
                </template>
              </el-table-column>
              <el-table-column prop="recordDate" label="记录日期" width="180">
                <template slot-scope="scope">
                  {{ formatDateTime(scope.row.recordDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="recordRemark" label="备注"></el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope">
                  <el-button type="danger" size="mini" @click="deleteRecord(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="empty-data" v-if="recordList.length === 0">
              <el-empty description="暂无进度记录"></el-empty>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <div class="empty-data" v-else>
        <el-empty description="暂无目标数据"></el-empty>
      </div>
      
      <div class="action-buttons" v-if="goal && goal.status === 0">
        <el-button type="success" @click="completeGoal">标记为已完成</el-button>
        <el-button type="info" @click="abandonGoal">放弃目标</el-button>
      </div>
    </el-card>
    
    <!-- 添加记录对话框 -->
    <el-dialog title="添加进度记录" :visible.sync="recordDialogVisible" width="40%">
      <el-form :model="recordForm" :rules="recordRules" ref="recordForm" label-width="80px">
        <el-form-item label="记录值" prop="recordValue">
          <el-input-number v-model="recordForm.recordValue" :min="0" :precision="2" :step="1"></el-input-number>
          <span class="unit-text">{{ goal ? goal.unit : '' }}</span>
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="recordForm.recordDate"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="recordRemark">
          <el-input type="textarea" v-model="recordForm.recordRemark" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="recordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRecord" :loading="recordSubmitting">确 定</el-button>
      </span>
    </el-dialog>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除目标"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这个目标吗？删除后无法恢复！</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'GoalDetail',
  data() {
    return {
      goal: null,
      recordList: [],
      loading: false,
      deleteDialogVisible: false,
      recordDialogVisible: false,
      recordSubmitting: false,
      recordForm: {
        recordValue: 0,
        recordDate: new Date(),
        recordRemark: ''
      },
      recordRules: {
        recordValue: [
          { required: true, message: '请输入记录值', trigger: 'blur' }
        ],
        recordDate: [
          { required: true, message: '请选择记录日期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getGoalDetail()
    this.getRecordList()
  },
  methods: {
    // 获取目标详情
    getGoalDetail() {
      const goalId = this.$route.params.id
      if (!goalId) {
        this.$message.error('目标ID不能为空')
        return
      }
      
      this.loading = true
      this.$http.get(`/goal/${goalId}`).then(res => {
        if (res.data.code === 200) {
          this.goal = res.data.data
        } else {
          this.$message.error(res.data.msg || '获取目标详情失败')
        }
      }).catch(() => {
        this.$message.error('获取目标详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 获取记录列表
    getRecordList() {
      const goalId = this.$route.params.id
      if (!goalId) return
      
      this.$http.get(`/goal/record/list/${goalId}`).then(res => {
        if (res.data.code === 200) {
          this.recordList = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '获取记录列表失败')
        }
      }).catch(() => {
        this.$message.error('获取记录列表失败')
      })
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '--'
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
    },
    // 格式化日期时间
    formatDateTime(date) {
      if (!date) return '--'
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
    // 获取进度条状态
    getProgressStatus(goal) {
      if (goal.status === 1) return 'success'
      if (goal.status === 2) return 'exception'
      if (this.calculateProgress(goal) >= 100) return 'success'
      return ''
    },
    // 获取状态标签
    getStatusTag(status) {
      const tags = ['primary', 'success', 'info']
      return tags[status] || 'info'
    },
    // 获取状态文本
    getStatusText(status) {
      const texts = ['进行中', '已完成', '已放弃']
      return texts[status] || '未知'
    },
    // 编辑目标
    handleEdit() {
      this.$router.push({
        path: '/goal/create',
        query: { id: this.goal.id }
      })
    },
    // 删除目标
    handleDelete() {
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      this.$http.delete(`/goal/${this.goal.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.$router.push('/goal/list')
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
      })
    },
    // 完成目标
    completeGoal() {
      this.updateGoalStatus(1)
    },
    // 放弃目标
    abandonGoal() {
      this.updateGoalStatus(2)
    },
    // 更新目标状态
    updateGoalStatus(status) {
      const goalData = { ...this.goal, status }
      this.loading = true
      this.$http.put('/goal', goalData).then(res => {
        if (res.data.code === 200) {
          this.$message.success('更新目标状态成功')
          this.getGoalDetail()
        } else {
          this.$message.error(res.data.msg || '更新目标状态失败')
        }
      }).catch(() => {
        this.$message.error('更新目标状态失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 显示添加记录对话框
    showAddRecordDialog() {
      this.recordForm = {
        recordValue: 0,
        recordDate: new Date(),
        recordRemark: ''
      }
      this.recordDialogVisible = true
    },
    // 提交记录
    submitRecord() {
      this.$refs.recordForm.validate(valid => {
        if (valid) {
          this.recordSubmitting = true
          const record = {
            ...this.recordForm,
            goalId: this.goal.id
          }
          
          this.$http.post('/goal/record', record).then(res => {
            if (res.data.code === 200) {
              this.$message.success('添加记录成功')
              this.recordDialogVisible = false
              // 刷新记录列表和目标详情
              this.getRecordList()
              this.getGoalDetail()
            } else {
              this.$message.error(res.data.msg || '添加记录失败')
            }
          }).catch(() => {
            this.$message.error('添加记录失败')
          }).finally(() => {
            this.recordSubmitting = false
          })
        }
      })
    },
    // 删除记录
    deleteRecord(record) {
      this.$confirm('确认删除此记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/goal/record/${record.id}`).then(res => {
          if (res.data.code === 200) {
            this.$message.success('删除记录成功')
            // 刷新记录列表和目标详情
            this.getRecordList()
            this.getGoalDetail()
          } else {
            this.$message.error(res.data.msg || '删除记录失败')
          }
        }).catch(() => {
          this.$message.error('删除记录失败')
        })
      }).catch(() => {
        // 取消删除
      })
    }
  }
}
</script>

<style scoped>
.goal-detail-container {
  padding: 20px;
}

.operation-btns {
  float: right;
}

.goal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.goal-header h2 {
  margin: 0;
  font-size: 18px;
}

.goal-status {
  margin-left: 10px;
}

.progress-section {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  background-color: #f9f9f9;
}

.progress-text {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
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

.record-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.record-header h3 {
  margin: 0;
  font-size: 16px;
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

.unit-text {
  margin-left: 10px;
  color: #606266;
}
</style> 