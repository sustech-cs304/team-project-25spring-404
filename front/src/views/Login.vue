<template>
  <div class="login-container">
    <div class="login-box">
      <div class="title">
        <h3>训练任务管理系统</h3>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
        <div class="tips">
          <span @click="goToRegister">还没有账号？点击注册</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.post('/user/login', this.loginForm).then(res => {
            if (res.data.code === 200) {
              this.$message.success('登录成功')
              // 存储用户信息
              this.$store.commit('setUserInfo', res.data.data)
              // 跳转到首页
              this.$router.push('/dashboard')
            } else {
              this.$message.error(res.data.msg)
            }
          }).catch(() => {
            this.$message.error('登录失败，请稍后重试')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    goToRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100%;
  background-color: #f0f2f5;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
}

.login-btn {
  width: 100%;
}

.tips {
  margin-top: 15px;
  text-align: right;
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
}
</style> 