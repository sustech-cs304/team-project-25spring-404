<template>
  <div class="user-center-container">
    <el-card>
      <div slot="header">
        <span>个人中心</span>
      </div>
      
      <el-tabs v-model="activeName">
        <el-tab-pane label="个人信息" name="info">
          <el-form :model="userForm" :rules="rules" ref="userForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="userForm.nickname" placeholder="请输入昵称"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateUserInfo" :loading="loading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword" :loading="passwordLoading">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="账号安全" name="security">
          <div class="security-item">
            <div class="security-title">
              <span>账号密码</span>
              <el-tag type="success" size="mini">已设置</el-tag>
            </div>
            <div class="security-desc">用于保护账号信息和登录安全</div>
            <el-button type="text" @click="activeName = 'password'">修改密码</el-button>
          </div>
          <el-divider></el-divider>
          <div class="security-item">
            <div class="security-title">
              <span>绑定邮箱</span>
              <el-tag :type="userForm.email ? 'success' : 'danger'" size="mini">
                {{ userForm.email ? '已绑定' : '未绑定' }}
              </el-tag>
            </div>
            <div class="security-desc">用于接收训练提醒和重要通知</div>
            <el-button type="text" @click="activeName = 'info'">{{ userForm.email ? '修改邮箱' : '绑定邮箱' }}</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UserCenter',
  data() {
    // 校验两次输入的密码是否一致
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      activeName: 'info',
      userForm: {
        id: null,
        username: '',
        nickname: '',
        email: '',
        avatar: ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      loading: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      passwordLoading: false
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.getUserInfo()
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      if (this.userInfo) {
        this.userForm = { ...this.userInfo }
      } else {
        this.$store.dispatch('getUserInfo').then(data => {
          this.userForm = { ...data }
        }).catch(() => {
          this.$message.error('获取用户信息失败')
        })
      }
    },
    // 更新用户信息
    updateUserInfo() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.put('/user', this.userForm).then(res => {
            if (res.data.code === 200) {
              this.$message.success('修改成功')
              // 更新Vuex中的用户信息
              this.$store.commit('setUserInfo', this.userForm)
            } else {
              this.$message.error(res.data.msg || '修改失败')
            }
          }).catch(() => {
            this.$message.error('修改失败')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    // 修改密码
    updatePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.passwordLoading = true
          const params = {
            id: this.userForm.id,
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }
          this.$http.put('/user/password', params).then(res => {
            if (res.data.code === 200) {
              this.$message.success('密码修改成功，请重新登录')
              // 清除用户信息并退出登录
              this.$store.commit('clearUserInfo')
              this.$router.push('/login')
            } else {
              this.$message.error(res.data.msg || '密码修改失败')
            }
          }).catch(() => {
            this.$message.error('密码修改失败')
          }).finally(() => {
            this.passwordLoading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.user-center-container {
  padding: 20px;
}

.security-item {
  padding: 10px 0;
}

.security-title {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.security-title span {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
}

.security-desc {
  color: #999;
  font-size: 14px;
  margin-bottom: 8px;
}

.el-divider {
  margin: 15px 0;
}
</style> 