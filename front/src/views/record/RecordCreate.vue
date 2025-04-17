<template>
  <div class="record-create-container">
    <el-card>
      <div slot="header">
        <span>添加运动记录</span>
      </div>
      
      <el-form :model="recordForm" :rules="rules" ref="recordForm" label-width="100px" v-loading="loading">
        <el-form-item label="训练目标" prop="goalId">
          <el-select v-model="recordForm.goalId" placeholder="请选择训练目标" @change="handleGoalChange">
            <el-option
              v-for="item in goalList"
              :key="item.id"
              :label="item.goalName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="recordForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="记录值" prop="recordValue">
          <el-input-number v-model="recordForm.recordValue" :min="0" :precision="2" :step="1"></el-input-number>
          <span class="unit-text">{{ recordForm.unit }}</span>
        </el-form-item>
        
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="recordForm.recordDate"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="备注" prop="recordRemark">
          <el-input type="textarea" v-model="recordForm.recordRemark" placeholder="请输入备注" :rows="3"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">立即创建</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'RecordCreate',
  data() {
    return {
      loading: false,
      submitting: false,
      goalList: [],
      recordForm: {
        goalId: '',
        goalName: '',
        startDate: '',
        recordValue: 0,
        recordDate: '',
        recordRemark: '',
        unit: ''
      },
      rules: {
        goalId: [
          { required: true, message: '请选择训练目标', trigger: 'change' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
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
    this.getGoalList()
    // 设置默认记录日期为当前时间
    this.recordForm.recordDate = this.formatDateTime(new Date())
    // 设置默认开始日期为当前日期
    this.recordForm.startDate = this.formatDate(new Date())
  },
  methods: {
    // 获取目标列表
    getGoalList() {
      this.loading = true
      this.$http.get('/goal/list').then(res => {
        if (res.data.code === 200) {
          this.goalList = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '获取目标列表失败')
        }
      }).catch(() => {
        this.$message.error('获取目标列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 目标选择变化
    handleGoalChange(goalId) {
      const goal = this.goalList.find(g => g.id === goalId)
      if (goal) {
        this.recordForm.goalName = goal.goalName
        this.recordForm.unit = goal.unit
      }
    },
    // 提交表单
    submitForm() {
      this.$refs.recordForm.validate(valid => {
        if (valid) {
          this.submitting = true
          
          // 确保所有必要字段都已设置
          const formData = {
            ...this.recordForm,
            startDate: this.recordForm.startDate || this.formatDate(new Date()) // 如果为空，使用今天的日期
          }
          
          // 打印要提交的数据，用于调试
          console.log('提交的表单数据:', formData)
          
          this.$http.post('/goal/add', formData).then(res => {
            if (res.data.code === 200) {
              this.$message.success('创建成功')
              this.$router.push('/record/list')
            } else {
              this.$message.error(res.data.msg || '创建失败')
            }
          }).catch(err => {
            console.error('请求错误:', err)
            this.$message.error('创建失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.recordForm.resetFields()
      this.recordForm = {
        goalId: '',
        goalName: '',
        startDate: this.formatDate(new Date()),
        recordValue: 0,
        recordDate: this.formatDateTime(new Date()),
        recordRemark: '',
        unit: ''
      }
    },
    // 返回
    goBack() {
      this.$router.go(-1)
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
    },
    // 格式化日期时间
    formatDateTime(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}:00`
    }
  }
}
</script>

<style scoped>
.record-create-container {
  padding: 20px;
}

.unit-text {
  margin-left: 10px;
  color: #606266;
}
</style> 