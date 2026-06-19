<template>
  <view class="nav-bar" :style="{ background: bgColor }">
    <view class="nav-left">
      <view v-if="showBack" class="nav-back" @click="goBack"><Icon name="arrowleft" size="md" /> 返回</view>
    </view>
    <view class="nav-center">
      <Icon :name="icon" size="md" v-if="icon" />
      <text class="nav-title">{{ title }}</text>
    </view>
    <view class="nav-right">
      <slot name="right" />
    </view>
  </view>
  <view class="nav-placeholder" :style="{ height: navHeight + 'px' }"></view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Icon from '@/components/Icon.vue'

const props = defineProps({
  title: { type: String, default: '一晨一食' },
  icon: { type: String, default: '' },
  showBack: { type: Boolean, default: false },
  bgColor: { type: String, default: '#F0F7FF' },
})

const navHeight = ref(44)

onMounted(() => {
  const sys = uni.getSystemInfoSync()
  navHeight.value = (sys.statusBarHeight || 20) + 44
})

function goBack() { uni.navigateBack() }
</script>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  display: flex;
  align-items: flex-end;
  padding: 0 16rpx;
  padding-top: var(--status-bar-height, 44px);
  height: calc(var(--status-bar-height, 44px) + 88rpx);
}
.nav-left {
  width: 160rpx;
  display: flex;
  align-items: center;
}
.nav-back {
  display: flex;
  align-items: center;
  gap: 4rpx;
  font-size: 26rpx;
  color: #5B9BD5;
  padding: 8rpx;
}
.nav-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}
.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
}
.nav-right {
  width: 160rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.nav-placeholder {
  width: 100%;
}
</style>
