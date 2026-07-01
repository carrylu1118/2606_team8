<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

// ==================== 目的地三字码 → 中文映射 ====================
const DEST_MAP = {
  HND:'东京羽田', PVG:'上海浦东', SZX:'深圳', TSN:'天津', HRB:'哈尔滨', ZUH:'珠海', XMN:'厦门',
  CTU:'成都', XIY:'西安', CAN:'广州', HGH:'杭州', CSX:'长沙', PEK:'北京首都', SHE:'沈阳',
  DLC:'大连', KMG:'昆明', NGB:'宁波', CKG:'重庆', WUH:'武汉', NKG:'南京', TNA:'济南',
  SIA:'新加坡', ICN:'首尔仁川', NRT:'东京成田', KIX:'大阪关西', BKK:'曼谷', SIN:'新加坡',
  LXA:'拉萨', URC:'乌鲁木齐', HAK:'海口', SYX:'三亚', NNG:'南宁', KWL:'桂林',
  FOC:'福州', KHN:'南昌', WEH:'威海', YNT:'烟台', XUZ:'徐州', WNZ:'温州', HSN:'舟山',
  JJN:'泉州', SWA:'汕头', ZHA:'湛江', BHY:'北海', LZH:'柳州', KWE:'贵阳', LJG:'丽江', DIG:'香格里拉',
  HET:'呼和浩特', SHP:'秦皇岛', SHA:'上海虹桥', TXN:'黄山', XNN:'西宁', DAT:'大同', NDG:'齐齐哈尔',
  KHI:'卡拉奇', HLP:'香港'
}
function destName(code, fallback) { return DEST_MAP[code] || fallback || code || '-'; }

// ==================== 航班号复合格式构造 ====================
// airline(二字码) + flightNo(原始) → 前端尝试拼成 CA-1319-20170601-D
function buildFullFlightNo(row) {
  const code = row.airline || '';
  const no = row.flightNo || '';
  // 从 planDepartFull 提取日期: "2017-06-01 08:00:00" → "20170601"
  let datePart = '';
  if (row.planDepartFull) {
    const d = row.planDepartFull.replace(/\D/g, ''); // 去掉非数字
    datePart = d.substring(0, 8); // "20170601"
  }
  // 默认为出港 D
  const dir = 'D';
  if (code && no && datePart) {
    return `${code}-${no}-${datePart}-${dir}`;
  }
  return `${code}${no}` || no || '-';
}

// ==================== 状态标签 ====================
function statusTagClass(code) {
  const map = { PLAN:'info', DEP:'success', ARR:'success', DLY:'danger', CAN:'info', RTN:'danger' };
  return map[code] || '';
}

