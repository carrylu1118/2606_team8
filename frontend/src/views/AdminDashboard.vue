<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// Mock 统计数据
const stats = reactive({
  total: 5006,
  today: 461,
  delayed: 219
})

// Mock 航班列表
const mockFlights = [
  { id:1, flightNo:'CA-1319-20170601-D', destination:'北京首都', planDepart:'2017-06-01 08:00', statusCode:'DEP',statusName:'已起飞' },
  { id:2, flightNo:'CZ-3210-20170601-D', destination:'广州', planDepart:'2017-06-01 08:30', statusCode:'PLAN',statusName:'计划中' },
  { id:3, flightNo:'MU-5103-20170601-D', destination:'上海浦东', planDepart:'2017-06-01 09:00', statusCode:'DLY',statusName:'延误' },
  { id:4, flightNo:'HU-7160-20170601-D', destination:'海口', planDepart:'2017-06-01 09:30', statusCode:'DEP',statusName:'已起飞' },
  { id:5, flightNo:'3U-8349-20170601-D', destination:'成都', planDepart:'2017-06-01 10:00', statusCode:'CAN',statusName:'取消' },
  { id:6, flightNo:'ZH-9421-20170601-D', destination:'深圳', planDepart:'2017-06-01 10:30', statusCode:'PLAN',statusName:'计划中' },
  { id:7, flightNo:'GS-7567-20170601-D', destination:'西安', planDepart:'2017-06-01 11:00', statusCode:'ARR',statusName:'已到达' },
  { id:8, flightNo:'MF-8123-20170601-D', destination:'厦门', planDepart:'2017-06-01 11:30', statusCode:'DEP',statusName:'已起飞' }
]

const flights = ref([...mockFlights])
const searchKeyword = ref('')
const filteredFlights = computed(() => {
  if (!searchKeyword.value) return flights.value
  const kw = searchKeyword.value.toLowerCase()
  return flights.value.filter(f => f.flightNo.toLowerCase().includes(kw) || f.destination.includes(kw))
})

function statusTag(code) {
  const map = { PLAN:'info', DEP:'success', ARR:'success', DLY:'danger', CAN:'info' }
  return map[code] || ''
}

function handleDelete(row) {
  flights.value = flights.value.filter(f => f.id !== row.id)
}

function handleEdit(row) {
  // TODO: 编辑弹窗
  console.log('编辑', row)
}

function handleAdd() {
  // TODO: 新增弹窗
  console.log('新增航班')
}

function backToUser() {
  router.push('/user/')
}

// 检查是否已登录
onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/admin/login')
  }
})
</script>

<template>
  <div class="dashboard">
    <div class="dash-header">
      <h2>&#9992; 管理控制台</h2>
      <div style="display:flex;gap:12px">
        <el-button @click="backToUser" size="small">← 返回首页</el-button>
        <el-button type="danger" size="small" @click="router.push('/user/'); localStorage.removeItem('token')">退出</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card"><strong>{{ stats.total }}</strong><span>总航班数</span></div>
      <div class="stat-card accent"><strong>{{ stats.today }}</strong><span>今日航班</span></div>
      <div class="stat-card warn"><strong>{{ stats.delayed }}</strong><span>延误航班</span></div>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索航班号或目的地" clearable style="width:280px" />
      <el-button type="primary" @click="handleAdd">+ 新增航班</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="filteredFlights" stripe border style="width:100%" empty-text="暂无数据">
      <el-table-column prop="flightNo" label="航班号" min-width="180" />
      <el-table-column prop="destination" label="目的地" min-width="120" />
      <el-table-column prop="planDepart" label="起飞时间" min-width="150" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.statusCode)" size="small">{{ row.statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.dashboard { max-width: 1200px; margin: 0 auto; padding: 24px 20px; display: flex; flex-direction: column; gap: 20px; }
.dash-header { display: flex; justify-content: space-between; align-items: center; background: #fff; border-radius: 14px; padding: 20px 28px; border: 1px solid #e5e9f0; }
.dash-header h2 { font-size: 20px; font-weight: 700; color: #162b42; }

.stat-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.stat-card {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 24px 28px; text-align: center;
}
.stat-card strong { display: block; font-size: 36px; font-weight: 800; color: #162b42; }
.stat-card span { font-size: 13px; color: #8a9bb5; margin-top: 4px; }
.stat-card.accent strong { color: #e85d3a; }
.stat-card.warn strong { color: #ff9800; }

.toolbar { display: flex; justify-content: space-between; align-items: center; background: #fff; border-radius: 14px; padding: 16px 28px; border: 1px solid #e5e9f0; }

@media (max-width: 640px) { .stat-cards { grid-template-columns: 1fr; } .toolbar { flex-direction: column; gap: 12px; } }
</style>
