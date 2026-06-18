<template><view></view></template>

<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'

onLaunch(() => {
  const userStore = useUserStore()
  setTimeout(() => userStore.autoLogin(), 500)
})
</script>

<style>
page {
  background-color: #F0F7FF;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
