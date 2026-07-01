<script setup>
const faq = [
  {
    q: '行李分哪几种？',
    a: `<p><strong>行李</strong>：指旅客在旅行中为了穿着、使用、舒适或方便的需要而携带的物品和其他个人财物。除另有规定外，包括旅客的托运行李和自理行李。</p>
<p><strong>托运行李</strong>：指旅客交由承运人负责照管和运输并填开行李票的行李。</p>
<p><strong>自理行李</strong>：指经承运人同意由旅客自行负责照管的行李。</p>
<p><strong>随身携带物品</strong>：指经承运人同意由旅客自行携带的零星小件物品。</p>
<p><strong>行李牌</strong>：指识别行李的标志和旅客领取托运行李的凭证。</p>`
  },
  {
    q: '每位旅客的免费行李额是多少？',
    a: `<p>每位旅客的免费行李额（包括托运和自理行李）：持成人或儿童票的头等舱旅客为 <span class="hl">40公斤</span>，公务舱旅客为 <span class="hl">30公斤</span>，经济舱旅客为 <span class="hl">20公斤</span>。持婴儿票的旅客无免费行李额。</p>
<p>搭乘同一航班前往同一目的地的两个以上的同行旅客如在同一时间、同一地点办理行李托运手续，其免费行李额可以按照各自的客票价等级标准合并计算。</p>
<p>构成国际运输的国内航段，每位旅客的免费行李额按适用的国际航线免费行李额计算。目的地为美洲时，其托运行李可以为两件，每件不超过23公斤。部分航空公司有特殊重量限制规定，请旅客留意机票上的提示，或向航空公司咨询。</p>`
  },
  {
    q: '行李体积、重量及件数有哪些限制？',
    a: `<p>托运行李的重量每件不能超过 <span class="hl">50公斤</span>，体积不能超过 <span class="num">40×60×100</span> 厘米，超过上述规定的行李，须事先征得承运人的同意才能托运。</p>
<p>自理行李的重量不能超过 <span class="hl">10公斤</span>，体积每件不超过 <span class="num">20×40×55</span> 厘米。</p>
<p>随身携带物品的重量，每位旅客以 <span class="hl">5公斤</span> 为限。持头等舱客票的旅客，每人可随身携带两件物品。每件随身携带物品的体积均不得超过 <span class="num">20×40×55</span> 厘米。超过上述重量、件数或体积限制的随身携带物品，应作为托运行李托运。</p>`
  },
  {
    q: '行李赔偿电话是什么？',
    a: `<div class="phone-list">
<div class="phone-item"><span class="label">机场行李查询</span><a href="tel:24906927">24906927</a></div>
<div class="phone-item"><span class="label">国航行李查询</span><a href="tel:24905143">24905143</a></div>
<div class="phone-item"><span class="label">天航行李查询</span><a href="tel:24905529">24905529</a></div>
</div>`
  },
  {
    q: '行李赔偿的金额是多少？',
    a: `<p>旅客的托运行李全部或部分损坏、丢失，赔偿金额每公斤不超过人民币 <span class="hl">100元</span>。如行李的价值每公斤低于100元时，按实际价值赔偿。</p>
<div class="phone-list" style="margin-top:16px">
<div class="phone-item"><span class="label">机场行李查询</span><a href="tel:24906927">24906927</a></div>
<div class="phone-item"><span class="label">国航行李查询</span><a href="tel:24905143">24905143</a></div>
</div>`
  },
  {
    q: '小动物如何运输？',
    a: `<p>小动物是指家庭饲养的猫、狗或其他小动物。旅客必须在定座或购票时提出，并提供动物检疫证明，经承运人同意后方可托运。旅客应在乘机的当日，按承运人指定的时间，将小动物自行运到机场办理托运手续。</p>
<p>装运小动物的容器应能防止小动物破坏、逃逸和伸出容器以外损伤旅客、行李或货物；保证空气流通，不致使小动物窒息；能防止粪便渗漏，以免污染飞机、机上设备及其他物品。</p>
<p>旅客携带的小动物，除经承运人特许外，<strong>一律不能放在客舱内</strong>运输。小动物及其容器的重量应按逾重行李费的标准单独收费。</p>`
  }
]
</script>

<template>
  <div class="baggage-page">
    <div class="page-hero">
      <span class="hero-icon">&#128717;</span>
      <div>
        <h2>行李须知</h2>
        <p>了解行李托运、随身携带、赔偿等相关规定</p>
      </div>
    </div>

    <div class="faq-list">
      <div v-for="(item, i) in faq" :key="i" class="faq-card">
        <div class="faq-header">
          <span class="faq-num">Q{{ i + 1 }}</span>
          <h3>{{ item.q }}</h3>
        </div>
        <div class="faq-body" v-html="item.a" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.baggage-page { max-width: 820px; margin: 0 auto; display: flex; flex-direction: column; gap: 20px; }

.page-hero {
  display: flex; align-items: center; gap: 16px;
  background: linear-gradient(135deg, #162b42, #1f3d5c);
  border-radius: 16px; padding: 26px 32px; color: #fff;
}
.hero-icon { font-size: 36px; }
.page-hero h2 { font-size: 20px; font-weight: 700; }
.page-hero p { font-size: 13px; color: rgba(255,255,255,.55); margin-top: 2px; }

.faq-list { display: flex; flex-direction: column; gap: 14px; }

.faq-card {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 24px 28px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
  transition: box-shadow .25s;
}
.faq-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.07); }

.faq-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 14px; }
.faq-num {
  flex-shrink: 0; width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  background: #e85d3a; color: #fff; border-radius: 8px;
  font-size: 13px; font-weight: 700;
}
.faq-header h3 { font-size: 16px; font-weight: 700; color: #162b42; }

.faq-body { color: #3a4a5c; font-size: 14px; line-height: 2; }
.faq-body :deep(p) { margin-bottom: 10px; }
.faq-body :deep(p:last-child) { margin-bottom: 0; }
.faq-body :deep(.hl) { color: #e85d3a; font-weight: 700; }
.faq-body :deep(.num) { font-family: 'Outfit', monospace; font-weight: 600; color: #162b42; }

.phone-list { display: flex; flex-wrap: wrap; gap: 12px; }
.phone-item {
  display: flex; align-items: center; gap: 8px;
  background: #f0f4fa; border-radius: 8px; padding: 8px 16px;
}
.phone-item .label { font-size: 12px; color: #8a9bb5; }
.phone-item a {
  font-size: 15px; font-weight: 700; color: #162b42; text-decoration: none;
  font-family: 'Outfit', monospace; letter-spacing: .5px;
}
.phone-item a:hover { color: #e85d3a; }

@media (max-width: 560px) {
  .faq-card { padding: 18px 16px; }
  .phone-list { flex-direction: column; }
}
</style>
