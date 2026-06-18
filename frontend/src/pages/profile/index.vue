<template>
  <view class="profile">
    <NavBar title="我的" icon="profile" />
    <view class="profile-body">
    <view class="user-card">
      <image :src="userStore.userInfo?.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="avatar"></image>
      <text class="nickname">{{ userStore.userInfo?.nickname || '未登录' }}</text>
      <text class="city" v-if="userStore.userInfo?.city">{{ userStore.userInfo.city }}</text>
    </view>

    <view class="stats-row">
      <view class="stat">
        <Icon name="score" size="md" color="orange" />
        <text class="num">{{ userStore.userInfo?.totalScore || 0 }}</text>
        <text class="label">总积分</text>
      </view>
      <view class="stat">
        <Icon name="streak" size="md" color="orange" />
        <text class="num">{{ userStore.userInfo?.continuousDays || 0 }}</text>
        <text class="label">连续天数</text>
      </view>
      <view class="stat">
        <Icon name="cup" size="md" color="orange" />
        <text class="num">{{ userStore.userInfo?.maxContinuousDays || 0 }}</text>
        <text class="label">最高连续</text>
      </view>
      <view class="stat">
        <Icon name="gift" size="md" color="orange" />
        <text class="num">{{ userStore.userInfo?.breakCardCount || 0 }}</text>
        <text class="label">补签卡</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @click="goPage('health')">
        <view class="menu-left"><Icon name="health" size="md" color="red" /><text>健康档案</text></view>
        <Icon name="arrow" size="sm" color="#ccc" />
      </view>
      <view class="menu-item" @click="goPage('posts')">
        <view class="menu-left"><Icon name="posts" size="md" color="blue" /><text>我的帖子</text></view>
        <Icon name="arrow" size="sm" color="#ccc" />
      </view>
      <view class="menu-item" @click="goPage('settings')">
        <view class="menu-left"><Icon name="settings" size="md" color="gray" /><text>提醒设置</text></view>
        <Icon name="arrow" size="sm" color="#ccc" />
      </view>
      <view class="menu-item" v-if="unreadCount > 0" @click="goNotifications">
        <view class="menu-left"><Icon name="notification" size="md" color="orange" /><text>通知 ({{ unreadCount }})</text></view>
        <Icon name="arrow" size="sm" color="#ccc" />
      </view>
      <view class="menu-item" @click="handleLogout">
        <view class="menu-left"><Icon name="logout" size="md" color="red" /><text style="color:#F44336">退出登录</text></view>
        <Icon name="arrow" size="sm" color="#ccc" />
      </view>
    </view>
  </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { userApi } from '@/api/user'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const unreadCount = ref(0)

onShow(async () => {
  if (userStore.isLoggedIn) {
    await userStore.refreshUserInfo()
    try { unreadCount.value = await userApi.getUnreadCount() } catch (e) {}
  }
})

function goPage(page) {
  if (!userStore.isLoggedIn) return uni.showToast({ title: '请先登录', icon: 'none' })
  uni.navigateTo({ url: '/pages/profile/' + page })
}

function goNotifications() {
  uni.showModal({ title: '通知', content: '此功能开发中...' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => { if (res.confirm) userStore.logout() }
  })
}
</script>

<style lang="scss">
.profile { background: #F0F7FF; min-height: 100vh; }

.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0 40rpx;
  background: #fff;
  border-radius: 0 0 40rpx 40rpx;

  .avatar {
    width: 140rpx;
    height: 140rpx;
    border-radius: 50%;
    margin-bottom: 20rpx;
    border: 4rpx solid #5B9BD5;
  }

  .nickname { font-size: 36rpx; font-weight: 500; color: #333; }
  .city { font-size: 26rpx; color: #999; margin-top: 8rpx; }
}

.stats-row {
  display: flex;
  background: #fff;
  margin: 20rpx 32rpx;
  border-radius: 16rpx;
  padding: 24rpx 0;

  .stat {
    flex: 1;
    text-align: center;
    border-right: 2rpx solid #f0f0f0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6rpx;

    &:last-child { border-right: none; }
  }

  .num { font-size: 34rpx; font-weight: bold; color: #5B9BD5; display: block; }
  .label { font-size: 22rpx; color: #999; }
}

.menu-list {
  background: #fff;
  margin: 20rpx 32rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  font-size: 28rpx;
  color: #333;
  border-bottom: 2rpx solid #f0f0f0;
  transition: background 0.2s;
  &:active { background: #fafafa; }
  &:last-child { border-bottom: none; }

  .menu-left { display: flex; align-items: center; gap: 16rpx; }
}
</style>
