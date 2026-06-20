<template><view></view></template>

<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'

onLaunch(() => {
  const userStore = useUserStore()
  setTimeout(() => userStore.autoLogin(), 500)
})
</script>

<style lang="scss">
@import '@/uni.scss';

page {
  background: $bg;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: $text;
  -webkit-font-smoothing: antialiased;
}
</style>
