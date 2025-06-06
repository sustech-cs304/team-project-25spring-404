<template>
  <div class="goal-create-container">
    <el-card>
      <div slot="header">
        <span>{{ isEdit ? '编辑目标' : '创建目标' }}</span>
      </div>
      
      <el-form :model="goalForm" :rules="rules" ref="goalForm" label-width="100px" v-loading="loading">
        <el-form-item label="目标名称" prop="goalName">
          <el-input v-model="goalForm.goalName" placeholder="请输入目标名称"></el-input>
        </el-form-item>
        
        <el-form-item label="目标描述" prop="goalDesc">
          <el-input type="textarea" v-model="goalForm.goalDesc" placeholder="请输入目标描述" :rows="3"></el-input>
        </el-form-item>
        
        <el-form-item label="目标值" prop="targetValue">
          <el-input-number v-model="goalForm.targetValue" :precision="2" :step="1" :min="0"></el-input-number>
        </el-form-item>
        
        <el-form-item label="当前值" prop="currentValue">
          <el-input-number v-model="goalForm.currentValue" :precision="2" :step="1" :min="0"></el-input-number>
        </el-form-item>
        
        <el-form-item label="单位" prop="unit">
          <el-input v-model="goalForm.unit" placeholder="例如：公里、次、千克等"></el-input>
        </el-form-item>
        
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="goalForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="截止日期" prop="endDate">
          <el-date-picker
            v-model="goalForm.endDate"
            type="date"
            placeholder="选择截止日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="目标状态" prop="status">
          <el-select v-model="goalForm.status" placeholder="请选择目标状态">
            <el-option label="进行中" :value="0"></el-option>
            <el-option label="已完成" :value="1"></el-option>
            <el-option label="已放弃" :value="2"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">{{ isEdit ? '保存修改' : '立即创建' }}</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'GoalCreate',
  data() {
    return {
      isEdit: false,
      loading: false,
      submitting: false,
      goalForm: {
        id: null,
        goalName: '',
        goalDesc: '',
        targetValue: 0,
        currentValue: 0,
        unit: '',
        startDate: '',
        endDate: '',
        status: 0
      },
      rules: {
        goalName: [
          { required: true, message: '请输入目标名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        targetValue: [
          { required: true, message: '请输入目标值', trigger: 'blur' }
        ],
        unit: [
          { required: true, message: '请输入单位', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    // 判断是否为编辑模式
    const id = this.$route.query.id
    if (id) {
      this.isEdit = true
      this.getGoalDetail(id)
    } else {
      // 设置默认开始日期为今天
      this.goalForm.startDate = this.formatDate(new Date())
    }
  },
  watch: {
    '$route.query': {
      handler(v){
        if(Object.keys(v).length === 0) {
          this.goalForm = {
            id: null,
            goalName: '',
            goalDesc: '',
            targetValue: 0,
            currentValue: 0,
            unit: '',
            startDate: '',
            endDate: '',
            status: 0
          }
        }
      }
    }
  },
  methods: {
    // 获取目标详情
    getGoalDetail(id) {
      this.loading = true
      this.$http.get(`/goal/${id}`).then(res => {
        if (res.data.code === 200) {
          this.goalForm = res.data.data
          
          // 确保日期字段格式正确
          if (this.goalForm.startDate) {
            this.goalForm.startDate = this.formatDate(new Date(this.goalForm.startDate))
          }
          if (this.goalForm.endDate) {
            this.goalForm.endDate = this.formatDate(new Date(this.goalForm.endDate))
          }
        } else {
          this.$message.error(res.data.msg || '获取目标详情失败')
        }
      }).catch(() => {
        this.$message.error('获取目标详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 提交表单
    submitForm() {
      this.$refs.goalForm.validate(valid => {
        if (valid) {
          this.submitting = true
          
          // 验证目标值和当前值
          if (this.goalForm.currentValue > this.goalForm.targetValue) {
            this.$message.warning('当前值不能大于目标值')
            this.submitting = false
            return
          }
          
          // 验证日期
          if (this.goalForm.endDate && new Date(this.goalForm.endDate) < new Date(this.goalForm.startDate)) {
            this.$message.warning('截止日期不能早于开始日期')
            this.submitting = false
            return
          }
          
          // 确保所有必要字段都已设置
          const formData = {
            ...this.goalForm,
            startDate: this.goalForm.startDate || this.formatDate(new Date()), // 如果为空，使用今天的日期
            status: this.goalForm.status || 0,
            currentValue: this.goalForm.currentValue || 0
          }
          
          // 打印要提交的数据，用于调试
          console.log('提交的表单数据:', formData)
          
          const url = this.isEdit ? '/goal' : '/goal/add'
          const method = this.isEdit ? 'put' : 'post'
          
          this.$http[method](url, formData).then(res => {
            console.log('响应结果:', res)
            if (res.data.code === 200) {
              this.$message.success(this.isEdit ? '编辑成功' : '创建成功')
              // 跳转到目标列表
              this.$router.push('/goal/list')
            } else {
              this.$message.error(res.data.msg || (this.isEdit ? '编辑失败' : '创建失败'))
            }
          }).catch(err => {
            console.error('请求错误:', err)
            this.$message.error(this.isEdit ? '编辑失败' : '创建失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.goalForm.resetFields()
      
      if (!this.isEdit) {
        this.goalForm = {
          id: null,
          goalName: '',
          goalDesc: '',
          targetValue: 0,
          currentValue: 0,
          unit: '',
          startDate: this.formatDate(new Date()),
          endDate: '',
          status: 0
        }
      } else {
        this.getGoalDetail(this.goalForm.id)
      }
    },
    // 返回
    goBack() {
      this.$router.go(-1)
    },
    // 格式化日期
    formatDate(date) {
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.goal-create-container {
  padding: 20px;
}
</style> 