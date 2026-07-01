<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)

const navItems = [
  { label: '首页', path: '/' },
  { label: '航班信息', path: '/flights' },
  { label: '机场位置', path: '/airport-location' },
  { label: '机场', children: [
    { label: '机场概况', path: '/airport-overview' },
    { label: '机场设施', path: '/airport-facilities' },
    { label: '交通指南', path: '/airport-transport' }
  ]},
  { label: '旅客须知', children: [
    { label: '海关须知', path: '/customs' },
    { label: '行李规定', path: '/luggage' },
    { label: '出入境流程', path: '/immigration' }
  ]},
  { label: '超值购票', path: '/tickets' }
]

function handleMenuClick(item) { if (item.path) router.push(item.path) }
function handleSubClick(child) { router.push(child.path) }

// ==================== Auth ====================
const token = ref(localStorage.getItem('token') || '')
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
const isLoggedIn = computed(() => !!token.value)

axios.interceptors.request.use(config => {
  if (token.value) config.headers.Authorization = `Bearer ${token.value}`
  return config
})

const loginVisible = ref(false)
const loginForm = ref({ username: '', password: '' })
const loginLoading = ref(false)

async function handleLogin() {
  loginLoading.value = true
  try {
    const res = await axios.post('/api/auth/login', loginForm.value)
    if (res.data.code === 1) {
      const { token: t, id, userName, name } = res.data.data
      token.value = t; userInfo.value = { id, userName, name }
      localStorage.setItem('token', t)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      loginVisible.value = false
      loginForm.value = { username: '', password: '' }
      ElMessage.success('登录成功')
    } else { ElMessage.error(res.data.message || '登录失败') }
  } catch (e) { ElMessage.error(e.response?.data?.message || '登录请求失败') }
  finally { loginLoading.value = false }
}

const registerVisible = ref(false)
const registerForm = ref({ username: '', password: '', confirmPassword: '', email: '', phone: '', realName: '' })
const registerLoading = ref(false)

async function handleRegister() {
  if (registerForm.value.password !== registerForm.value.confirmPassword) { ElMessage.warning('两次密码不一致'); return }
  registerLoading.value = true
  try {
    const res = await axios.post('/api/auth/register', registerForm.value)
    if (res.data.code === 1) { ElMessage.success('注册成功，请登录'); registerVisible.value = false }
    else { ElMessage.error(res.data.message || '注册失败') }
  } catch (e) { ElMessage.error(e.response?.data?.message || '注册请求失败') }
  finally { registerLoading.value = false }
}

