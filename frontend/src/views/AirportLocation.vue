<script setup>
import { ref, onMounted } from 'vue'

const mapReady = ref(false)

onMounted(() => {
  if (!window.AMap) {
    console.error('高德地图 JS API 未加载，请检查 index.html 中的 key')
    return
  }

  const map = new AMap.Map('mapContainer', {
    zoom: 15,
    center: [117.346, 39.124],
    mapStyle: 'amap://styles/light'
  })

  const marker = new AMap.Marker({
    position: [117.346, 39.124],
    title: '天津滨海国际机场'
  })
  marker.setMap(map)

  const infoWindow = new AMap.InfoWindow({
    offset: new AMap.Pixel(0, -36),
    content: `
      <div style="padding:10px 14px;font-size:14px;line-height:1.8">
        <h3 style="margin:0 0 6px;color:#162b42">天津滨海国际机场</h3>
        <p style="margin:0;color:#6b7c93">天津市东丽区机场大道1号</p>
      </div>`
  })
  infoWindow.open(map, marker.getPosition())

  marker.on('click', () => {
    infoWindow.open(map, marker.getPosition())
  })

  mapReady.value = true
})
</script>

<template>
  <div class="location-page">
    <div class="location-header">
      <h2>&#128506; 机场位置</h2>
      <p>天津滨海国际机场 · 天津市东丽区机场大道1号</p>
    </div>
    <div id="mapContainer" class="map-wrap" />
    <div v-if="!mapReady" class="map-placeholder">
      <p>地图加载中，请确保已配置高德地图 API Key</p>
    </div>
  </div>
</template>

<style scoped>
.location-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 124px);
}
.location-header {
  background: #fff;
  border: 1px solid #e5e9f0;
  border-radius: 14px;
  padding: 20px 28px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.location-header h2 {
  font-size: 18px; font-weight: 700; color: #162b42; margin-bottom: 4px;
}
.location-header p {
  font-size: 13px; color: #6b7c93;
}
.map-wrap {
  flex: 1;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #e5e9f0;
}
.map-placeholder {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  color: #999;
}
</style>
