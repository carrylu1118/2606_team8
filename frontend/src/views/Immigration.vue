<script setup>
import { ref } from 'vue'

const activeTab = ref('departure')
</script>

<template>
  <div class="immi-page">
    <div class="page-hero">
      <span class="hero-icon">&#127760;</span>
      <div>
        <h2>出入境流程</h2>
        <p>国际出发与到达通关指引</p>
      </div>
    </div>

    <div class="tab-bar">
      <button :class="{ active: activeTab === 'departure' }" @click="activeTab = 'departure'">国际出发</button>
      <button :class="{ active: activeTab === 'arrival' }" @click="activeTab = 'arrival'">国际到达</button>
    </div>

    <!-- 国际出发 -->
    <div v-if="activeTab === 'departure'" class="flow-card">
      <h3>国际出发流程</h3>
      <div class="step-list">
        <div class="step">
          <span class="step-num">1</span>
          <div>
            <strong>到达机场</strong>
            <p>旅客应注意时间，一般航空公司规定航班计划起飞时间前45-60分钟停止办理乘机手续。请旅客根据所乘航班航空公司提示的到达机场时间安排好出行，最好在航班起飞前<span class="hl">150分钟</span>到达机场。出港大厅在1号候机楼2楼（航空公司有特殊规定的除外）。</p>
          </div>
        </div>
        <div class="step">
          <span class="step-num">2</span>
          <div><strong>检验检疫</strong><p>进入国际出港大厅后，旅客需首先经过检疫检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">3</span>
          <div><strong>海关检查</strong><p>旅客通过检疫检查后，需经过海关检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">4</span>
          <div><strong>办理乘机手续</strong><p>经过海关和检疫检查后，按照航站楼内航显提示信息前往相应的值机柜台办理乘机手续并托运行李。</p></div>
        </div>
        <div class="step">
          <span class="step-num">5</span>
          <div><strong>边防检查</strong><p>办理完乘机手续后经过边防检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">6</span>
          <div><strong>安全检查</strong><p>旅客应准备好登机牌、机票及有效证件交给安全检查员查验，安检员审核后加盖安检章。为确保飞行安全，旅客须从安全检测门通过，随身行李物品须经X光机检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">7</span>
          <div><strong>等候登机</strong><p>根据登机牌上注明的登机口及候机区内航显屏提示信息，找到候机区内相应登机口等候登机。</p></div>
        </div>
        <div class="step">
          <span class="step-num">8</span>
          <div><strong>登机</strong><p>听到登机广播后，在相应登机口凭登机牌有序登机。</p></div>
        </div>
      </div>
      <div class="phone-bar">
        &#128222; 行李查询热线：<a href="tel:022-24906972">022-24906972</a>
      </div>
    </div>

    <!-- 国际到达 -->
    <div v-if="activeTab === 'arrival'" class="flow-card">
      <h3>国际到达流程</h3>
      <div class="step-list">
        <div class="step">
          <span class="step-num">1</span>
          <div><strong>航班到达</strong><p>航班到达后，可按照服务员的指示进入航站楼，然后参照航站楼内到达旅客的引导标识前往行李提取大厅。</p></div>
        </div>
        <div class="step">
          <span class="step-num">2</span>
          <div><strong>检疫检查</strong><p>旅客下机后首先要经过检疫检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">3</span>
          <div><strong>边防检查</strong><p>检疫检查后到达边防检查通道经过边防检查。</p></div>
        </div>
        <div class="step">
          <span class="step-num">4</span>
          <div><strong>提取行李</strong><p>通过电梯到达一楼行李提取厅领取行李，其入口处设有行李转盘显示屏，您可根据航班号查知您的托运行李所在的转盘。机场配有免费行李车供旅客使用。</p></div>
        </div>
        <div class="step">
          <span class="step-num">5</span>
          <div><strong>海关检查</strong><p>提取完行李后，旅客经过海关检查进入到达大厅。</p></div>
        </div>
        <div class="step">
          <span class="step-num">6</span>
          <div><strong>离开机场</strong><p>在到达大厅您可与亲友会面，或到问讯柜台进行咨询，或到银行兑换货币。出了到达大厅，您可选择乘坐地铁、机场巴士或出租车离开机场。</p></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.immi-page { max-width: 780px; margin: 0 auto; display: flex; flex-direction: column; gap: 20px; }

.page-hero {
  display: flex; align-items: center; gap: 16px;
  background: linear-gradient(135deg, #162b42, #1f3d5c);
  border-radius: 16px; padding: 26px 32px; color: #fff;
}
.hero-icon { font-size: 36px; }
.page-hero h2 { font-size: 20px; font-weight: 700; }
.page-hero p { font-size: 13px; color: rgba(255,255,255,.55); margin-top: 2px; }

.tab-bar { display: flex; gap: 0; background: #fff; border: 1px solid #e5e9f0; border-radius: 12px; overflow: hidden; }
.tab-bar button {
  flex: 1; padding: 14px 0; font-size: 14px; font-weight: 600;
  border: none; background: #fff; color: #6b7c93; cursor: pointer; transition: all .2s;
}
.tab-bar button.active { background: #162b42; color: #fff; }

.flow-card {
  background: #fff; border: 1px solid #e5e9f0; border-radius: 14px;
  padding: 28px 32px; box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.flow-card h3 { font-size: 17px; font-weight: 700; color: #162b42; margin-bottom: 22px; }

.step-list { display: flex; flex-direction: column; }
.step {
  display: flex; gap: 16px; padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}
.step:last-child { border-bottom: none; }

.step-num {
  flex-shrink: 0; width: 30px; height: 30px;
  display: flex; align-items: center; justify-content: center;
  background: #e85d3a; color: #fff; border-radius: 50%;
  font-size: 13px; font-weight: 700;
}
.step strong { display: block; font-size: 14px; color: #162b42; margin-bottom: 3px; }
.step p { font-size: 13px; color: #5a6b7c; line-height: 1.8; margin: 0; }
.hl { color: #e85d3a; font-weight: 700; }

.phone-bar {
  margin-top: 20px; padding: 14px 20px; background: #f0f4fa;
  border-radius: 10px; font-size: 14px; color: #5a6b7c;
  display: flex; align-items: center; gap: 8px;
}
.phone-bar a { color: #162b42; font-weight: 700; text-decoration: none; font-family: 'Outfit', monospace; }
.phone-bar a:hover { color: #e85d3a; }

@media (max-width: 560px) {
  .flow-card { padding: 20px 16px; }
  .step { gap: 12px; }
}
</style>
