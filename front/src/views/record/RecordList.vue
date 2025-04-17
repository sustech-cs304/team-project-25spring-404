<template>
  <div class="record-list-container">
    <el-card>
      <div slot="header">
        <span>运动记录列表</span>
        <el-button style="float: right" type="primary" size="small" @click="goToCreate">新增记录</el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-select v-model="searchForm.goalId" placeholder="选择训练目标" clearable>
            <el-option
              v-for="item in goalList"
              :key="item.id"
              :label="item.goalName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 记录列表 -->
      <el-table :data="filteredRecordList" v-loading="loading" style="width: 100%">
        <el-table-column prop="goalName" label="目标名称" min-width="120">
          <template slot-scope="scope">
            <el-link type="primary" @click="goToGoalDetail(scope.row.goalId)">{{ scope.row.goalName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="recordValue" label="记录值" width="120">
          <template slot-scope="scope">
            {{ scope.row.recordValue }} {{ scope.row.unit }}
          </template>
        </el-table-column>
        <el-table-column label="开始日期" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>
        <el-table-column label="记录日期" width="180">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.recordDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="recordRemark" label="备注" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="150" align="center">
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
      <div class="empty-data" v-if="recordList.length === 0 && !loading">
        <el-empty description="暂无运动记录"></el-empty>
      </div>
    </el-card>
    
    <!-- 编辑记录对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form :model="recordForm" :rules="rules" ref="recordForm" label-width="80px">
        <el-form-item label="目标" prop="goalId">
          <el-select v-model="recordForm.goalId" placeholder="选择训练目标" @change="handleGoalChange">
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
          <el-input type="textarea" v-model="recordForm.recordRemark" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitRecord" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>
    
    <!-- 删除确认对话框 -->
    <el-dialog
      title="删除记录"
      :visible.sync="deleteDialogVisible"
      width="30%">
      <span>确定要删除这条记录吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelete">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'RecordList',
  data() {
    return {
      // 运动记录列表
      recordList: [],
      // 目标列表
      goalList: [],
      // 搜索表单
      searchForm: {
        goalId: '',
        dateRange: []
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
      dialogTitle: '添加记录',
      submitting: false,
      recordForm: {
        id: null,
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
      },
      // 删除对话框
      deleteDialogVisible: false,
      currentRecord: null
    }
  },
  computed: {
    // 过滤后的记录列表
    filteredRecordList() {
      let result = this.recordList.filter(record => {
        // 按目标ID过滤
        if (this.searchForm.goalId && record.goalId !== this.searchForm.goalId) {
          return false
        }
        
        // 按日期范围过滤
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          const recordDate = new Date(record.recordDate).setHours(0, 0, 0, 0)
          const startDate = new Date(this.searchForm.dateRange[0]).setHours(0, 0, 0, 0)
          const endDate = new Date(this.searchForm.dateRange[1]).setHours(0, 0, 0, 0)
          
          if (recordDate < startDate || recordDate > endDate) {
            return false
          }
        }
        
        return true
      })
      
      // 按日期倒序排序
      result.sort((a, b) => new Date(b.recordDate) - new Date(a.recordDate))
      
      // 分页处理
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      return result.slice(start, end)
    },
    // 总记录数
    totalCount() {
      return this.recordList.filter(record => {
        // 按目标ID过滤
        if (this.searchForm.goalId && record.goalId !== this.searchForm.goalId) {
          return false
        }
        
        // 按日期范围过滤
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          const recordDate = new Date(record.recordDate).setHours(0, 0, 0, 0)
          const startDate = new Date(this.searchForm.dateRange[0]).setHours(0, 0, 0, 0)
          const endDate = new Date(this.searchForm.dateRange[1]).setHours(0, 0, 0, 0)
          
          if (recordDate < startDate || recordDate > endDate) {
            return false
          }
        }
        
        return true
      }).length
    }
  },
  created() {
    this.getGoalList()
    this.getRecordList()
  },
  methods: {
    // 获取目标列表
    getGoalList() {
      this.$http.get('/goal/list').then(res => {
        if (res.data.code === 200) {
          this.goalList = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '获取目标列表失败')
        }
      }).catch(() => {
        this.$message.error('获取目标列表失败')
      })
    },
    // 获取记录列表
    getRecordList() {
      this.loading = true
      this.$http.get('/goal/record/all').then(res => {
        if (res.data.code === 200) {
          this.recordList = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '获取记录列表失败')
        }
      }).catch(() => {
        this.$message.error('获取记录列表失败')
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
        goalId: '',
        dateRange: []
      }
      this.handleSearch()
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
    // 前往目标详情
    goToGoalDetail(goalId) {
      this.$router.push(`/goal/detail/${goalId}`)
    },
    // 新增记录
    goToCreate() {
      this.dialogTitle = '添加记录'
      this.recordForm = {
        id: null,
        goalId: '',
        goalName: '',
        startDate: this.formatDate(new Date()),
        recordValue: 0,
        recordDate: this.formatDateTime(new Date()),
        recordRemark: '',
        unit: ''
      }
      this.dialogVisible = true
    },
    // 编辑记录
    handleEdit(record) {
      this.dialogTitle = '编辑记录'
      this.recordForm = { ...record }
      this.dialogVisible = true
    },
    // 目标选择变化
    handleGoalChange(goalId) {
      const goal = this.goalList.find(g => g.id === goalId)
      if (goal) {
        this.recordForm.goalName = goal.goalName
        this.recordForm.unit = goal.unit
      }
    },
    // 提交记录
    submitRecord() {
      this.$refs.recordForm.validate(valid => {
        if (valid) {
          this.submitting = true
          
          // 确保开始日期不为空
          const formData = {
            ...this.recordForm,
            startDate: this.recordForm.startDate || this.formatDate(new Date())
          }
          
          const method = formData.id ? 'put' : 'post'
          const url = formData.id ? '/goal/record' : '/goal/record/add'
          
          this.$http[method](url, formData).then(res => {
            if (res.data.code === 200) {
              this.$message.success(formData.id ? '修改成功' : '添加成功')
              this.dialogVisible = false
              this.getRecordList()
            } else {
              this.$message.error(res.data.msg || (formData.id ? '修改失败' : '添加失败'))
            }
          }).catch(err => {
            console.error('请求错误:', err)
            this.$message.error(formData.id ? '修改失败' : '添加失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    // 删除记录
    handleDelete(record) {
      this.currentRecord = record
      this.deleteDialogVisible = true
    },
    // 确认删除
    confirmDelete() {
      if (!this.currentRecord) return
      
      this.$http.delete(`/goal/record/${this.currentRecord.id}`).then(res => {
        if (res.data.code === 200) {
          this.$message.success('删除成功')
          this.getRecordList()
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
.record-list-container {
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

.unit-text {
  margin-left: 10px;
  color: #606266;
}
</style> 