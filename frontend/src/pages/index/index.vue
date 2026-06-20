<template>
  <view class="home">
    <NavBar title="一晨一食" icon="home" />
    <view class="home-content">
      <view class="header" @click="rainSnacks">
        <view class="logo-ring">
          <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
        </view>
        <text class="title">一晨一食</text>
        <text class="subtitle">每天早起，从早餐开始</text>
      </view>

      <view class="snack-rain">
        <view v-for="s in snacks" :key="s.id" class="snack" :style="s.style">{{ s.emoji }}</view>
      </view>

      <view class="stats-card">
        <view class="stat-item" @click="goCheckin">
          <view class="stat-icon green"><Icon name="ontime" size="md" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.currentMonthOnTime || 0 }}</text>
          <text class="stat-label">本月准时</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon orange"><Icon name="streak" size="md" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.continuousDays || 0 }}</text>
          <text class="stat-label">连续天数</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon blue"><Icon name="score" size="md" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.totalScore || 0 }}</text>
          <text class="stat-label">总积分</text>
        </view>
      </view>

      <view class="action-section">
        <button class="action-btn" @click="goCheckin">
          <Icon v-if="todayCheckin" name="check" size="md" />
          <Icon v-else name="breakfast" size="md" />
          <text>{{ todayCheckin ? '今日已打卡' : '去打卡' }}</text>
        </button>
      </view>

      <view class="today-tip" v-if="todayCheckin && todayCheckin.aiComment">
        <view class="tip-icon"><Icon name="ai" size="sm" color="#5B9BD5" /></view>
        <view class="tip-body">
          <text class="tip-label">AI 点评</text>
          <text class="tip-text">{{ todayCheckin.aiComment }}</text>
        </view>
      </view>

      <view class="bottom-section">
        <text class="bottom-text">一晨一食 · 日日坚持</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { checkinApi } from '@/api/checkin'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const todayCheckin = ref(null)
const snacks = ref([])

const snackEmojis = ['🥐', '🥚', '🥛', '☕', '🍞', '🥟', '🍎', '🥣', '🍪', '🧁', '🍩', '🥝', '🍌', '🥯']

onShow(async () => {
  if (userStore.isLoggedIn) {
    try {
      todayCheckin.value = await checkinApi.getToday()
      await userStore.refreshUserInfo()
    } catch (e) {}
  }
})

let snackId = 0

function rainSnacks() {
  for (let i = 0; i < 20; i++) {
    const id = ++snackId
    const emoji = snackEmojis[Math.floor(Math.random() * snackEmojis.length)]
    const left = Math.random() * 100
    const delay = Math.random() * 1.5
    const duration = 2 + Math.random() * 2
    const size = 28 + Math.random() * 24
    const rotation = Math.random() * 360
    snacks.value.push({
      id,
      emoji,
      style: {
        left: left + '%',
        animationDelay: delay + 's',
        animationDuration: duration + 's',
        fontSize: size + 'rpx',
        '--r': rotation + 'deg'
      }
    })
    const totalTime = (delay + duration) * 1000
    setTimeout(() => {
      snacks.value = snacks.value.filter(s => s.id !== id)
    }, totalTime)
  }
}

function goCheckin() {
  uni.switchTab({ url: '/pages/checkin/index' })
}
</script>

<style lang="scss">
@import '@/uni.scss';

.home {
  min-height: 100vh;
  background: $bg-gradient;
}
.home-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80rpx;
}

.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 50rpx;
  padding: 0 32rpx;

  .logo-ring {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(91,155,213,0.15), rgba(74,139,194,0.08));
    @include flex-center;
    margin-bottom: 24rpx;
    box-shadow: 0 0 0 8rpx rgba(91,155,213,0.08);
  }

  .logo {
    width: 130rpx;
    height: 130rpx;
    border-radius: 50%;
    box-shadow: 0 8rpx 32rpx rgba(91, 155, 213, 0.3);
    transition: transform 0.3s;
    &:active { transform: scale(0.92); }
  }

  .title {
    font-size: 48rpx;
    font-weight: bold;
    background: linear-gradient(135deg, #5B9BD5, #3A7BBF);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .subtitle {
    font-size: 26rpx;
    color: #7F8C9B;
    margin-top: 8rpx;
  }
}

.stats-card {
  @include card;
  padding: 32rpx 16rpx;
  margin: 0 32rpx 32rpx;
  width: calc(100% - 64rpx);
  display: flex;
  align-items: center;

  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10rpx;
    padding: 8rpx 16rpx;
    transition: transform 0.2s;
    &:active { transform: scale(0.95); }
  }

  .stat-icon {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    @include flex-center;
    &.green { background: linear-gradient(135deg, #66BB6A, #43A047); }
    &.orange { background: linear-gradient(135deg, #FFA726, #FB8C00); }
    &.blue { background: linear-gradient(135deg, #5B9BD5, #3A7BBF); }
  }

  .stat-divider {
    width: 2rpx;
    height: 64rpx;
    background: linear-gradient(180deg, transparent, $border, transparent);
  }

  .stat-num {
    font-size: 40rpx;
    font-weight: bold;
    color: $text;
  }

  .stat-label {
    font-size: 22rpx;
    color: $text-secondary;
  }
}

.action-section {
  width: 100%;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.action-btn {
  @include btn-primary;
  width: 100%;
  letter-spacing: 4rpx;
  &:active { opacity: 0.9; }
}

.today-tip {
  @include card;
  margin: 0 32rpx 32rpx;
  padding: 24rpx 28rpx;
  display: flex;
  gap: 16rpx;

  .tip-icon {
    flex-shrink: 0;
    width: 48rpx;
    height: 48rpx;
    background: rgba(91,155,213,0.1);
    border-radius: 50%;
    @include flex-center;
  }

  .tip-body {
    flex: 1;
  }

  .tip-label {
    font-size: 22rpx;
    font-weight: 600;
    color: $primary;
    margin-bottom: 4rpx;
    display: block;
  }

  .tip-text {
    font-size: 26rpx;
    color: $text-secondary;
    line-height: 1.6;
  }
}

.bottom-section {
  margin-top: 48rpx;
  padding: 16rpx 32rpx 48rpx;

  .bottom-text {
    font-size: 24rpx;
    color: $text-muted;
    letter-spacing: 4rpx;
  }
}

.snack-rain {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  pointer-events: none;
  z-index: 999;
}

.snack {
  position: absolute;
  top: -60rpx;
  animation: snackFall ease-in forwards;
  @keyframes snackFall {
    0% { transform: translateY(0) rotate(0deg); opacity: 1; }
    100% { transform: translateY(110vh) rotate(var(--r, 360deg)); opacity: 0.3; }
  }
}
</style>
