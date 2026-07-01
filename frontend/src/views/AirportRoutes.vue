<script setup>
import { ref } from 'vue'

const activeTab = ref('domestic')

const tabs = [
  { key: 'domestic', label: '国内通航' },
  { key: 'international', label: '国际及地区通航' }
]
</script>

<template>
  <div class="routes-page">
    <div class="page-header">
      <h2>&#9992; 机场通航</h2>
      <p>天津滨海国际机场航线网络</p>
    </div>

    <div class="routes-layout">
      <aside class="routes-sidebar">
        <div
          v-for="tab in tabs" :key="tab.key"
          class="sidebar-item"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          <span class="dot" />
          {{ tab.label }}
        </div>
      </aside>

      <section class="routes-content">
        <!-- 国内通航 -->
        <div v-if="activeTab === 'domestic'" class="content-panel">
          <h3>国内通航</h3>
          <div class="img-wrap">
            <img
              src="https://www.tbia.cn/oss/bucket-tbia/dev/260325f1ab77dee6274394a89469180d66a803.jpg"
              alt="国内通航地图"
              @error="e => e.target.style.display='none'"
            />
          </div>
        </div>

        <!-- 国际及地区通航 -->
        <div v-if="activeTab === 'international'" class="content-panel">
          <h3>国际及地区通航</h3>
          <p class="season-tag">2026年夏秋航季</p>

          <p>天津滨海机场计划执行国际航线<span class="hl">13条</span>，执行地区航线<span class="hl">1条</span>。</p>

          <p>国际航线计划通航<span class="hl">8个国家的11个城市</span>，其中，国际客运航线通航<span class="hl">6个国家的9个城市</span>，分别为：</p>

          <div class="dest-grid">
            <div class="dest-item"><span class="flag">&#127462;&#127482;</span>澳大利亚 · 悉尼</div>
            <div class="dest-item"><span class="flag">&#127472;&#127479;</span>韩国 · 首尔</div>
            <div class="dest-item"><span class="flag">&#127472;&#127479;</span>韩国 · 济州</div>
            <div class="dest-item"><span class="flag">&#127471;&#127477;</span>日本 · 东京</div>
            <div class="dest-item"><span class="flag">&#127471;&#127477;</span>日本 · 大阪</div>
            <div class="dest-item"><span class="flag">&#127471;&#127477;</span>日本 · 名古屋</div>
            <div class="dest-item"><span class="flag">&#127468;&#127466;</span>新加坡</div>
            <div class="dest-item"><span class="flag">&#127468;&#127463;</span>英国 · 伦敦</div>
            <div class="dest-item"><span class="flag">&#127483;&#127475;</span>越南 · 芽庄</div>
          </div>

          <p class="region-line">地区计划通航 <span class="hl">香港</span></p>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.routes-page { display: flex; flex-direction: column; gap: 16px; }

.page-header {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 20px 28px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.page-header h2 { font-size: 18px; font-weight: 700; color: #162b42; }
.page-header p { font-size: 13px; color: #8a9bb5; margin-top: 4px; }

.routes-layout { display: flex; gap: 16px; flex: 1; }

/* Sidebar */
.routes-sidebar {
  flex: 0 0 200px; background: #fff; border: 1px solid #e5e9f0;
  border-radius: 14px; padding: 16px 0; box-shadow: 0 1px 4px rgba(0,0,0,.04);
  height: fit-content;
}
.sidebar-item {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 24px; font-size: 14px; color: #5a6b7c;
  cursor: pointer; transition: all .2s;
}
.sidebar-item:hover { color: #162b42; background: #f5f7fa; }
.sidebar-item.active { color: #e85d3a; font-weight: 600; background: #fef8f6; }
.dot {
  width: 6px; height: 6px; background: #c0ccda; border-radius: 50%; transition: all .2s;
}
.sidebar-item.active .dot { background: #e85d3a; }

/* Content */
.routes-content { flex: 1; }
.content-panel {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 28px 32px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
  min-height: 400px;
}
.content-panel h3 { font-size: 17px; font-weight: 700; color: #162b42; margin-bottom: 16px; }
.content-panel p { font-size: 14px; line-height: 2; color: #3a4a5c; }

.img-wrap { margin-top: 12px; }
.img-wrap img { width: 100%; border-radius: 8px; }

.season-tag {
  display: inline-block; background: #e85d3a; color: #fff;
  font-size: 11px; padding: 2px 12px; border-radius: 20px; font-weight: 600;
  margin-bottom: 16px;
}
.hl { color: #e85d3a; font-weight: 700; }

.dest-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px;
  margin: 16px 0;
}
.dest-item {
  background: #f8f9fc; border-radius: 8px; padding: 10px 14px;
  font-size: 13px; color: #162b42; font-weight: 500;
}
.flag { margin-right: 4px; }
.region-line { margin-top: 12px; }

@media (max-width: 720px) {
  .routes-layout { flex-direction: column; }
  .routes-sidebar { flex: none; display: flex; padding: 0; gap: 0; }
  .sidebar-item { flex: 1; justify-content: center; padding: 12px 0; }
  .dest-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