const promos = ref([
  { route: '海口-中国香港', date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '1,398', from: 'HAK', to: 'HKG', img: 'https://th.bing.com/th/id/R.fb64295dd9877ba77fb94e9aa76d2e9b?rik=fOF0Q6Xf0XHimw&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50026%2f0831.jpg_wh1200.jpg&ehk=OJhUc5bRaLVgmJDlyjcVmcEW%2bRBndsrwwmB3q75kwvM%3d&risl=&pid=ImgRaw&r=0' },
  { route: '北京-柏林',     date: '2024/06/01 - 2024/09/30', cabin: '经济舱单程', price: '3,173', from: 'PEK', to: 'BER', img: 'https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?w=400&q=80' },
  { route: '上海-布鲁塞尔', date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '4,029', from: 'PVG', to: 'BRU', img: 'https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?w=400&q=80' },
  { route: '深圳-巴黎',     date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '2,073', from: 'SZX', to: 'CDG', img: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400&q=80' },
  { route: '重庆-巴黎',     date: '2024/06/01 - 2024/09/30', cabin: '经济舱单程', price: '2,289', from: 'CKG', to: 'CDG', img: 'https://images.unsplash.com/photo-1473864803180-ca1b3d93c9a0?w=400&q=80' },
  { route: '海口-曼谷',     date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '5,541', from: 'HAK', to: 'BKK', img: 'https://images.unsplash.com/photo-1556388158-158ea5ccacbd?w=400&q=80' },
  { route: '北京-普吉',     date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '1,303', from: 'PEK', to: 'HKT', img: 'https://images.unsplash.com/photo-1542296332-2e4473faf563?w=400&q=80' },
  { route: '海口-中国香港', date: '2024/06/01 - 2024/09/30', cabin: '经济舱往返', price: '1,398', from: 'HAK', to: 'HKG', img: 'https://images.unsplash.com/photo-1530521954074-e64f6810b32d?w=400&q=80' }
])

const flightQuery = reactive({ keyword: '', status: '', startDate: '', endDate: '', pageNum: 1, pageSize: 10 })
const flightData = ref([])
const flightLoading = ref(false)
const flightTotal = ref(0)

const statusOptions = [
  { label: '全部', value: '' }, { label: '计划中', value: 'PLAN' }, { label: '已起飞', value: 'DEP' },
  { label: '已到达', value: 'ARR' }, { label: '延误', value: 'DLY' }, { label: '取消', value: 'CAN' }, { label: '返航', value: 'RTN' }
]
const statusTagType = { PLAN: '', DEP: 'success', ARR: '', DLY: 'warning', CAN: 'danger', RTN: 'danger' }

async function fetchFlights() {
  flightLoading.value = true
  try {
    const res = await axios.get('/api/flights/dynamic', { params: flightQuery })
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
  <div class="home">

    <!-- ========== HERO ========== -->
    <section class="hero">
      <div class="hero-content">
        <p class="hero-eyebrow">TIANJIN BINHAI INTERNATIONAL AIRPORT</p>
        <h1 class="hero-title">连接世界的<span class="hero-dot">每一站</span></h1>
        <p class="hero-sub">天津滨海机场，您的全球出行伙伴</p>
        <div class="hero-kpis">
          <div class="kpi"><strong>180+</strong><span>通航城市</span></div>
          <div class="kpi"><strong>320+</strong><span>日均航班</span></div>
          <div class="kpi"><strong class="kpi-nowrap">99.7%</strong><span>准点率</span></div>
        </div>
      </div>
      <div class="hero-visual">
        <img src="https://images.unsplash.com/photo-1540962351504-03099e0a754b?w=700&q=80" alt="" />
      </div>
    </section>

    <!-- ========== 促销航班 ========== -->
    <section class="deck">
      <div class="deck-head">
        <h2>&#9992; 特价促销航班</h2>
        <a href="#" class="deck-more">查看全部 &rarr;</a>
      </div>
      <div class="promo-layout">
        <!-- 左：大卡片 -->
        <div class="ticket-card ticket-card--hero" v-if="promos.length > 0">
          <div class="ticket-img">
            <img :src="promos[0].img" :alt="promos[0].route" loading="lazy" @error="e => e.target.style.display='none'" />
            <span class="ticket-code">{{ promos[0].from }}&rarr;{{ promos[0].to }}</span>
          </div>
          <div class="ticket-body">
            <div class="ticket-route">{{ promos[0].route }}</div>
            <div class="ticket-meta">{{ promos[0].date }}</div>
            <div class="ticket-meta">{{ promos[0].cabin }}</div>
            <div class="ticket-price"><span class="price-num">{{ promos[0].price }}</span> <span class="price-suf">CNY起</span></div>
          </div>
        </div>
        <!-- 右：6 张小卡片 2×3 -->
        <div class="card-grid">
          <div v-for="(p, i) in promos.slice(1, 7)" :key="i" class="ticket-card" :style="{ animationDelay: `${(i+1) * .06}s` }">
            <div class="ticket-img">
              <img :src="p.img" :alt="p.route" loading="lazy" @error="e => e.target.style.display='none'" />
              <span class="ticket-code">{{ p.from }}&rarr;{{ p.to }}</span>
            </div>
            <div class="ticket-body">
              <div class="ticket-route">{{ p.route }}</div>
              <div class="ticket-meta">{{ p.date }}</div>
              <div class="ticket-meta">{{ p.cabin }}</div>
              <div class="ticket-price"><span class="price-num">{{ p.price }}</span> <span class="price-suf">CNY起</span></div>
            </div>
          </div>
        </div>
      </div>
      <p class="deck-note">以上价格含税费，特价座位数量有限，售完即止。</p>
    </section>

    <!-- ========== 航班动态 ========== -->
    <section class="deck">
      <div class="deck-head"><h2>&#8986; 实时航班动态</h2></div>
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
          <template #default="{ row }">{{ buildFullFlightNo(row) }}</template>
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
            <el-tag :type="statusTagClass(row.statusCode)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-foot">
        <el-pagination v-model:current-page="flightQuery.pageNum" v-model:page-size="flightQuery.pageSize" :total="flightTotal" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" small />
      </div>
    </section>

  </div>
</template>

<style scoped>
/* ================================================================
   HOME — White + Navy + Coral
   ================================================================ */
.home { display: flex; flex-direction: column; gap: 28px; }

/* ===== Hero ===== */
.hero {
  display: flex; align-items: center; gap: 40px;
  background: linear-gradient(135deg, #f0f4fa 0%, #e8edf4 100%);
  border-radius: 18px; overflow: hidden; min-height: 260px;
}
.hero-content { flex: 1; padding: 36px 0 36px 36px; }
.hero-eyebrow { font-size: 10px; letter-spacing: 4px; color: #8a9bb5; margin-bottom: 8px; font-weight: 500; }
.hero-title { font-size: 38px; font-weight: 900; color: var(--navy); line-height: 1.15; }
.hero-dot { color: var(--coral); }
.hero-sub { color: var(--text-dim); font-size: 15px; margin-top: 8px; }
.hero-kpis { display: flex; gap: 32px; margin-top: 22px; }
.kpi strong { display: block; font-size: 24px; font-weight: 700; color: var(--navy); }
.kpi-nowrap { white-space: nowrap; }
.kpi span { display: block; font-size: 12px; color: var(--text-dim); margin-top: 1px; }
.hero-visual { flex: 0 0 360px; height: 260px; }
.hero-visual img { width: 100%; height: 100%; object-fit: cover; }

/* ===== Deck ===== */
.deck { background: var(--white); border: 1px solid var(--border); border-radius: 14px; padding: 26px 28px; box-shadow: 0 1px 4px rgba(0,0,0,.04); }
.deck-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.deck-head h2 { font-size: 18px; font-weight: 700; color: var(--navy); }
.deck-more { color: var(--coral); text-decoration: none; font-size: 13px; font-weight: 500; }
.deck-more:hover { text-decoration: underline; }

/* ===== Cards ===== */
.promo-layout { display: flex; gap: 16px; margin-bottom: 16px; }
.card-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; flex: 1; }
.ticket-card {
  background: var(--white); border: 1px solid var(--border); border-radius: 12px; overflow: hidden;
  transition: all .3s ease; animation: fadeUp .5s ease both;
}
@keyframes fadeUp { from { opacity: 0; transform: translateY(14px); } to { opacity: 1; transform: translateY(0); } }
.ticket-card:hover { transform: translateY(-5px); box-shadow: 0 10px 28px rgba(0,0,0,.1); border-color: #c0ccda; }

/* 大卡片 */
.ticket-card--hero { flex: 0 0 320px; }
.ticket-card--hero .ticket-img { height: 200px; }
.ticket-card--hero .ticket-route { font-size: 20px; }
.ticket-card--hero .ticket-body { padding: 20px 22px 22px; }
.ticket-card--hero .price-num { font-size: 32px; }

.ticket-img { position: relative; height: 100px; overflow: hidden; background: #e5e9f0; }
.ticket-img img { width: 100%; height: 100%; object-fit: cover; }
.ticket-code {
  position: absolute; top: 8px; right: 8px;
  background: rgba(22,43,66,.72); color: #fff; font-size: 11px;
  padding: 2px 10px; border-radius: 20px; font-weight: 500;
}
.ticket-body { padding: 12px 14px 14px; }
.ticket-route { font-size: 14px; font-weight: 700; color: var(--navy); margin-bottom: 3px; }
.ticket-meta  { font-size: 11px; color: var(--text-sub); margin-bottom: 1px; }
.ticket-price { margin-top: 6px; display: flex; align-items: baseline; gap: 4px; }
.price-num { font-size: 22px; font-weight: 700; color: var(--coral); }
.price-suf { font-size: 11px; color: var(--coral); opacity: .7; }
.deck-note { font-size: 12px; color: var(--text-sub); }

/* ===== Filter ===== */
.filter-bar { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; margin-bottom: 16px; }
.fld { width: 180px; } .fld-s { width: 110px; } .fld-d { width: 145px; }
.btn-go { background: var(--coral); color: #fff; border: none; font-weight: 600; }
.btn-go:hover { background: var(--coral-dk); color: #fff; }

/* ===== Table ===== */
.flight-table { width: 100% !important; }
.flight-table :deep(.el-table__header) { width: 100% !important; }
.flight-table :deep(.el-table__body)   { width: 100% !important; }
.table-foot { margin-top: 14px; display: flex; justify-content: flex-end; }

@media (max-width: 960px) {
  .promo-layout { flex-direction: column; }
  .ticket-card--hero { flex: 0 0 auto; }
  .ticket-card--hero .ticket-img { height: 160px; }
  .card-grid { grid-template-columns: repeat(2, 1fr); }
  .hero { flex-direction: column; }
  .hero-visual { flex: 0 0 200px; width: 100%; }
  .hero-content { padding: 24px; }
}
@media (max-width: 560px) {
  .card-grid { grid-template-columns: 1fr; }
  .filter-bar { flex-direction: column; }
  .fld { width: 100%; }
}
</style>
