<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
onMounted(() => {
  const obs = new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible') })
  }, { threshold: 0.1 })
  document.querySelectorAll('.reveal').forEach(el => obs.observe(el))
})
</script>

<template>
  <div class="home">

    <!-- HERO -->
    <header class="hero-sec">
      <div class="hero-bg-wrap">
        <img src="https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=1920&q=80" class="hero-bg-img" alt="">
        <div class="hero-gradient" />
      </div>
      <div class="hero-content">
        <div class="gold-line" style="margin-bottom:2rem" />
        <h1 class="serif hero-title-main">每一次起飞，<br><em class="gold-em">都是一次</em><br>全新的抵达</h1>
        <p class="hero-desc">天津滨海机场通达82座国内城市与47个国际航点，每日执飞超过600个航班，连接世界的每一个角落。</p>
        <div class="hero-btns">
          <button class="btn-primary" @click="router.push('/user/flights')"><span>探索航线</span></button>
          <a href="#" class="btn-link" @click.prevent="router.push('/user/flights')"><span>了解更多</span> &rarr;</a>
        </div>
      </div>
      <div class="hero-stat floating"><em class="serif">600+</em><span>每日航班</span></div>
    </header>

    <!-- VALUES -->
    <section class="section-cream">
      <div class="section-inner">
        <div class="section-head reveal">
          <h2 class="serif section-title-lg">核心价值</h2>
          <p class="section-head-p">安全为基石，航线为血脉，体验为灵魂，美食为记忆，数字化为未来。</p>
        </div>
        <div class="values-grid">
          <div class="val-card reveal card-hover"><svg class="val-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg><h3>安全至上</h3><p>零妥协的安全标准，行业领先的维护体系。</p></div>
          <div class="val-card reveal card-hover mt-12"><svg class="val-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z"/></svg><h3>通达全球</h3><p>129个目的地，无缝衔接您的每一段旅程。</p></div>
          <div class="val-card reveal card-hover mt-24"><svg class="val-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg><h3>客舱体验</h3><p>宽敞座椅、静谧空间、贴心服务。</p></div>
          <div class="val-card reveal card-hover mt-12"><svg class="val-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8h1a4 4 0 010 8h-1"/><path d="M2 8h16v9a4 4 0 01-4 4H6a4 4 0 01-4-4V8z"/></svg><h3>高空美馔</h3><p>米其林团队打造，万米高空也能享受味蕾盛宴。</p></div>
          <div class="val-card reveal card-hover"><svg class="val-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg><h3>数字服务</h3><p>一键值机、实时追踪、智能推荐。</p></div>
        </div>
      </div>
    </section>

    <!-- PROMOS -->
    <section class="promo-sec diagonal-bg">
      <div class="section-inner">
        <div class="flex items-center gap-6 mb-16 reveal"><div class="gold-line" /><h2 class="serif section-title-lg" style="color:var(--cream)">精选航线优惠</h2></div>
        <div class="promo-grid">
          <div v-for="(p,i) in promos" :key="i" class="promo-card reveal" :class="{ 'mt-16': i===1 }" :style="{ transitionDelay: i*0.15+'s' }">
            <div class="promo-img-wrap"><img :src="p.img" class="promo-img destination-img" :alt="p.route" @error="e=>e.target.style.display='none'"></div>
            <div class="promo-info"><div><p class="promo-route">{{ p.from }} &rarr; {{ p.to }}</p><p class="serif promo-price">¥{{ p.price }} <small>起</small></p></div><div class="promo-arrow">&nearr;</div></div>
          </div>
        </div>
      </div>
    </section>

    <!-- DESTINATIONS -->
    <section class="dest-sec">
      <div class="section-inner reveal" style="margin-bottom:3rem"><h2 class="serif section-title-md">热门目的地</h2></div>
      <div class="marquee-wrap"><div class="marquee-track">
        <div v-for="d in destinations" :key="d.name" class="dest-card"><img :src="d.img" class="destination-img" :alt="d.name" @error="e=>e.target.style.display='none'"><div class="dest-card-overlay" /><div class="dest-card-label"><span>{{ d.tag }}</span><strong class="serif">{{ d.name }}</strong></div></div>
        <div v-for="d in destinations" :key="'r'+d.name" class="dest-card"><img :src="d.img" class="destination-img" :alt="d.name" @error="e=>e.target.style.display='none'"><div class="dest-card-overlay" /><div class="dest-card-label"><span>{{ d.tag }}</span><strong class="serif">{{ d.name }}</strong></div></div>
      </div></div>
    </section>

    <!-- FREQUENT FLYER -->
    <section class="section-cream ff-sec">
      <div class="section-inner"><div class="ff-layout reveal">
        <div>
          <div class="gold-line" style="margin-bottom:2rem" /><h2 class="serif section-title-lg" style="margin-bottom:2rem">常旅客计划</h2>
          <p class="ff-sub">从银卡到钻石，每一次飞行都是向上的阶梯。积分永不过期，礼遇层层升级。</p>
          <div class="ff-tiers">
            <div class="ff-tier"><span class="tier-badge">Ag</span><div><strong>银卡 Silver</strong><small>专属值机通道 · 额外5kg行李</small></div></div>
            <div class="ff-tier"><span class="tier-badge au">Au</span><div><strong>金卡 Gold</strong><small>贵宾休息室 · 优先登机 · 25%积分加成</small></div></div>
            <div class="ff-tier"><span class="tier-badge pt">Pt</span><div><strong>铂金 Platinum</strong><small>国际休息室 · 免费升舱 · 50%积分加成</small></div></div>
            <div class="ff-tier"><span class="tier-badge di">Di</span><div><strong>钻石 Diamond</strong><small>全服务特权 · 100%积分加成 · 专属管家</small></div></div>
          </div>
        </div>
        <div class="ff-img-wrap"><img src="https://images.unsplash.com/photo-1540339832862-474599807836?auto=format&fit=crop&w=800&q=80" alt=""></div>
      </div></div>
    </section>

  </div>
