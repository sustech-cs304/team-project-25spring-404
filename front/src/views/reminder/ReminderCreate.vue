<template>
  <div class="reminder-create-container">
    <el-card>
      <div slot="header">
        <span>添加提醒事项</span>
      </div>
      
      <el-form :model="reminderForm" :rules="rules" ref="reminderForm" label-width="100px" v-loading="loading">
        <el-form-item label="标题" prop="title">
          <el-input v-model="reminderForm.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="reminderForm.content" placeholder="请输入内容" :rows="3"></el-input>
        </el-form-item>
        
        <el-form-item label="提醒时间" prop="remindTime">
          <el-date-picker
            v-model="reminderForm.remindTime"
            type="datetime"
            placeholder="选择日期时间"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="重复类型" prop="repeatType">
          <el-select v-model="reminderForm.repeatType" placeholder="请选择重复类型">
            <el-option label="不重复" :value="0"></el-option>
            <el-option label="每天" :value="1"></el-option>
            <el-option label="每周" :value="2"></el-option>
            <el-option label="每月" :value="3"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="reminderForm.status"
            :active-value="1"
            :inactive-value="0">
          </el-switch>
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
  name: 'ReminderCreate',
  data() {
    return {
      loading: false,
      submitting: false,
      reminderForm: {
        title: '',
        content: '',
        remindTime: '',
        repeatType: 0,
        status: 1
      },
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ],
        remindTime: [
          { required: true, message: '请选择提醒时间', trigger: 'change' }
        ],
        repeatType: [
          { required: true, message: '请选择重复类型', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    // 设置默认提醒时间为当前时间
    this.reminderForm.remindTime = this.formatDateTime(new Date())
  },
  methods: {
    // 提交表单
    submitForm() {
      this.$refs.reminderForm.validate(valid => {
        if (valid) {
          this.submitting = true
          this.$http.post('/reminder/add', this.reminderForm).then(res => {
            if (res.data.code === 200) {
              this.$message.success('创建成功')
              this.$router.push('/reminder/list')
            } else {
              this.$message.error(res.data.msg || '创建失败')
            }
          }).catch(() => {
            this.$message.error('创建失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.reminderForm.resetFields()
      this.reminderForm = {
        title: '',
        content: '',
        remindTime: this.formatDateTime(new Date()),
        repeatType: 0,
        status: 1
      }
    },
    // 返回
    goBack() {
      this.$router.go(-1)
    },
    // 格式化日期时间
    formatDateTime(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.reminder-create-container {
  padding: 20px;
}
</style> 