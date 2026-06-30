<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

// ==================== 促销航班数据 ====================
const promos = ref([
  { route: '海口-中国香港', date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY1398起' },
  { route: '北京-柏林',       date: '2024/06/01-2024/09/30', cabin: '经济舱单程', price: 'CNY3173起' },
  { route: '上海-布鲁塞尔',   date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY4029起' },
  { route: '深圳-巴黎',       date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY2073起' },
  { route: '重庆-巴黎',       date: '2024/06/01-2024/09/30', cabin: '经济舱单程', price: 'CNY2289起' },
  { route: '海口-曼谷',       date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY5541起' },
  { route: '北京-普吉',       date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY1303起' },
  { route: '海口-中国香港',   date: '2024/06/01-2024/09/30', cabin: '经济舱往返', price: 'CNY1398起' }
])

// ==================== 航班动态表格 ====================
const flightQuery = reactive({
  keyword: '',
  status: '',
  startDate: '',
  endDate: '',
  page: 1,
  pageSize: 10
})

const flightData = ref([])
const flightLoading = ref(false)
const flightTotal = ref(0)

const statusOptions = [
  { label: '全部', value: '' },
  { label: '计划中', value: 'PLAN' },
  { label: '已起飞', value: 'DEP' },
  { label: '已到达', value: 'ARR' },
  { label: '延误', value: 'DLY' },
  { label: '取消', value: 'CAN' },
  { label: '返航', value: 'RTN' }
]

const statusTagType = {
  PLAN: '',
  DEP: 'success',
  ARR: '',
  DLY: 'warning',
  CAN: 'danger',
  RTN: 'danger'
}

function formatDateTime(dt) {
  if (!dt) return '-'
  return dt.replace('T', ' ').substring(0, 16)
}

async function fetchFlights() {
  flightLoading.value = true
  try {
    const res = await axios.post('/api/flights/page', flightQuery)
    if (res.data.code === 1) {
      flightData.value = res.data.data.records
      flightTotal.value = res.data.data.total
    }
  } catch (e) {
    console.error('获取航班数据失败', e)
  } finally {
    flightLoading.value = false
  }
}

function handleSizeChange(size) {
  flightQuery.pageSize = size
  flightQuery.page = 1
  fetchFlights()
}

function handleCurrentChange(page) {
  flightQuery.page = page
  fetchFlights()
}

function handleSearch() {
  flightQuery.page = 1
  fetchFlights()
}

onMounted(() => {
  fetchFlights()
})

// ==================== 动态时间 ====================
const now = ref(new Date())
setInterval(() => { now.value = new Date() }, 1000)
</script>

<template>
  <div class="home-page">
    <!-- ========== 促销航班卡片 ========== -->
    <section class="section">
      <h2 class="section-title">
        <el-icon><Promotion /></el-icon>
        特价促销航班
      </h2>
      <div class="promo-grid">
        <div
          v-for="(p, i) in promos"
          :key="i"
          class="promo-card"
        >
          <div class="card-route">{{ p.route }}</div>
          <div class="card-date">{{ p.date }}</div>
          <div class="card-cabin">{{ p.cabin }}</div>
          <div class="card-price">{{ p.price }}</div>
        </div>
      </div>
      <p class="promo-note">
        以上价格含税费。特价座位数量有限，售完则自动搜索更高价格。
      </p>
      <el-button type="primary" link size="large">查看更多 →</el-button>
    </section>

    <!-- ========== 航班动态表格 ========== -->
    <section class="section">
      <h2 class="section-title">
        <el-icon><Connection /></el-icon>
        实时航班动态
      </h2>

      <!-- 筛选栏 -->
      <div class="flight-filters">
        <el-input
          v-model="flightQuery.keyword"
          placeholder="航班号 / 目的地"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="flightQuery.status" placeholder="状态筛选" clearable style="width: 140px">
          <el-option
            v-for="opt in statusOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
        <el-date-picker
          v-model="flightQuery.startDate"
          type="date"
          placeholder="开始日期"
          value-format="YYYY-MM-DD"
          style="width: 160px"
        />
        <el-date-picker
          v-model="flightQuery.endDate"
          type="date"
          placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 160px"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </div>

      <!-- 表格 -->
      <el-table
        :data="flightData"
        v-loading="flightLoading"
        stripe
        style="width: 100%"
        empty-text="暂无航班数据"
      >
        <el-table-column prop="flightNo" label="航班号" width="110" />
        <el-table-column prop="airline" label="航空公司" width="90" />
        <el-table-column prop="departure" label="出发地" width="100" />
        <el-table-column prop="destination" label="目的地" width="130" />
        <el-table-column label="计划离港" width="140">
          <template #default="{ row }">{{ formatDateTime(row.planDepartTime) }}</template>
        </el-table-column>
        <el-table-column label="计划到港" width="140">
          <template #default="{ row }">{{ formatDateTime(row.planArriveTime) }}</template>
        </el-table-column>
        <el-table-column label="实际离港" width="140">
          <template #default="{ row }">{{ formatDateTime(row.actualDepartTime) }}</template>
        </el-table-column>
        <el-table-column label="实际到港" width="140">
          <template #default="{ row }">{{ formatDateTime(row.actualArriveTime) }}</template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.statusCode] || ''">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="flight-pagination">
        <el-pagination
          v-model:current-page="flightQuery.page"
          v-model:page-size="flightQuery.pageSize"
          :total="flightTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </section>

    <!-- ========== 底部提示 ========== -->
    <section class="section-bottom">
      <p class="info-line">
        搭乘天津航空 · {{ now.toLocaleString('zh-CN') }}
      </p>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  color: #333;
}

/* ====== Section ====== */
.section {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a6fb5;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #eef6fc;
}

/* ====== Promo Grid ====== */
.promo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.promo-card {
  background: linear-gradient(135deg, #f0f7ff 0%, #fff 100%);
  border: 1px solid #e8eef4;
  border-radius: 10px;
  padding: 20px 18px;
  text-align: center;
  transition: all .3s ease;
  cursor: pointer;
}

.promo-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(26, 111, 181, 0.15);
  border-color: #1a6fb5;
}

.card-route {
  font-size: 18px;
  font-weight: 700;
  color: #1a6fb5;
  margin-bottom: 10px;
}

.card-date {
  font-size: 13px;
  color: #888;
  margin-bottom: 6px;
}

.card-cabin {
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
}

.card-price {
  font-size: 22px;
  font-weight: 700;
  color: #e74c3c;
}

.promo-note {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

/* ====== Flight Filters ====== */
.flight-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  align-items: center;
}

.flight-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* ====== Bottom ====== */
.section-bottom {
  text-align: center;
  padding: 16px;
}

.info-line {
  font-size: 15px;
  color: #1a6fb5;
  font-weight: 500;
}

/* ====== Responsive ====== */
@media (max-width: 960px) {
  .promo-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .promo-grid {
    grid-template-columns: 1fr;
  }
  .flight-filters {
    flex-direction: column;
  }
}
</style>
