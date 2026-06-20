<template>
  <view class="rank-page">
    <NavBar title="排行榜" icon="rank" />
    <view class="rank-body">
      <view class="tab-bar">
        <view class="tab-item" v-for="t in tabs" :key="t.key"
          :class="{ active: activeTab === t.key }"
          @click="switchTab(t.key)">
          <text>{{ t.icon }}</text>
          <text>{{ t.label }}</text>
        </view>
        <view class="tab-indicator" :class="'pos-' + tabs.findIndex(t => t.key === activeTab)"></view>
      </view>

      <view class="rank-list">
        <view class="rank-item" v-for="(item, idx) in rankList" :key="item.userId">
          <view class="rank-badge" :class="{ gold: idx === 0, silver: idx === 1, bronze: idx === 2 }">
            <text v-if="idx < 3" class="medal-text">{{ ['🥇','🥈','🥉'][idx] }}</text>
            <text v-else class="rank-number">{{ idx + 1 }}</text>
          </view>
          <image :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="rank-avatar"></image>
          <view class="rank-info">
            <text class="rank-name">{{ item.nickname }}</text>
            <text class="rank-value">{{ item.scoreValue }}{{ activeTab === 'score' ? '分' : '次' }}</text>
          </view>
        </view>

        <view class="empty" v-if="rankList.length === 0">
          <Icon name="crown" size="xl" color="#B0BEC5" />
          <text class="empty-title">暂无排行数据</text>
          <text class="empty-sub">快来打卡抢占榜首吧</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { rankApi } from '@/api/rank'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const activeTab = ref('on_time')
const rankList = ref([])

const tabs = [
  { key: 'on_time', label: '准时早餐榜', icon: '⏰' },
  { key: 'continuous', label: '连续早起榜', icon: '🔥' },
  { key: 'score', label: '积分总榜', icon: '⭐' },
]

onShow(() => loadRank())

async function switchTab(key) {
  activeTab.value = key
  await loadRank()
}

async function loadRank() {
  try {
    const apis = {
      on_time: rankApi.getOnTimeRank,
      continuous: rankApi.getContinuousRank,
      score: rankApi.getScoreRank,
    }
    rankList.value = await apis[activeTab.value](50)
  } catch (e) {
    rankList.value = []
  }
}
</script>

<style lang="scss">
@import '@/uni.scss';

.rank-page {
  min-height: 100vh;
  background: $bg-gradient;
}

.tab-bar {
  position: relative;
  display: flex;
  background: $card;
  margin: 20rpx 32rpx;
  border-radius: 40rpx;
  padding: 6rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);

  .tab-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6rpx;
    font-size: 24rpx;
    color: $text-secondary;
    padding: 16rpx 0;
    border-radius: 34rpx;
    z-index: 1;
    transition: all 0.3s;
    white-space: nowrap;

    &.active {
      color: #fff;
      font-weight: 600;
    }
  }

  .tab-indicator {
    position: absolute;
    top: 6rpx;
    bottom: 6rpx;
    width: calc(33.33% - 12rpx);
    background: linear-gradient(135deg, $primary, $primary-dark);
    border-radius: 34rpx;
    transition: left 0.3s cubic-bezier(.4,0,.2,1);
    &.pos-0 { left: 6rpx; }
    &.pos-1 { left: calc(33.33% + 2rpx); }
    &.pos-2 { left: calc(66.66% - 2rpx); }
  }
}

.rank-list {
  padding: 0 32rpx;
}

.rank-item {
  @include card;
  display: flex;
  align-items: center;
  padding: 24rpx;
  margin-bottom: 16rpx;
  transition: transform 0.2s;
  &:active { transform: scale(0.98); }
}

.rank-badge {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  @include flex-center;
  margin-right: 20rpx;
  flex-shrink: 0;
  font-weight: bold;

  &.gold {
    background: linear-gradient(135deg, #FFD700, #FFC107);
    box-shadow: 0 4rpx 12rpx rgba(255,215,0,0.4);
  }
  &.silver {
    background: linear-gradient(135deg, #E0E0E0, #BDBDBD);
    box-shadow: 0 4rpx 12rpx rgba(192,192,192,0.3);
  }
  &.bronze {
    background: linear-gradient(135deg, #CD7F32, #A0522D);
    box-shadow: 0 4rpx 12rpx rgba(205,127,50,0.3);
  }

  .medal-text { font-size: 30rpx; }
  .rank-number {
    font-size: 26rpx;
    color: $text-secondary;
    font-weight: 600;
  }
}

.rank-avatar {
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  border: 3rpx solid $primary-light;
  flex-shrink: 0;
}

.rank-info { flex: 1; }

.rank-name {
  font-size: 28rpx;
  color: $text;
  font-weight: 500;
  display: block;
  margin-bottom: 4rpx;
}

.rank-value {
  font-size: 24rpx;
  color: $primary;
  font-weight: 500;
}

.empty {
  @include card;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 80rpx 0;
  margin-top: 16rpx;

  .empty-title {
    font-size: 28rpx;
    color: $text-secondary;
  }

  .empty-sub {
    font-size: 24rpx;
    color: $text-muted;
  }
}
</style>
