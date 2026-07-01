<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

const flightQuery = reactive({ keyword: '', status: '', startDate: '', endDate: '', pageNum: 1, pageSize: 15 })
const flightData = ref([])
const flightLoading = ref(false)
const flightTotal = ref(0)

const statusOptions = [
  { label: '全部', value: '' }, { label: '计划中', value: 'PLAN' }, { label: '已起飞', value: 'DEP' },
  { label: '已到达', value: 'ARR' }, { label: '延误', value: 'DLY' }, { label: '取消', value: 'CAN' }, { label: '返航', value: 'RTN' }
]
function statusTag(code) {
  const map = { PLAN:'info', DEP:'success', ARR:'success', DLY:'danger', CAN:'info', RTN:'danger' }
  return map[code] || ''
}

// ==================== 目的地映射 ====================
const DEST_MAP = {
  HND:'东京羽田', PVG:'上海浦东', SZX:'深圳', TSN:'天津', HRB:'哈尔滨', ZUH:'珠海', XMN:'厦门',
  CTU:'成都', XIY:'西安', CAN:'广州', HGH:'杭州', CSX:'长沙', PEK:'北京首都', SHE:'沈阳',
  DLC:'大连', KMG:'昆明', NGB:'宁波', CKG:'重庆', WUH:'武汉', NKG:'南京', TNA:'济南',
  HAK:'海口', SYX:'三亚', NNG:'南宁', KWL:'桂林', FOC:'福州', KHN:'南昌', WEH:'威海',
  YNT:'烟台', XUZ:'徐州', WNZ:'温州', HSN:'舟山', JJN:'泉州', SWA:'汕头', ZHA:'湛江',
  BHY:'北海', LZH:'柳州', KWE:'贵阳', LJG:'丽江', DIG:'香格里拉', HET:'呼和浩特',
  SHP:'秦皇岛', SHA:'上海虹桥', TXN:'黄山', XNN:'西宁', DAT:'大同', NDG:'齐齐哈尔',
  KHI:'卡拉奇', HLP:'香港'
}
function destName(code, fb) { return DEST_MAP[code] || fb || code || '-'; }
function buildFlightNo(row) {
  const code = row.airline || ''; const no = row.flightNo || '';
  let datePart = '';
  if (row.planDepartFull) { datePart = row.planDepartFull.replace(/\D/g,'').substring(0,8); }
  if (code && no && datePart) return `${code}-${no}-${datePart}-D`;
  return `${code}${no}` || no || '-';
}

async function fetchFlights() {
  flightLoading.value = true
  try {
    const res = await axios.get('/api/flights/all', { params: flightQuery })
    if (res.data.code === 1) { flightData.value = res.data.data.records; flightTotal.value = res.data.data.total }
  } catch (e) { console.error(e) }
  finally { flightLoading.value = false }
}
function handleSizeChange(s) { flightQuery.pageSize = s; flightQuery.pageNum = 1; fetchFlights() }
function handleCurrentChange(p) { flightQuery.pageNum = p; fetchFlights() }
function handleSearch() { flightQuery.pageNum = 1; fetchFlights() }
onMounted(() => fetchFlights())
</script>

<template>
  <div class="flights-page">
    <section class="deck">
      <div class="deck-head"><h2>&#9992; 全部航班信息</h2></div>
      <div class="filter-bar">
        <el-input v-model="flightQuery.keyword" placeholder="航班号 / 目的地" clearable class="fld" @keyup.enter="handleSearch" />
        <el-select v-model="flightQuery.status" placeholder="状态" clearable class="fld fld-s">
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-date-picker v-model="flightQuery.startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD" class="fld fld-d" />
        <el-date-picker v-model="flightQuery.endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD" class="fld fld-d" />
        <el-button class="btn-go" @click="handleSearch">查询</el-button>
      </div>

      <el-table :data="flightData" v-loading="flightLoading" class="flight-table" empty-text="暂无航班数据" stripe border style="width:100%">
        <el-table-column label="起飞时间" min-width="90">
          <template #default="{ row }">{{ row.planDepartTime }}</template>
        </el-table-column>
        <el-table-column label="航班号" min-width="170">
          <template #default="{ row }">{{ buildFlightNo(row) }}</template>
        </el-table-column>
        <el-table-column prop="airlineName" label="航空公司" min-width="140" />
        <el-table-column label="目的地" min-width="110">
          <template #default="{ row }">{{ destName(row.destinationCode, row.destination) }}</template>
        </el-table-column>
        <el-table-column label="值机柜台" min-width="100">
          <template #default="{ row }">{{ row.checkCounter || '-' }}</template>
        </el-table-column>
        <el-table-column label="登机口" min-width="90">
          <template #default="{ row }">{{ row.gate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.statusCode)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-foot">
        <el-pagination v-model:current-page="flightQuery.pageNum" v-model:page-size="flightQuery.pageSize" :total="flightTotal" :page-sizes="[10,15,30,50]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" small />
      </div>
    </section>
  </div>
</template>

<style scoped>
.flights-page { display: flex; flex-direction: column; gap: 24px; }
.deck { background: #fff; border: 1px solid #e5e9f0; border-radius: 14px; padding: 26px 28px; box-shadow: 0 1px 4px rgba(0,0,0,.04); }
.deck-head { margin-bottom: 18px; }
.deck-head h2 { font-size: 18px; font-weight: 700; color: #162b42; }
.filter-bar { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; margin-bottom: 16px; }
.fld { width: 180px; } .fld-s { width: 110px; } .fld-d { width: 145px; }
.btn-go { background: #e85d3a; color: #fff; border: none; font-weight: 600; }
.btn-go:hover { background: #c94a2a; color: #fff; }
.flight-table { width: 100% !important; }
.table-foot { margin-top: 14px; display: flex; justify-content: flex-end; }
@media (max-width: 560px) { .filter-bar { flex-direction: column; } .fld { width: 100%; } }
</style>
