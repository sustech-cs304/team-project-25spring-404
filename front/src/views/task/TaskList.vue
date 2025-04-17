<template>
  <div class="task-list-container">
    <el-card>
      <div slot="header">
        <span>训练任务列表</span>
        <el-button style="float: right" type="primary" size="small" @click="goToCreate">新增任务</el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="请输入任务名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="请选择任务状态" clearable>
            <el-option label="未开始" :value="0"></el-option>
            <el-option label="进行中" :value="1"></el-option>
            <el-option label="已完成" :value="2"></el-option>
            <el-option label="已取消" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.repeatType" placeholder="请选择重复类型" clearable>
            <el-option label="不重复" :value="0"></el-option>
            <el-option label="每天" :value="1"></el-option>
            <el-option label="每周" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 任务列表 -->
      <el-table :data="filteredTaskList" v-loading="loading" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称"></el-table-column>
        <el-table-column prop="taskDesc" label="任务描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="开始时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="重复类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getRepeatTypeTag(scope.row.repeatType)">
              {{ getRepeatTypeText(scope.row.repeatType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleDetail(scope.row)">详情</el-button>
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
    </el-card>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除任务"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这个任务吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TaskList',
  data() {
    return {
      // 任务列表
      taskList: [],
      // 搜索表单
      searchForm: {
        keyword: '',
        status: '',
        repeatType: ''
      },
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10
      },
      // 加载状态
      loading: false,
      // 删除对话框
      deleteDialogVisible: false,
      // 当前操作的任务
      currentTask: null
    }
  },
  computed: {
    // 过滤后的任务列表
    filteredTaskList() {
      let result = this.taskList.filter(task => {
        // 按关键字过滤
        if (this.searchForm.keyword && 
            !task.taskName.toLowerCase().includes(this.searchForm.keyword.toLowerCase()) &&
            !task.taskDesc.toLowerCase().includes(this.searchForm.keyword.toLowerCase())) {
          return false
        }
        // 按状态过滤
        if (this.searchForm.status !== '' && task.status !== this.searchForm.status) {
          return false
        }
        // 按重复类型过滤
        if (this.searchForm.repeatType !== '' && task.repeatType !== this.searchForm.repeatType) {
          return false
        }
        return true
      })
      
      // 计算总数
      const total = result.length
      
      // 分页处理
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      result = result.slice(start, end)
      
      return result
    },
    // 总记录数
    totalCount() {
      return this.taskList.filter(task => {
        // 按关键字过滤
        if (this.searchForm.keyword && 
            !task.taskName.toLowerCase().includes(this.searchForm.keyword.toLowerCase()) &&
            !task.taskDesc.toLowerCase().includes(this.searchForm.keyword.toLowerCase())) {
          return false
        }
        // 按状态过滤
        if (this.searchForm.status !== '' && task.status !== this.searchForm.status) {
          return false
        }
        // 按重复类型过滤
        if (this.searchForm.repeatType !== '' && task.repeatType !== this.searchForm.repeatType) {
          return false
        }
        return true
      }).length
    }
  },
  created() {
    this.getTaskList()
  },
  methods: {
    // 获取任务列表
    getTaskList() {
      this.loading = true
      this.$http.get('/task/list').then(res => {
        if (res.data.code === 200) {
          this.taskList = res.data.data
          this.$store.commit('setTaskList', res.data.data)
        } else {
          this.$message.error(res.data.msg || '获取任务列表失败')
        }
      }).catch(() => {
        this.$message.error('获取任务列表失败')
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
        keyword: '',
        status: '',
        repeatType: ''
      }
      this.handleSearch()
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '--'
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
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
    // 获取状态标签
    getStatusTag(status) {
      const tags = ['info', 'primary', 'success', 'danger']
      return tags[status] || 'info'
    },
    // 获取状态文本
    getStatusText(status) {
      const texts = ['未开始', '进行中', '已完成', '已取消']
      return texts[status] || '未知'
    },
    // 查看详情
    handleDetail(row) {
      this.$router.push(`/task/detail/${row.id}`)
    },
    // 编辑任务
    handleEdit(row) {
      this.$router.push({
        path: '/task/create',
        query: { id: row.id }
      })
    },
    // 删除任务
    handleDelete(row) {
      this.currentTask = row
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      if (!this.currentTask) return
      this.$http.delete(`/task/${this.currentTask.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.getTaskList()
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
        this.currentTask = null
      })
    },
    // 新增任务
    goToCreate() {
      this.$router.push('/task/create')
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
.task-list-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 