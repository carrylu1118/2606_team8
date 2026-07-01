<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: 'admin', password: 'admin123' })
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  try {
    const res = await axios.post('/api/auth/login', form.value)
    if (res.data.code === 1) {
      const { token, id, userName, name } = res.data.data
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify({ id, userName, name }))
      ElMessage.success('登录成功')
      router.push('/admin/dashboard')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  } finally { loading.value = false }
}
</script>

<template>
  <div class="admin-login">
    <div class="login-card">
      <div class="login-header">
        <span class="login-icon">&#9992;</span>
        <h2>管理控制台</h2>
        <p>天津滨海机场 · 航班管理后台</p>
      </div>
      <el-form :model="form" label-width="0" @keyup.enter="handleLogin">
        <el-form-item><el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="管理员密码" prefix-icon="Lock" show-password size="large" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="loading" @click="handleLogin" size="large" style="width:100%">登 录</el-button></el-form-item>
      </el-form>
      <div class="login-footer">
        <a @click="router.push('/user/')">← 返回首页</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-login {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #162b42 0%, #1f3d5c 100%);
}
.login-card {
  width: 400px; background: #fff; border-radius: 16px; padding: 40px 36px 32px;
  box-shadow: 0 20px 60px rgba(0,0,0,.3);
}
.login-header { text-align: center; margin-bottom: 28px; }
.login-icon { font-size: 40px; }
.login-header h2 { font-size: 22px; font-weight: 700; color: #162b42; margin: 8px 0 4px; }
.login-header p { font-size: 13px; color: #8a9bb5; }
.login-footer { text-align: center; margin-top: 16px; }
.login-footer a { color: #6b7c93; font-size: 13px; cursor: pointer; text-decoration: none; }
.login-footer a:hover { color: #e85d3a; }
</style>
