<template>
  <div class="task-create-container">
    <el-card>
      <div slot="header">
        <span>{{ isEdit ? '编辑训练任务' : '新增训练任务' }}</span>
      </div>
      
      <el-form :model="taskForm" :rules="rules" ref="taskForm" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称"></el-input>
        </el-form-item>
        
        <el-form-item label="任务描述" prop="taskDesc">
          <el-input v-model="taskForm.taskDesc" type="textarea" :rows="3" placeholder="请输入任务描述"></el-input>
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="taskForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="taskForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="重复类型" prop="repeatType">
          <el-select v-model="taskForm.repeatType" placeholder="请选择重复类型" style="width: 100%;">
            <el-option label="不重复" :value="0"></el-option>
            <el-option label="每天" :value="1"></el-option>
            <el-option label="每周" :value="2"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="重复天数" v-if="taskForm.repeatType === 2">
          <el-checkbox-group v-model="repeatDays">
            <el-checkbox :label="1">周一</el-checkbox>
            <el-checkbox :label="2">周二</el-checkbox>
            <el-checkbox :label="3">周三</el-checkbox>
            <el-checkbox :label="4">周四</el-checkbox>
            <el-checkbox :label="5">周五</el-checkbox>
            <el-checkbox :label="6">周六</el-checkbox>
            <el-checkbox :label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="taskForm.status" placeholder="请选择任务状态" style="width: 100%;">
            <el-option label="未开始" :value="0"></el-option>
            <el-option label="进行中" :value="1"></el-option>
            <el-option label="已完成" :value="2"></el-option>
            <el-option label="已取消" :value="3"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">{{ isEdit ? '保存修改' : '立即创建' }}</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TaskCreate',
  data() {
    return {
      // 是否为编辑模式
      isEdit: false,
      // 任务表单
      taskForm: {
        id: null,
        taskName: '',
        taskDesc: '',
        startTime: '',
        endTime: '',
        repeatType: 0,
        repeatDays: '',
        status: 0,

        createTime: '',
        userId: ''
      },
      // 重复天数
      repeatDays: [],
      // 表单验证规则
      rules: {
        taskName: [
          { required: true, message: '请输入任务名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择任务状态', trigger: 'change' }
        ]
      },
      // 加载状态
      loading: false
    }
  },
  created() {
    // 判断是否为编辑模式
    const taskId = this.$route.query.id
    if (taskId) {
      this.isEdit = true
      this.getTaskDetail(taskId)
    }
  },
  watch: {
    '$route.query': {
      handler(v){
        if(Object.keys(v).length === 0) {
          this.taskForm = {
            id: null,
            taskName: '',
            taskDesc: '',
            startTime: '',
            endTime: '',
            repeatType: 0,
            repeatDays: '',
            status: 0,
            createTime: '',
            userId: ''
          }
        }
      }
    }
  },
  methods: {
    // 获取任务详情
    getTaskDetail(id) {
      this.loading = true
      this.$http.get(`/task/${id}`).then(res => {
        if (res.data.code === 200) {
          this.taskForm = res.data.data
          // 解析重复天数
          if (this.taskForm.repeatDays && this.taskForm.repeatType === 2) {
            this.repeatDays = this.taskForm.repeatDays.split(',').map(day => parseInt(day))
          }
        } else {
          this.$message.error(res.data.msg || '获取任务详情失败')
        }
      }).catch(() => {
        this.$message.error('获取任务详情失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 提交表单
    submitForm() {
      this.$refs.taskForm.validate(valid => {
        if (valid) {
          // 处理重复天数
          if (this.taskForm.repeatType === 2 && this.repeatDays.length > 0) {
            this.taskForm.repeatDays = this.repeatDays.sort().join(',')
          } else {
            this.taskForm.repeatDays = null
          }
          
          // 确保表单中的所有必要字段都已设置
          const formData = {
            ...this.taskForm,
            repeatType: this.taskForm.repeatType || 0,
            status: this.taskForm.status || 0
          }
          
          // 打印要提交的数据，用于调试
          console.log('提交的表单数据:', formData)
          
          this.loading = true
          // 判断是新增还是编辑
          const request = this.isEdit
            ? this.$http.post('/task', formData)
            : this.$http.post('/task/add', formData)
          
          request.then(res => {
            console.log('响应数据:', res)
            if (res.data.code === 200) {
              this.$message.success(this.isEdit ? '修改成功' : '创建成功')
              this.$router.push('/task/list')
            } else {
              this.$message.error(res.data.msg || (this.isEdit ? '修改失败' : '创建失败'))
            }
          }).catch(err => {
            console.error('请求错误:', err)
            this.$message.error(this.isEdit ? '修改失败' : '创建失败')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    // 取消
    cancel() {
      this.$router.push('/task/list')
    }
  }
}
</script>

<style scoped>
.task-create-container {
  padding: 20px;
}

.el-select {
  display: block;
}

.el-date-picker {
  width: 100%;
}
</style> 