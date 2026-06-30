<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <div class="placeholder-page">
    <el-icon :size="64" color="#bbb"><Clock /></el-icon>
    <h2>{{ route.meta.title }} — 页面开发中</h2>
    <p>该功能即将上线，敬请期待！</p>
  </div>
</template>

<style scoped>
.placeholder-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #999;
  gap: 16px;
}
.placeholder-page h2 {
  font-size: 22px;
  color: #666;
}
.placeholder-page p {
  font-size: 14px;
}
</style>
