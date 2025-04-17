<template>
  <div class="goal-list-container">
    <el-card>
      <div slot="header">
        <span>训练目标列表</span>
        <el-button style="float: right" type="primary" size="small" @click="goToCreate">新增目标</el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="请输入目标名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="请选择目标状态" clearable>
            <el-option label="进行中" :value="0"></el-option>
            <el-option label="已完成" :value="1"></el-option>
            <el-option label="已放弃" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 目标列表 -->
      <el-table :data="filteredGoalList" v-loading="loading" style="width: 100%">
        <el-table-column prop="goalName" label="目标名称"></el-table-column>
        <el-table-column prop="goalDesc" label="目标描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="进度" width="200">
          <template slot-scope="scope">
            <el-progress :percentage="calculateProgress(scope.row)" :format="percentageFormat"></el-progress>
            <div class="progress-detail">
              {{ scope.row.currentValue }} / {{ scope.row.targetValue }} {{ scope.row.unit }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="开始日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column label="截止日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.endDate) }}
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
      title="删除目标"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这个目标吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'GoalList',
  data() {
    return {
      // 目标列表
      goalList: [],
      // 搜索表单
      searchForm: {
        keyword: '',
        status: ''
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
      // 当前操作的目标
      currentGoal: null
    }
  },
  computed: {
    // 过滤后的目标列表
    filteredGoalList() {
      let result = this.goalList.filter(goal => {
        // 按关键字过滤
        if (this.searchForm.keyword && 
            !goal.goalName.toLowerCase().includes(this.searchForm.keyword.toLowerCase()) &&
            !goal.goalDesc.toLowerCase().includes(this.searchForm.keyword.toLowerCase())) {
          return false
        }
        // 按状态过滤
        if (this.searchForm.status !== '' && goal.status !== this.searchForm.status) {
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
      return this.goalList.filter(goal => {
        // 按关键字过滤
        if (this.searchForm.keyword && 
            !goal.goalName.toLowerCase().includes(this.searchForm.keyword.toLowerCase()) &&
            !goal.goalDesc.toLowerCase().includes(this.searchForm.keyword.toLowerCase())) {
          return false
        }
        // 按状态过滤
        if (this.searchForm.status !== '' && goal.status !== this.searchForm.status) {
          return false
        }
        return true
      }).length
    }
  },
  created() {
    this.getGoalList()
  },
  methods: {
    // 获取目标列表
    getGoalList() {
      this.loading = true
      this.$http.get('/goal/list').then(res => {
        if (res.data.code === 200) {
          this.goalList = res.data.data
          this.$store.commit('setGoalList', res.data.data)
        } else {
          this.$message.error(res.data.msg || '获取目标列表失败')
        }
      }).catch(() => {
        this.$message.error('获取目标列表失败')
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
        status: ''
      }
      this.handleSearch()
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '--'
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
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
    // 查看详情
    handleDetail(row) {
      this.$router.push(`/goal/detail/${row.id}`)
    },
    // 编辑目标
    handleEdit(row) {
      this.$router.push({
        path: '/goal/create',
        query: { id: row.id }
      })
    },
    // 删除目标
    handleDelete(row) {
      this.currentGoal = row
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      if (!this.currentGoal) return
      this.$http.delete(`/goal/${this.currentGoal.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.getGoalList()
        } else {
          this.$message.error(res.data.msg || '删除失败')
        }
      }).catch(() => {
        this.$message.error('删除失败')
      }).finally(() => {
        this.deleteDialogVisible = false
        this.currentGoal = null
      })
    },
    // 新增目标
    goToCreate() {
      this.$router.push('/goal/create')
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
.goal-list-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.progress-detail {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: center;
}
</style> 