</template>

<script>
export default {
  data() {
    return {
      promos: [
        { route: '天津-中国香港', from: '天津', to: '香港', price: '1,398', img: 'https://images.unsplash.com/photo-1540206351-d6465b3ac5c1?auto=format&fit=crop&w=800&q=80' },
        { route: '天津-曼谷', from: '天津', to: '曼谷', price: '5,541', img: 'https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&w=800&q=80' },
        { route: '天津-普吉', from: '天津', to: '普吉', price: '1,303', img: 'https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80' },
      ],
      destinations: [
        { tag: '热带天堂', name: '三亚', img: 'https://images.unsplash.com/photo-1540202404-a2f29016b523?auto=format&fit=crop&w=640&q=80' },
        { tag: '天府之国', name: '成都', img: 'https://images.unsplash.com/photo-1565099824688-e93eb20fe622?auto=format&fit=crop&w=640&q=80' },
        { tag: '霓虹都市', name: '东京', img: 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?auto=format&fit=crop&w=640&q=80' },
        { tag: '沙漠明珠', name: '迪拜', img: 'https://images.unsplash.com/photo-1518684079-3c830dcef090?auto=format&fit=crop&w=640&q=80' },
        { tag: '浪漫之都', name: '巴黎', img: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=640&q=80' },
      ]
    }
  }
}
</script>

<style scoped>
.home { overflow-x: hidden; }
.hero-sec { position: relative; min-height: 100vh; display: flex; align-items: center; overflow: hidden; }
.hero-bg-wrap { position: absolute; inset: 0; z-index: 0; }
.hero-bg-img { width: 100%; height: 100%; object-fit: cover; }
.hero-gradient { position: absolute; inset: 0; background: linear-gradient(135deg, rgba(248,246,241,.92) 0%, rgba(248,246,241,.6) 45%, transparent 100%); }
.hero-content { position: relative; z-index: 1; max-width: 80rem; padding: 120px 80px 80px; width: 100%; }
.hero-title-main { font-size: clamp(48px,8vw,88px); line-height: .95; color: var(--navy); margin-bottom: 2rem; }
.gold-em { color: var(--gold); font-style: italic; }
.hero-desc { max-width: 28rem; font-size: 14px; color: var(--navy-light); line-height: 1.8; margin-bottom: 2.5rem; }
.hero-btns { display: flex; align-items: center; gap: 1.5rem; }
.btn-link { font-size: 12px; text-transform: uppercase; letter-spacing: .2em; color: var(--navy); display: flex; align-items: center; gap: .5rem; text-decoration: none; }
.hero-stat { position: absolute; right: 80px; bottom: 100px; z-index: 1; padding: 28px 32px; background: rgba(255,255,255,.55); backdrop-filter: blur(12px); border: 1px solid rgba(201,162,39,.3); }
.hero-stat em { display: block; font-size: 40px; font-style: italic; color: var(--gold); }
.hero-stat span { display: block; font-size: 11px; text-transform: uppercase; letter-spacing: .15em; color: var(--navy-light); margin-top: 4px; }
.section-cream { padding: 8rem 0; background: var(--cream); }
.section-inner { max-width: 80rem; margin: 0 auto; padding: 0 80px; }
.section-head { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 5rem; gap: 1.5rem; }
.section-head-p { font-size: 14px; color: var(--navy-light); max-width: 24rem; line-height: 1.8; }
.section-title-lg { font-size: 48px; line-height: 1; color: var(--navy); }
.section-title-md { font-size: 40px; line-height: 1; color: var(--navy); }
.values-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 2rem; }
.val-card { border-top: 1px solid var(--gold); padding: 2rem 1.5rem; }
.val-icon { color: var(--gold); width: 32px; height: 32px; margin-bottom: 1.5rem; }
.val-card h3 { font-size: 13px; letter-spacing: .1em; text-transform: uppercase; margin-bottom: .75rem; font-weight: 600; color: var(--navy); }
.val-card p { font-size: 12px; color: var(--navy-light); line-height: 1.8; }
.mt-12 { margin-top: 3rem; } .mt-24 { margin-top: 6rem; }
.promo-sec { padding: 8rem 0; background: var(--navy); color: var(--cream); }
.promo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 2.5rem; }
.mt-16 { margin-top: 4rem; }
.promo-card { cursor: pointer; }
.promo-img-wrap { overflow: hidden; margin-bottom: 1.5rem; }
.promo-img { width: 100%; height: 320px; object-fit: cover; display: block; }
.promo-info { display: flex; justify-content: space-between; align-items: flex-end; }
.promo-route { font-size: 11px; letter-spacing: .2em; text-transform: uppercase; color: var(--gold-soft); margin-bottom: 4px; }
.promo-price { font-size: 32px; font-style: italic; color: var(--cream); }
.promo-price small { font-size: 13px; font-style: normal; opacity: .5; }
.promo-arrow { width: 40px; height: 40px; border-radius: 50%; border: 1px solid var(--gold); display: flex; align-items: center; justify-content: center; color: var(--gold); font-size: 20px; }
.dest-sec { padding: 6rem 0 4rem; background: var(--cream); overflow: hidden; }
.marquee-wrap { overflow: hidden; }
.marquee-track { display: flex; gap: 2rem; padding: 0 1rem; animation: marquee 30s linear infinite; }
.marquee-track:hover { animation-play-state: paused; }
.dest-card { flex-shrink: 0; width: 320px; height: 420px; position: relative; overflow: hidden; }
.dest-card img { width: 100%; height: 100%; object-fit: cover; }
.dest-card-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(10,22,40,.7), transparent); }
.dest-card-label { position: absolute; bottom: 1.5rem; left: 1.5rem; color: var(--cream); }
.dest-card-label span { display: block; font-size: 10px; letter-spacing: .2em; text-transform: uppercase; color: var(--gold-soft); margin-bottom: 4px; }
.dest-card-label strong { font-size: 28px; display: block; }
.ff-sec { padding: 8rem 0; }
.ff-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 5rem; align-items: center; }
.ff-sub { font-size: 14px; color: var(--navy-light); line-height: 1.8; margin-bottom: 2.5rem; max-width: 28rem; }
.ff-tiers { display: flex; flex-direction: column; gap: 1.5rem; }
.ff-tier { display: flex; align-items: center; gap: 1.5rem; padding: 1.5rem; border: 1px solid rgba(201,162,39,.3); transition: all .3s; cursor: pointer; }
.ff-tier:hover { border-color: var(--gold); background: #fff; }
.tier-badge { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 600; background: rgba(192,192,192,.2); color: var(--navy); flex-shrink: 0; }
.tier-badge.au { background: rgba(201,162,39,.15); }
.tier-badge.pt { background: rgba(229,228,226,.3); }
.tier-badge.di { background: var(--gold); }
.ff-tier strong { display: block; font-size: 14px; color: var(--navy); margin-bottom: 4px; }
.ff-tier small { display: block; font-size: 12px; color: var(--navy-light); }
.ff-img-wrap img { width: 100%; height: 600px; object-fit: cover; clip-path: polygon(15% 0, 100% 0, 85% 100%, 0 100%); }
.flex { display: flex; } .items-center { align-items: center; } .gap-6 { gap: 1.5rem; } .mb-16 { margin-bottom: 4rem; }
@media (max-width: 900px) {
  .hero-content { padding: 100px 32px 60px; } .hero-stat { right: 32px; bottom: 60px; }
  .hero-title-main { font-size: 48px; } .section-title-lg { font-size: 36px; } .section-title-md { font-size: 30px; }
  .section-inner { padding: 0 32px; } .values-grid { grid-template-columns: repeat(2, 1fr); } .mt-12, .mt-24 { margin-top: 0; }
  .promo-grid { grid-template-columns: 1fr; } .mt-16 { margin-top: 0; } .ff-layout { grid-template-columns: 1fr; }
}
@media (max-width: 500px) { .values-grid { grid-template-columns: 1fr; } }
</style>
