<template>
  <div class="reminder-list-container">
    <el-card>
      <div slot="header">
        <span>提醒事项列表</span>
        <el-button style="float: right" type="primary" size="small" @click="goToCreate">新增提醒</el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="状态" clearable>
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 提醒列表 -->
      <el-table :data="filteredReminderList" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="120"></el-table-column>
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="remindTime" label="提醒时间" width="180">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.remindTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="repeatType" label="重复类型" width="120">
          <template slot-scope="scope">
            {{ getRepeatTypeText(scope.row.repeatType) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount">
        </el-pagination>
      </div>
      
      <!-- 空数据显示 -->
      <div class="empty-data" v-if="reminderList.length === 0 && !loading">
        <el-empty description="暂无提醒事项"></el-empty>
      </div>
    </el-card>
    
    <!-- 编辑提醒对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form :model="reminderForm" :rules="rules" ref="reminderForm" label-width="80px">
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
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitReminder" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除提醒"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这个提醒吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ReminderList',
  data() {
    return {
      // 提醒列表
      reminderList: [],
      // 搜索表单
      searchForm: {
        status: ''
      },
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10
      },
      // 加载状态
      loading: false,
      // 对话框
      dialogVisible: false,
      dialogTitle: '添加提醒',
      submitting: false,
      reminderForm: {
        id: null,
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
      },
      // 删除对话框
      deleteDialogVisible: false,
      currentReminder: null
    }
  },
  computed: {
    // 过滤后的提醒列表
    filteredReminderList() {
      let result = this.reminderList.filter(reminder => {
        // 按状态过滤
        if (this.searchForm.status !== '' && reminder.status !== this.searchForm.status) {
          return false
        }
        return true
      })
      
      // 按提醒时间排序
      result.sort((a, b) => new Date(a.remindTime) - new Date(b.remindTime))
      
      // 分页处理
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      return result.slice(start, end)
    },
    // 总记录数
    totalCount() {
      return this.reminderList.filter(reminder => {
        if (this.searchForm.status !== '' && reminder.status !== this.searchForm.status) {
          return false
        }
        return true
      }).length
    }
  },
  created() {
    this.getReminderList()
  },
  methods: {
    // 获取提醒列表
    getReminderList() {
      this.loading = true
      this.$http.get('/reminder/list').then(res => {
        if (res.data.code === 200) {
          this.reminderList = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '获取提醒列表失败')
        }
      }).catch(() => {
        this.$message.error('获取提醒列表失败')
      }).finally(() => {
        this.loading = false
      })
    },
    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        status: ''
      }
      this.handleSearch()
    },
    // 格式化日期时间
    formatDateTime(date) {
      if (!date) return '--'
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
    },
    // 获取重复类型文本
    getRepeatTypeText(type) {
      const types = {
        0: '不重复',
        1: '每天',
        2: '每周',
        3: '每月'
      }
      return types[type] || '未知'
    },
    // 新增提醒
    goToCreate() {
      this.dialogTitle = '添加提醒'
      this.reminderForm = {
        id: null,
        title: '',
        content: '',
        remindTime: this.formatDateTime(new Date()),
        repeatType: 0,
        status: 1
      }
      this.dialogVisible = true
    },
    // 编辑提醒
    handleEdit(reminder) {
      this.dialogTitle = '编辑提醒'
      this.reminderForm = { ...reminder }
      this.dialogVisible = true
    },
    // 提交提醒
    submitReminder() {
      this.$refs.reminderForm.validate(valid => {
        if (valid) {
          this.submitting = true
          
          const method = this.reminderForm.id ? 'put' : 'post'
          const url = this.reminderForm.id ? '/reminder' : '/reminder/add'
          
          this.$http[method](url, this.reminderForm).then(res => {
            if (res.data.code === 200) {
              this.$message.success(this.reminderForm.id ? '修改成功' : '添加成功')
              this.dialogVisible = false
              this.getReminderList()
            } else {
              this.$message.error(res.data.msg || (this.reminderForm.id ? '修改失败' : '添加失败'))
            }
          }).catch(() => {
            this.$message.error(this.reminderForm.id ? '修改失败' : '添加失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    // 删除提醒
    handleDelete(reminder) {
      this.currentReminder = reminder
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      if (!this.currentReminder) return
      
      this.$http.delete(`/reminder/${this.currentReminder.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.getReminderList()
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
      })
    },
    // 改变每页大小
    handleSizeChange(val) {
      this.pagination.pageSize = val
    },
    // 改变当前页
    handleCurrentChange(val) {
      this.pagination.currentPage = val
    }
  }
}
</script>

<style scoped>
.reminder-list-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.empty-data {
  padding: 30px 0;
}
</style> 