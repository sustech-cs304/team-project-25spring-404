<template>
  <el-container class="home-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <el-menu
        :default-active="activePath"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        router
        unique-opened
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-s-order"></i>
            <span>训练任务</span>
          </template>
          <el-menu-item index="/task/list">
            <i class="el-icon-menu"></i>
            <span slot="title">任务列表</span>
          </el-menu-item>
          <el-menu-item index="/task/create">
            <i class="el-icon-plus"></i>
            <span slot="title">新增任务</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-s-flag"></i>
            <span>训练目标</span>
          </template>
          <el-menu-item index="/goal/list">
            <i class="el-icon-menu"></i>
            <span slot="title">目标列表</span>
          </el-menu-item>
          <el-menu-item index="/goal/create">
            <i class="el-icon-plus"></i>
            <span slot="title">新增目标</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="3">
          <template slot="title">
            <i class="el-icon-s-data"></i>
            <span>训练记录</span>
          </template>
          <el-menu-item index="/record/list">
            <i class="el-icon-menu"></i>
            <span slot="title">记录列表</span>
          </el-menu-item>
          <el-menu-item index="/record/create">
            <i class="el-icon-plus"></i>
            <span slot="title">新增记录</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="4">
          <template slot="title">
            <i class="el-icon-alarm-clock"></i>
            <span>提醒设置</span>
          </template>
          <el-menu-item index="/reminder/list">
            <i class="el-icon-menu"></i>
            <span slot="title">提醒列表</span>
          </el-menu-item>
          <el-menu-item index="/reminder/create">
            <i class="el-icon-plus"></i>
            <span slot="title">新增提醒</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/user/center">
          <i class="el-icon-user"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 右侧内容 -->
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-left">
          <span>训练任务管理系统</span>
        </div>
        <div class="header-right">
          <span class="user-info">
            <img class="avatar" :src="userAvatar" />
            <span class="username">{{ userInfo ? userInfo.nickname || userInfo.username : '未登录' }}</span>
          </span>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <i class="el-icon-setting"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userInfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <!-- 主要内容 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Home',
  data() {
    return {
      activePath: '/dashboard',
      defaultAvatar: require('@/assets/default.png')
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    userAvatar() {
      return this.userInfo && this.userInfo.avatar ? this.userInfo.avatar : this.defaultAvatar
    }
  },
  created() {
    // 获取当前路由路径
    this.activePath = this.$route.path
    // 获取用户信息
    this.getUserInfo()
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      this.$store.dispatch('getUserInfo').catch(() => {
        this.$message.error('获取用户信息失败，请重新登录')
        this.$router.push('/login')
      })
    },
    // 下拉菜单命令处理
    handleCommand(command) {
      if (command === 'userInfo') {
        this.$router.push('/user/center')
      } else if (command === 'logout') {
        this.logout()
      }
    },
    // 退出登录
    logout() {
      this.$http.get('/user/logout').then(res => {
        if (res.data.code === 200) {
          // 清除用户信息
          this.$store.commit('clearUserInfo')
          // 跳转到登录页
          this.$router.push('/login')
          this.$message.success('退出成功')
        }
      })
    }
  }
}
</script>

<style scoped>
.home-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.username {
  font-size: 14px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-main {
  background-color: #f0f2f5;
  color: #333;
  padding: 20px;
}
</style> 