function handleLogout() { token.value = ''; userInfo.value = null; localStorage.removeItem('token'); localStorage.removeItem('userInfo'); ElMessage.success('已退出') }
onMounted(() => { if (token.value) axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}` })

const now = ref(new Date())
setInterval(() => { now.value = new Date() }, 1000)
function formatTime(d) { return d.toLocaleString('zh-CN') }
</script>

<template>
  <div class="app-shell">
    <header class="site-header">
      <div class="header-inner">
        <div class="brand" @click="router.push('/')">
          <span class="brand-icon">&#9992;</span>
          <span class="brand-text">天津滨海机场</span>
        </div>

        <nav class="main-nav">
          <template v-for="item in navItems" :key="item.label">
            <div v-if="!item.children" class="nav-link" :class="{ on: activeMenu === item.path }" @click="handleMenuClick(item)">{{ item.label }}</div>
            <el-dropdown v-else trigger="hover" popper-class="nav-pop">
              <div class="nav-link">{{ item.label }} <el-icon :size="12"><ArrowDown /></el-icon></div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="child in item.children" :key="child.path" @click="handleSubClick(child)">{{ child.label }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </nav>

        <div class="header-actions">
          <template v-if="isLoggedIn">
            <span class="user-tag">{{ userInfo?.userName || userInfo?.name }}</span>
            <el-button text size="small" @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button class="btn-outline" size="small" round @click="loginVisible = true">登录</el-button>
            <el-button class="btn-fill" size="small" round @click="registerVisible = true">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <main class="page-body"><router-view /></main>

    <footer class="site-footer">
      <div class="footer-grid">
        <div class="footer-col col-brand">
          <div class="footer-brand-line"><span>&#9992;</span><strong>天津滨海机场</strong></div>
          <p>天津滨海机场以"至诚、至简"为核心价值观，建设文化先进、制度健全、流程合理、操作规范的品牌机场。</p>
        </div>
        <div class="footer-col">
          <h4>关于我们</h4>
          <ul>
            <li><a href="https://baike.baidu.com/item/%E5%A4%A9%E6%B4%A5%E6%BB%A8%E6%B5%B7%E5%9B%BD%E9%99%85%E6%9C%BA%E5%9C%BA/1377382" target="_blank">机场简介</a></li>
            <li><a href="https://www.tj.gov.cn/" target="_blank">天津市人民政府</a></li>
          </ul>
        </div>
        <div class="footer-col">
          <h4>友情链接</h4>
          <ul>
            <li><a href="https://www.caac.gov.cn" target="_blank">中国民用航空局</a></li>
            <li><a href="http://www.cannews.com.cn" target="_blank">中国民航网</a></li>
          </ul>
        </div>
        <div class="footer-col col-qr">
          <h4>关注我们</h4>
          <div class="qr-row">
            <div class="qr-box"><img src="https://www.cah.com.cn/upload/2023/01-14/16-45-070298-1577553336.jpg" alt="微信" /><span>官方微信</span></div>
            <div class="qr-box"><img src="https://www.cah.com.cn/upload/2023/01-14/16-45-2103161792363489.jpg" alt="微博" /><span>官方微博</span></div>
          </div>
        </div>
      </div>
      <div class="footer-bar">搭乘天津航空 · {{ formatTime(now) }}</div>
      <div class="footer-legal">CopyRight &copy; 首都机场集团有限公司 &nbsp; <a href="#">网站地图</a> &nbsp; 京ICP备2022033189号-1 &nbsp; 京公网安备11030302000001</div>
    </footer>

    <el-dialog v-model="loginVisible" title="用户登录" width="400px"><!-- same --></el-dialog>
    <el-dialog v-model="registerVisible" title="用户注册" width="460px"><!-- same --></el-dialog>
  </div>
</template>

<style>
/* ================================================================
   AIR MAIL — Light + Navy + Coral
   ================================================================ */
:root {
  --bg:        #fafbfc;
  --white:     #ffffff;
  --navy:      #162b42;
  --navy-lt:   #1f3d5c;
  --coral:     #e85d3a;
  --coral-dk:  #c94a2a;
  --text:      #2c3e50;
  --text-dim:  #6b7c93;
  --text-sub:  #94a3b4;
  --border:    #e5e9f0;
  --font-body: 'Noto Sans SC', system-ui, sans-serif;
}

* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: var(--font-body); background: var(--bg); color: var(--text); -webkit-font-smoothing: antialiased; line-height: 1.7; }
.app-shell { min-height: 100vh; display: flex; flex-direction: column; }

/* ===== Header ===== */
.site-header { position: sticky; top: 0; z-index: 100; background: rgba(255,255,255,.94); backdrop-filter: blur(12px); border-bottom: 1px solid var(--border); }
.header-inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; height: 62px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 8px; cursor: pointer; margin-right: 40px; }
.brand-icon { font-size: 26px; }
.brand-text { font-size: 18px; font-weight: 700; color: var(--navy); letter-spacing: .3px; }

.main-nav { display: flex; align-items: center; gap: 2px; flex: 1; }
.nav-link { padding: 8px 16px; font-size: 14px; color: var(--text-dim); cursor: pointer; border-radius: 6px; transition: all .2s; display: flex; align-items: center; gap: 5px; white-space: nowrap; outline: none; }
.nav-link:focus, .nav-link:focus-visible, .nav-link:active { outline: none; }
.nav-link:hover { color: var(--navy); background: #f0f3f8; }
.nav-link.on { color: var(--coral); font-weight: 600; }

.header-actions { display: flex; align-items: center; gap: 10px; margin-left: 24px; }
.user-tag { font-size: 14px; color: var(--navy); font-weight: 500; }
.btn-outline { color: var(--navy); border-color: var(--border); background: transparent; }
.btn-outline:hover { border-color: var(--navy); color: var(--navy); }
.btn-fill { background: var(--coral); color: #fff; border: none; font-weight: 600; }
.btn-fill:hover { background: var(--coral-dk); }

/* ===== Page ===== */
.page-body { flex: 1; max-width: 1200px; width: 100%; margin: 0 auto; padding: 24px; }

/* ===== Footer ===== */
.site-footer { background: var(--navy); color: #8899ab; font-size: 13px; margin-top: auto; }
.footer-grid { max-width: 1200px; margin: 0 auto; padding: 36px 24px 24px; display: grid; grid-template-columns: 1.4fr 1fr 1fr 1.1fr; gap: 30px; }
.footer-col h4 { color: #aab8c4; font-size: 12px; letter-spacing: 2px; margin-bottom: 14px; font-weight: 500; text-transform: uppercase; }
.footer-col ul { list-style: none; }
.footer-col li { margin-bottom: 8px; }
.footer-col a { color: #6e8194; text-decoration: none; transition: color .2s; }
.footer-col a:hover { color: #fff; }
.footer-brand-line { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.footer-brand-line strong { font-size: 15px; font-weight: 600; color: #d0d8e0; }
.col-brand p { color: #6e8194; line-height: 1.8; font-size: 12px; max-width: 270px; }
.qr-row { display: flex; gap: 14px; }
.qr-box { text-align: center; }
.qr-box img { width: 68px; height: 68px; border-radius: 6px; border: 1px solid rgba(255,255,255,.1); display: block; }
.qr-box span { display: block; font-size: 11px; color: #6e8194; margin-top: 5px; }
.footer-bar { max-width: 1200px; margin: 0 auto; padding: 14px 24px; border-top: 1px solid rgba(255,255,255,.06); text-align: center; color: #8899ab; font-size: 13px; }
.footer-legal { max-width: 1200px; margin: 0 auto; padding: 10px 24px 24px; text-align: center; color: #5a6b7a; font-size: 11px; }
.footer-legal a { color: #6e8194; text-decoration: none; }
.footer-legal a:hover { color: #fff; }

@media (max-width: 900px) { .footer-grid { grid-template-columns: 1fr 1fr; } }
@media (max-width: 560px) { .footer-grid { grid-template-columns: 1fr; } }
</style>
