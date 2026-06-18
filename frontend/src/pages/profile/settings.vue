<template>
  <view class="settings-wrapper">
    <NavBar title="提醒设置" icon="settings" showBack />
    <view class="settings">
    <view class="setting-item">
      <view class="setting-left"><Icon name="notification" size="md" color="orange" /><text>开启提醒</text></view>
      <switch :checked="setting.enabled" @change="toggleEnabled" color="#5B9BD5" />
    </view>

    <view class="setting-item" v-if="setting.enabled">
      <view class="setting-left"><Icon name="time" size="md" color="blue" /><text>工作日提醒时间</text></view>
      <picker mode="time" :value="setting.remindTime" @change="onTimeChange">
        <text class="time-value">{{ setting.remindTime }}</text>
      </picker>
    </view>

    <view class="setting-item" v-if="setting.enabled">
      <view class="setting-left"><Icon name="calendar" size="md" color="green" /><text>仅工作日提醒</text></view>
      <switch :checked="setting.weekdaysOnly" @change="toggleWeekdays" color="#5B9BD5" />
    </view>

    <view class="setting-item" v-if="setting.enabled && !setting.weekdaysOnly">
      <view class="setting-left"><Icon name="sun" size="md" color="orange" /><text>周末提醒时间</text></view>
      <picker mode="time" :value="setting.weekendTime" @change="onWeekendTimeChange">
        <text class="time-value">{{ setting.weekendTime }}</text>
      </picker>
    </view>
  </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { reminderApi } from '@/api/reminder'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const setting = ref({
  enabled: true,
  remindTime: '07:30',
  weekdaysOnly: true,
  weekendTime: '08:30',
})

onShow(() => loadSetting())

async function loadSetting() {
  try { setting.value = await reminderApi.getSetting() } catch (e) {}
}

async function updateSetting() {
  try {
    await reminderApi.updateSetting({
      enabled: setting.value.enabled,
      remindTime: setting.value.remindTime,
      weekdaysOnly: setting.value.weekdaysOnly,
      weekendTime: setting.value.weekendTime,
    })
  } catch (e) {}
}

async function toggleEnabled(e) {
  setting.value.enabled = e.detail.value
  await updateSetting()
}

function onTimeChange(e) {
  setting.value.remindTime = e.detail.value
  updateSetting()
}

async function toggleWeekdays(e) {
  setting.value.weekdaysOnly = e.detail.value
  await updateSetting()
}

function onWeekendTimeChange(e) {
  setting.value.weekendTime = e.detail.value
  updateSetting()
}
</script>

<style lang="scss">
.settings { padding: 32rpx; }

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 16rpx;
  font-size: 28rpx;
  color: #333;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03);

  .setting-left { display: flex; align-items: center; gap: 16rpx; }
}

.time-value { color: #5B9BD5; font-size: 28rpx; }
</style>
