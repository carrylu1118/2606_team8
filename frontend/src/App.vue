<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

// ==================== 导航菜单 ====================
const navItems = [
  { label: '首页', path: '/' },
  { label: '航班信息', path: '/flights' },
  { label: '机场位置', path: '/airport-location' },
  {
    label: '机场',
    children: [
      { label: '机场概况', path: '/airport-overview' },
      { label: '机场设施', path: '/airport-facilities' },
      { label: '交通指南', path: '/airport-transport' }
    ]
  },
  {
    label: '旅客须知',
    children: [
      { label: '海关须知', path: '/customs' },
      { label: '行李规定', path: '/luggage' },
      { label: '出入境流程', path: '/immigration' }
    ]
  },
  { label: '超值购票', path: '/tickets' }
]

function handleMenuClick(item) {
  if (item.path) router.push(item.path)
}
function handleSubClick(child) {
  router.push(child.path)
}

// ==================== 登录状态 ====================
const token = ref(localStorage.getItem('token') || '')
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

const isLoggedIn = computed(() => !!token.value)

// 设置 axios 拦截器自动带 token
axios.interceptors.request.use(config => {
  if (token.value) {
    config.headers.Authorization = `Bearer ${token.value}`
  }
  return config
})

// ==================== 登录对话框 ====================
const loginVisible = ref(false)
const loginForm = ref({ username: '', password: '' })
const loginLoading = ref(false)

async function handleLogin() {
  loginLoading.value = true
  try {
    const res = await axios.post('/api/auth/login', loginForm.value)
    if (res.data.code === 1) {
      const { token: t, id, userName, name } = res.data.data
      token.value = t
      userInfo.value = { id, userName, name }
      localStorage.setItem('token', t)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      loginVisible.value = false
      loginForm.value = { username: '', password: '' }
      ElMessage.success('登录成功')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录请求失败')
  } finally {
    loginLoading.value = false
  }
}

// ==================== 注册对话框 ====================
const registerVisible = ref(false)
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  realName: ''
})
const registerLoading = ref(false)

async function handleRegister() {
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  registerLoading.value = true
  try {
    const res = await axios.post('/api/auth/register', registerForm.value)
    if (res.data.code === 1) {
      ElMessage.success('注册成功，请登录')
      registerVisible.value = false
      registerForm.value = { username: '', password: '', confirmPassword: '', email: '', phone: '', realName: '' }
    } else {
      ElMessage.error(res.data.message || '注册失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册请求失败')
  } finally {
    registerLoading.value = false
  }
}

// ==================== 退出登录 ====================
function handleLogout() {
  token.value = ''
  userInfo.value = null
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出')
}

// ==================== 初始化 ====================
onMounted(() => {
  if (token.value) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
  }
})
</script>

<template>
  <div class="app-container">
    <!-- ========== 顶部导航栏 ========== -->
    <header class="header">
      <div class="header-inner">
        <div class="logo" @click="router.push('/')">
          <el-icon :size="28"><Promotion /></el-icon>
          <span class="logo-text">天津航空</span>
        </div>

        <nav class="nav">
          <template v-for="item in navItems" :key="item.label">
            <div
              v-if="!item.children"
              class="nav-item"
              :class="{ active: activeMenu === item.path }"
              @click="handleMenuClick(item)"
            >
              {{ item.label }}
            </div>
            <el-dropdown v-else trigger="hover" popper-class="nav-dropdown">
              <div class="nav-item">
                {{ item.label }}
                <el-icon :size="12"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="child in item.children" :key="child.path" @click="handleSubClick(child)">
                    {{ child.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </nav>

        <div class="header-actions">
          <template v-if="isLoggedIn">
            <span class="user-name">{{ userInfo?.userName || userInfo?.name }}</span>
            <el-button size="small" round @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" size="small" round @click="loginVisible = true">登录</el-button>
            <el-button size="small" round @click="registerVisible = true">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- ========== 内容区域 ========== -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- ========== 底部 ========== -->
    <footer class="footer">
      <p>&copy; 2024 天津航空 Tianjin Airlines. All rights reserved.</p>
    </footer>

    <!-- ========== 登录对话框 ========== -->
    <el-dialog v-model="loginVisible" title="用户登录" width="400px" :close-on-click-modal="false">
      <el-form :model="loginForm" label-width="72px" @keyup.enter="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="loginVisible = false">取消</el-button>
        <el-button type="primary" :loading="loginLoading" @click="handleLogin">登录</el-button>
      </template>
    </el-dialog>

    <!-- ========== 注册对话框 ========== -->
    <el-dialog v-model="registerVisible" title="用户注册" width="460px" :close-on-click-modal="false">
      <el-form :model="registerForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style>
/* ====== Global ====== */
* { margin: 0; padding: 0; box-sizing: border-box; }
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f5f7fa;
  color: #333;
}
.app-container { min-height: 100vh; display: flex; flex-direction: column; }

/* ====== Header ====== */
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,.08);
  position: sticky; top: 0; z-index: 100;
}
.header-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; height: 60px; padding: 0 20px;
}
.logo {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer; color: #1a6fb5; margin-right: 40px;
}
.logo-text { font-size: 20px; font-weight: 700; color: #1a6fb5; white-space: nowrap; }

/* ====== Nav ====== */
.nav { display: flex; align-items: center; gap: 4px; flex: 1; }
.nav-item {
  padding: 8px 16px; font-size: 14px; color: #555;
  cursor: pointer; border-radius: 4px; transition: all .2s;
  display: flex; align-items: center; gap: 4px; white-space: nowrap;
}
.nav-item:hover { color: #1a6fb5; background: #eef6fc; }
.nav-item.active {
  color: #1a6fb5; font-weight: 600;
  border-bottom: 2px solid #1a6fb5; border-radius: 0; background: none;
}

/* ====== Header Actions ====== */
.header-actions { display: flex; align-items: center; gap: 8px; margin-left: 20px; }
.user-name { font-size: 14px; color: #1a6fb5; font-weight: 500; }

/* ====== Main ====== */
.main-content { flex: 1; max-width: 1200px; width: 100%; margin: 0 auto; padding: 24px 20px; }

/* ====== Footer ====== */
.footer { background: #2c3e50; color: #aab; text-align: center; padding: 20px; font-size: 13px; }
</style>
