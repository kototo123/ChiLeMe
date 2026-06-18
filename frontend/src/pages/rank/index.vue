<template>
  <view class="rank-page">
    <NavBar title="排行榜" icon="rank" />
    <view class="rank-body">
    <view class="tabs">
      <view class="tab" v-for="t in tabs" :key="t.key"
        :class="{ active: activeTab === t.key }"
        @click="switchTab(t.key)">
        <text>{{ t.icon }} {{ t.label }}</text>
      </view>
    </view>

    <view class="rank-list">
      <view class="rank-item" v-for="(item, idx) in rankList" :key="item.userId">
        <view class="rank-num" :class="{ gold: idx === 0, silver: idx === 1, bronze: idx === 2 }">
          <Icon v-if="idx === 0" name="medal" size="sm" />
          <Icon v-else-if="idx === 1" name="medalsilver" size="sm" />
          <Icon v-else-if="idx === 2" name="medalbronze" size="sm" />
          <text v-else>{{ idx + 1 }}</text>
        </view>
        <image :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="rank-avatar"></image>
        <view class="rank-info">
          <text class="rank-name">{{ item.nickname }}</text>
          <text class="rank-value">{{ item.scoreValue }}{{ activeTab === 'score' ? '分' : '次' }}</text>
        </view>
      </view>

      <view class="empty" v-if="rankList.length === 0">
        <Icon name="crown" size="xl" />
        <text>暂无排行数据</text>
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

const tabIcons = { on_time: '⏰', continuous: '🔥', score: '⭐' }
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
.rank-page { background: #F0F7FF; min-height: 100vh; }

.tabs {
  display: flex;
  background: #fff;
  padding: 20rpx 0;

  .tab {
    flex: 1;
    text-align: center;
    font-size: 28rpx;
    color: #999;
    padding-bottom: 16rpx;
    border-bottom: 4rpx solid transparent;

    &.active {
      color: #5B9BD5;
      border-bottom-color: #5B9BD5;
    }
  }
}

.rank-list { padding: 16rpx 32rpx; }

.rank-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 12rpx;
}

.rank-num {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: bold;
  color: #666;
  margin-right: 20rpx;
  flex-shrink: 0;

  &.gold { background: linear-gradient(135deg, #FFD700, #FFC107); color: #fff; box-shadow: 0 4rpx 12rpx rgba(255,215,0,0.4); }
  &.silver { background: linear-gradient(135deg, #E0E0E0, #BDBDBD); color: #fff; box-shadow: 0 4rpx 12rpx rgba(192,192,192,0.3); }
  &.bronze { background: linear-gradient(135deg, #CD7F32, #A0522D); color: #fff; box-shadow: 0 4rpx 12rpx rgba(205,127,50,0.3); }
}

.rank-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.rank-info { flex: 1; }

.rank-name { font-size: 28rpx; color: #333; font-weight: 500; display: block; margin-bottom: 4rpx; }

.rank-value { font-size: 24rpx; color: #5B9BD5; }

.empty {
  text-align: center;
  padding: 80rpx 0;
  font-size: 28rpx;
  color: #999;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  .empty-sub { font-size: 24rpx; color: #ccc; }
}
</style>
