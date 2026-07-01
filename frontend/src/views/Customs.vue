<script setup>
</script>

<template>
  <div class="customs-page">
    <div class="page-hero">
      <span class="hero-icon">&#128220;</span>
      <div>
        <h2>海关须知</h2>
        <p>出入境旅客通关指引</p>
      </div>
    </div>

    <!-- 出境 -->
    <section class="card">
      <div class="card-header outbound">
        <span class="badge">出境</span>
        <h3>海关出境旅客通关指引</h3>
      </div>
      <p class="intro">各位旅客您好，在您办理海关通关手续时，有以下几项重要提示：</p>
      <ul class="tips">
        <li>
          <span class="dot red" />
          <span>如果您携带有<strong>"海关出境旅客行李物品申报单"9至15项</strong>的物品，应填写海关申报单，并选择<strong>"申报通道"</strong>（又称<em>"红色通道"</em>）通关。</span>
        </li>
        <li>
          <span class="dot green" />
          <span>如果您<strong>没有</strong>携带需要申报的物品无需填写海关申报单，可选择<strong>"无申报通道"</strong>（又称<em>"绿色通道"</em>，标识为<span class="green-tag">■</span>）通关。</span>
        </li>
        <li>
          <span class="dot" />
          <span>携带"海关出境旅客行李物品申报单"第9项所列物品时，旅客应填写<strong>两份申报单</strong>，海关验核签章后将其中一份申报单退还旅客凭以办理有关物品复带进境手续。</span>
        </li>
        <li>
          <span class="dot warn" />
          <span><strong>旅客禁止携带以下物品出境（节选）：</strong>各种武器、仿真武器、弹药及爆炸物品；伪造的货币及伪造的有价证券；鸦片、吗啡、海洛因、大麻以及其它能使人成瘾的麻醉品、精神药物；对中国政治、经济、文化、道德有害的印刷品、胶卷、照片、唱片、影片、录音带、录像带、激光唱盘、激光视盘、计算机存储介质及其它物品；珍贵文物及其它禁止出境的文物。</span>
        </li>
        <li>
          <span class="dot" />
          <span>请旅客<strong>如实申报</strong>，否则海关将依法处理。</span>
        </li>
      </ul>
    </section>

    <!-- 入境 -->
    <section class="card">
      <div class="card-header inbound">
        <span class="badge">入境</span>
        <h3>海关入境旅客通关指引</h3>
      </div>
      <p class="intro">各位旅客您好，在您办理海关通关手续时，有以下几项重要提示：</p>
      <ul class="tips">
        <li>
          <span class="dot red" />
          <span>如果您携带有<strong>"海关进境旅客行李物品申报单"9至17项</strong>的物品，应填写海关申报单，并选择<strong>"申报通道"</strong>（又称<em>"红色通道"</em>）通关。</span>
        </li>
        <li>
          <span class="dot green" />
          <span>如果您<strong>没有</strong>携带需要申报的物品无需填写海关申报单，可选择<strong>"无申报通道"</strong>（又称<em>"绿色通道"</em>，标识为<span class="green-tag">■</span>）通关。</span>
        </li>
        <li>
          <span class="dot" />
          <span>携带需复带出境超过折合<span class="hl">5000美元</span>的外币现钞或有分离运输行李时，旅客应填写<strong>两份申报手续</strong>。</span>
        </li>
        <li>
          <span class="dot warn" />
          <span><strong>旅客禁止携带以下物品进境（节选）：</strong>各种武器、仿真武器、弹药及爆炸物品；伪造的货币及伪造的有价证券；鸦片、吗啡、海洛因、大麻以及其它能使人成瘾的麻醉品、精神药物；对中国政治、经济、文化、道德有害的印刷品、胶卷、照片、唱片、影片、录音带、录像带、激光唱盘、激光视盘、计算机存储介质及其它物品。</span>
        </li>
        <li>
          <span class="dot" />
          <span>请旅客<strong>如实申报</strong>，否则海关将依法处理。</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.customs-page { max-width: 800px; margin: 0 auto; display: flex; flex-direction: column; gap: 20px; }

.page-hero {
  display: flex; align-items: center; gap: 16px;
  background: linear-gradient(135deg, #162b42, #1f3d5c);
  border-radius: 16px; padding: 26px 32px; color: #fff;
}
.hero-icon { font-size: 36px; }
.page-hero h2 { font-size: 20px; font-weight: 700; }
.page-hero p { font-size: 13px; color: rgba(255,255,255,.55); margin-top: 2px; }

.card {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 28px 32px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.card-header h3 { font-size: 16px; font-weight: 700; color: #162b42; }
.badge {
  font-size: 11px; font-weight: 700; padding: 3px 12px; border-radius: 20px;
  color: #fff; letter-spacing: 1px;
}
.outbound .badge { background: #162b42; }
.inbound .badge { background: #e85d3a; }
.intro { font-size: 13px; color: #5a6b7c; margin-bottom: 16px; }

.tips { list-style: none; display: flex; flex-direction: column; }
.tips li { display: flex; gap: 14px; padding: 14px 0; border-bottom: 1px solid #f0f2f5; font-size: 13px; line-height: 1.8; color: #3a4a5c; }
.tips li:last-child { border-bottom: none; }

.dot {
  flex-shrink: 0; width: 10px; height: 10px; border-radius: 50%;
  background: #c0ccda; margin-top: 6px;
}
.dot.red { background: #e85d3a; }
.dot.green { background: #4caf50; }
.dot.warn { background: #ff9800; }

.tips li strong { color: #162b42; }
.tips li em { font-style: normal; color: #e85d3a; font-weight: 500; }
.green-tag { color: #4caf50; font-weight: 700; font-size: 16px; }
.hl { color: #e85d3a; font-weight: 700; }

@media (max-width: 560px) {
  .card { padding: 20px 16px; }
}
</style>
