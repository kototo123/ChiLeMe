<template>
  <view class="calendar-wrapper">
    <NavBar title="打卡日历" icon="calendar" showBack />
    <view class="calendar-page">
      <view class="calendar-header">
        <text class="nav-btn" @click="prevMonth">‹</text>
        <text class="month-title">{{ year }}年{{ month }}月</text>
        <text class="nav-btn" @click="nextMonth">›</text>
      </view>

      <view class="calendar-card">
        <view class="weekday-header">
          <text v-for="day in weekdays" :key="day" class="weekday">{{ day }}</text>
        </view>

        <view class="days-grid">
          <view class="day-cell" v-for="(day, idx) in days" :key="idx"
            :class="day.statusClass">
            <text class="day-num" :class="{ muted: !day.num }">{{ day.num || '' }}</text>
            <text class="day-icon" v-if="day.icon">{{ day.icon }}</text>
          </view>
        </view>
      </view>

      <view class="legend">
        <view class="legend-item">
          <view class="legend-dot on-time"></view>
          <text>准时 {{ calendarData?.onTimeCount || 0 }}天</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot late"></view>
          <text>迟到 {{ calendarData?.lateCount || 0 }}天</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot break"></view>
          <text>补签 {{ calendarData?.breakCount || 0 }}天</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { checkinApi } from '@/api/checkin'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const year = ref(new Date().getFullYear())
const month = ref(new Date().getMonth() + 1)
const calendarData = ref(null)

const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const days = computed(() => {
  const firstDay = new Date(year.value, month.value - 1, 1).getDay()
  const daysInMonth = new Date(year.value, month.value, 0).getDate()
  const result = []
  for (let i = 0; i < firstDay; i++) result.push({ num: 0 })
  for (let d = 1; d <= daysInMonth; d++) {
    const dayStr = String(d)
    const status = calendarData.value?.dayStatus?.[dayStr]
    let statusClass = ''
    let icon = ''
    if (status === 1) { statusClass = 'on-time'; icon = '✓' }
    else if (status === 2) { statusClass = 'late'; icon = '!' }
    else if (status === 3) { statusClass = 'break'; icon = '↩' }
    result.push({ num: d, statusClass, icon })
  }
  return result
})

onShow(() => loadData())

async function loadData() {
  try {
    calendarData.value = await checkinApi.getCalendar(year.value, month.value)
  } catch (e) {}
}

function prevMonth() {
  if (month.value === 1) { month.value = 12; year.value-- }
  else month.value--
  loadData()
}

function nextMonth() {
  if (month.value === 12) { month.value = 1; year.value++ }
  else month.value++
  loadData()
}
</script>

<style lang="scss">
@import '@/uni.scss';

.calendar-wrapper {
  min-height: 100vh;
  background: $bg-gradient;
}

.calendar-page {
  padding: 0 32rpx;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40rpx;
  padding: 32rpx 0;

  .nav-btn {
    font-size: 48rpx;
    color: $primary;
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    background: $card;
    @include flex-center;
    box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
    transition: transform 0.2s;
    &:active { transform: scale(0.9); }
  }

  .month-title {
    font-size: 36rpx;
    font-weight: 600;
    color: $text;
    min-width: 200rpx;
    text-align: center;
  }
}

.calendar-card {
  @include card;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  padding-bottom: 16rpx;
  margin-bottom: 8rpx;
  border-bottom: 2rpx solid $border;
}

.weekday {
  font-size: 26rpx;
  color: $text-secondary;
  font-weight: 500;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4rpx;
}

.day-cell {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: transform 0.2s;
  &:active { transform: scale(0.9); }

  &.on-time { background: #E8F5E9; }
  &.late { background: #FFF3E0; }
  &.break { background: #E3F2FD; }

  .day-num {
    font-size: 28rpx;
    color: $text;
    font-weight: 500;
    &.muted { color: transparent; }
  }

  .day-icon { font-size: 20rpx; margin-top: 4rpx; }
}

.legend {
  @include card;
  display: flex;
  justify-content: space-around;
  padding: 24rpx;
  margin-bottom: 32rpx;

  .legend-item {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 26rpx;
    color: $text-secondary;
  }

  .legend-dot {
    width: 20rpx;
    height: 20rpx;
    border-radius: 50%;
    &.on-time { background: #4CAF50; }
    &.late { background: #FF9800; }
    &.break { background: #2196F3; }
  }
}
</style>
