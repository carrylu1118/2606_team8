<script setup>
</script>

<template>
  <div class="overview-page">
    <div class="hero-card">
      <div class="hero-badge">IATA: TSN &nbsp;·&nbsp; ICAO: ZBTJ</div>
      <h1 class="hero-name">天津滨海国际机场</h1>
      <p class="hero-en">Tianjin Binhai International Airport</p>
      <div class="hero-line"></div>
    </div>

    <div class="content-card">
      <p class="lead">天津滨海国际机场位于天津东丽区，距天津市中心<span class="num">13</span>公里，距天津港<span class="num">30</span>公里，距北京<span class="num">134</span>公里，是国内干线机场、国际定期航班机场、国家一类航空口岸，中国主要的航空货运中心之一。</p>

      <p>地理位置优越，具有较强的铁路、高速公路、轨道交通等综合交通优势，基础设施完善，市政能源配套齐全。天津滨海国际机场代理国内外客货运包机业务，并提供一条龙服务。同时为各航空公司提供地面代理业务。机场基地航空公司有中国国际航空公司天津分公司、天津航空有限责任公司、奥凯航空公司、厦门航空公司、天津货运航空有限公司。</p>

      <p>天津机场主营业务包括：飞机起降服务、航空地面服务、广告业务、房屋场地租赁、停车场服务、省际包车客运、市内包车客运等。</p>

      <div class="stat-row">
        <div class="stat-item">
          <strong>2</strong><span>条跑道</span>
        </div>
        <div class="stat-item">
          <strong>3600m</strong><span>第一跑道</span>
        </div>
        <div class="stat-item">
          <strong>3200m</strong><span>第二跑道</span>
        </div>
        <div class="stat-item">
          <strong>4E</strong><span>飞行区等级</span>
        </div>
        <div class="stat-item">
          <strong>36.4<span class="unit">万m²</span></strong><span>航站楼面积</span>
        </div>
      </div>

      <p>航站楼面积达<span class="num">36.4</span>万平方米，货库<span class="num">5.66</span>万平方米，具有管制二次雷达、卫星通信终端、机场数据传输网络等先进的导航通信设备及完善的地面保障设施。</p>

      <blockquote>
        <p>作为首都机场集团成员企业，天津滨海机场将以"至诚、至简"的核心价值观，守正创新、稳健务实、简约高效、自强不息，推动企业愿景的实现、企业使命的达成，建设成为文化先进、制度健全、流程合理、操作规范的品牌机场。</p>
      </blockquote>
    </div>
  </div>
</template>

<style scoped>
.overview-page { max-width: 860px; margin: 0 auto; display: flex; flex-direction: column; gap: 24px; }

.hero-card {
  background: linear-gradient(135deg, #162b42 0%, #1f3d5c 100%);
  border-radius: 18px; padding: 40px 44px; color: #fff;
}
.hero-badge {
  font-size: 12px; letter-spacing: 2px; color: rgba(255,255,255,.55);
  margin-bottom: 12px; font-family: 'Outfit', monospace;
}
.hero-name { font-size: 36px; font-weight: 900; margin-bottom: 6px; letter-spacing: 1px; }
.hero-en { font-size: 16px; color: rgba(255,255,255,.5); font-style: italic; }
.hero-line { width: 60px; height: 3px; background: #e85d3a; margin-top: 18px; border-radius: 2px; }

.content-card {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 36px 40px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
  font-size: 15px; line-height: 2; color: #3a4a5c;
}
.lead { font-size: 17px; font-weight: 500; color: #162b42; margin-bottom: 20px; }
.num { color: #e85d3a; font-weight: 700; }
.unit { font-size: 14px; font-weight: 400; }

.stat-row {
  display: flex; gap: 24px; justify-content: center;
  margin: 28px 0; padding: 24px 0;
  border-top: 1px solid #eef1f5; border-bottom: 1px solid #eef1f5;
}
.stat-item { text-align: center; }
.stat-item strong { display: block; font-size: 28px; font-weight: 800; color: #162b42; line-height: 1.2; }
.stat-item span { display: block; font-size: 12px; color: #8a9bb5; margin-top: 4px; }

blockquote {
  margin: 24px 0 0; padding: 20px 28px;
  background: #f8f9fc; border-left: 4px solid #e85d3a; border-radius: 0 8px 8px 0;
}
blockquote p { color: #5a6b7c; font-style: italic; font-size: 14px; line-height: 1.9; }

@media (max-width: 640px) {
  .hero-card { padding: 28px 24px; }
  .hero-name { font-size: 26px; }
  .content-card { padding: 24px 20px; }
  .stat-row { flex-wrap: wrap; gap: 16px; }
}
</style>
