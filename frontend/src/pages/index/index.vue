<template>
  <view class="home">
    <NavBar title="一晨一食" icon="home" />
    <view class="home-content">
    <view class="header" @click="rainSnacks">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
      <text class="title">一晨一食</text>
      <text class="subtitle">每天早起，从早餐开始</text>
    </view>

    <view class="snack-rain">
      <view v-for="s in snacks" :key="s.id" class="snack" :style="s.style">{{ s.emoji }}</view>
    </view>

    <view class="stats-card">
      <view class="stat-item" @click="goCheckin">
        <Icon name="ontime" size="lg" color="green" />
        <text class="stat-num">{{ userStore.userInfo?.currentMonthOnTime || 0 }}</text>
        <text class="stat-label">本月准时</text>
      </view>
      <view class="stat-item">
        <Icon name="streak" size="lg" color="orange" />
        <text class="stat-num">{{ userStore.userInfo?.continuousDays || 0 }}</text>
        <text class="stat-label">连续天数</text>
      </view>
      <view class="stat-item">
        <Icon name="score" size="lg" color="gold" />
        <text class="stat-num">{{ userStore.userInfo?.totalScore || 0 }}</text>
        <text class="stat-label">总积分</text>
      </view>
    </view>

    <view class="action-section">
      <view class="action-btn primary" @click="goCheckin">
        <Icon v-if="todayCheckin" name="check" size="md" />
        <Icon v-else name="breakfast" size="md" />
        <text>{{ todayCheckin ? '已打卡' : '去打卡' }}</text>
      </view>
    </view>

    <view class="today-tip" v-if="todayCheckin && todayCheckin.aiComment">
      <Icon name="ai" size="md" />
      <text class="tip-text">{{ todayCheckin.aiComment }}</text>
    </view>

    <view class="bottom-wave"><Icon name="sun" size="xl" /><text class="wave-text">一晨一食，日日坚持</text></view>
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
.home {
  min-height: 100vh;
  background: linear-gradient(180deg, #F0F7FF 0%, #E0EEFF 100%);
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

  .logo {
    width: 140rpx;
    height: 140rpx;
    margin-bottom: 20rpx;
    border-radius: 50%;
    box-shadow: 0 8rpx 24rpx rgba(91, 155, 213, 0.3);
    transition: transform 0.3s;
    &:active { transform: scale(0.92); }
  }

  .title {
    font-size: 48rpx;
    font-weight: bold;
    background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .subtitle {
    font-size: 26rpx;
    color: #999;
    margin-top: 8rpx;
  }
}

.stats-card {
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  border-radius: 20rpx;
  padding: 32rpx 16rpx;
  margin: 0 32rpx 32rpx;
  width: calc(100% - 64rpx);
  display: flex;
  justify-content: space-around;
  box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.06);

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8rpx;
    padding: 8rpx 16rpx;
    border-radius: 16rpx;
    transition: transform 0.2s;
    &:active { transform: scale(0.95); }
  }

  .stat-num {
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
  }

  .stat-label {
    font-size: 22rpx;
    color: #999;
  }
}

.action-section {
  width: 100%;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.action-btn {
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  font-size: 34rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
  box-shadow: 0 8rpx 24rpx rgba(91, 155, 213, 0.3);

  &.primary {
    background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
    color: #fff;
  }
}

.today-tip {
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin: 0 32rpx;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
  border-left: 6rpx solid #5B9BD5;

  .tip-text {
    font-size: 26rpx;
    color: #555;
    flex: 1;
    line-height: 1.6;
  }
}

.bottom-wave {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-top: 48rpx;
  padding: 16rpx 32rpx;
  .wave-text { font-size: 24rpx; color: #bbb; }
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
