<template>
  <view class="profile">
    <NavBar title="我的" icon="profile" />
    <view class="profile-body">
      <view class="user-card">
        <view class="user-card-bg"></view>
        <view v-if="userStore.isLoggedIn" class="avatar-wrap" @click="changeAvatar">
          <image :src="userStore.userInfo?.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="avatar"></image>
          <view class="avatar-overlay">
            <Icon name="camera" size="sm" color="white" />
          </view>
        </view>
        <image v-else src="/static/images/default-avatar.png" mode="aspectFill" class="avatar"></image>
        <text v-if="userStore.isLoggedIn" class="nickname">{{ userStore.userInfo?.nickname }}</text>
        <button v-else class="login-btn" @click="handleLogin">微信一键登录</button>
        <text class="city" v-if="userStore.userInfo?.city">{{ userStore.userInfo.city }}</text>
      </view>

      <view class="stats-row">
        <view class="stat-item">
          <view class="stat-icon blue"><Icon name="score" size="sm" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.totalScore || 0 }}</text>
          <text class="stat-label">总积分</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon green"><Icon name="streak" size="sm" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.continuousDays || 0 }}</text>
          <text class="stat-label">连续天数</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon purple"><Icon name="cup" size="sm" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.maxContinuousDays || 0 }}</text>
          <text class="stat-label">最高连续</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon orange"><Icon name="gift" size="sm" color="white" /></view>
          <text class="stat-num">{{ userStore.userInfo?.breakCardCount || 0 }}</text>
          <text class="stat-label">补签卡</text>
        </view>
      </view>

      <view class="menu-card">
        <view class="menu-item" @click="changeAvatar">
          <view class="menu-left">
            <view class="menu-dot blue"><Icon name="camera" size="sm" color="white" /></view>
            <text>更换头像</text>
          </view>
          <Icon name="arrow" size="sm" color="#B0BEC5" />
        </view>
        <view class="menu-divider"></view>
        <view class="menu-item" @click="goPage('health')">
          <view class="menu-left">
            <view class="menu-dot red"><Icon name="health" size="sm" color="white" /></view>
            <text>健康档案</text>
          </view>
          <Icon name="arrow" size="sm" color="#B0BEC5" />
        </view>
        <view class="menu-divider"></view>
        <view class="menu-item" @click="goPage('posts')">
          <view class="menu-left">
            <view class="menu-dot blue"><Icon name="posts" size="sm" color="white" /></view>
            <text>我的帖子</text>
          </view>
          <Icon name="arrow" size="sm" color="#B0BEC5" />
        </view>
        <view class="menu-divider"></view>
        <view class="menu-item" @click="goPage('settings')">
          <view class="menu-left">
            <view class="menu-dot gray"><Icon name="settings" size="sm" color="white" /></view>
            <text>提醒设置</text>
          </view>
          <Icon name="arrow" size="sm" color="#B0BEC5" />
        </view>
        <view class="menu-divider" v-if="unreadCount > 0"></view>
        <view class="menu-item" v-if="unreadCount > 0" @click="goNotifications">
          <view class="menu-left">
            <view class="menu-dot orange"><Icon name="notification" size="sm" color="white" /></view>
            <text>通知（{{ unreadCount }}）</text>
          </view>
          <Icon name="arrow" size="sm" color="#B0BEC5" />
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

async function changeAvatar() {
  if (!userStore.isLoggedIn) return uni.showToast({ title: '请先登录', icon: 'none' })
  try {
    const res = await new Promise((resolve, reject) => {
      uni.chooseImage({ count: 1, sizeType: ['compressed'], success: resolve, fail: reject })
    })
    uni.showLoading({ title: '上传中...' })
    const avatarUrl = await userApi.uploadAvatar(res.tempFilePaths[0])
    const fullUrl = 'https://chileme-production.up.railway.app' + avatarUrl
    await userApi.updateProfile(userStore.userInfo.nickname || '用户', fullUrl, userStore.userInfo.gender)
    userStore.userInfo.avatar = fullUrl
    uni.hideLoading()
    uni.showToast({ title: '头像已更新', icon: 'success' })
  } catch (e) {
    uni.hideLoading()
  }
}

function goPage(page) {
  if (!userStore.isLoggedIn) return uni.showToast({ title: '请先登录', icon: 'none' })
  uni.navigateTo({ url: '/pages/profile/' + page })
}

async function handleLogin() {
  try {
    await userStore.autoLogin()
    if (userStore.isLoggedIn) {
      uni.showToast({ title: '登录成功', icon: 'success' })
    }
  } catch (e) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  }
}

function goNotifications() {
  uni.showModal({ title: '通知', content: '此功能开发中...' })
}
</script>

<style lang="scss">
@import '@/uni.scss';

.profile {
  min-height: 100vh;
  background: $bg-gradient;
}

.profile-body {
  padding: 0 32rpx;
}

.user-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0 40rpx;
  margin-bottom: 24rpx;

  .user-card-bg {
    position: absolute;
    top: 0;
    left: -32rpx;
    right: -32rpx;
    height: 200rpx;
    background: linear-gradient(135deg, rgba(91,155,213,0.12), rgba(74,139,194,0.06));
    border-radius: 0 0 48rpx 48rpx;
  }

  .avatar-wrap {
    position: relative;
    z-index: 1;
    margin-bottom: 20rpx;
  }

  .avatar {
    width: 140rpx;
    height: 140rpx;
    border-radius: 50%;
    border: 6rpx solid $card;
    box-shadow: 0 8rpx 32rpx rgba(91,155,213,0.2);
    display: block;
  }

  .avatar-overlay {
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    border-radius: 50%;
    background: rgba(0,0,0,0.35);
    @include flex-center;
    opacity: 0;
    transition: opacity 0.2s;
  }

  .avatar-wrap:active .avatar-overlay {
    opacity: 1;
  }

  .nickname {
    font-size: 36rpx;
    font-weight: 600;
    color: $text;
    position: relative;
    z-index: 1;
  }

  .login-btn {
    @include btn-primary;
    width: 300rpx;
    font-size: 28rpx;
    position: relative;
    z-index: 1;
    margin-top: 8rpx;
  }

  .city {
    font-size: 26rpx;
    color: $text-secondary;
    margin-top: 8rpx;
    position: relative;
    z-index: 1;
  }
}

.stats-row {
  @include card;
  display: flex;
  align-items: center;
  padding: 28rpx 16rpx;
  margin-bottom: 24rpx;

  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10rpx;
  }

  .stat-icon {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    @include flex-center;
    &.blue { background: linear-gradient(135deg, #5B9BD5, #3A7BBF); }
    &.green { background: linear-gradient(135deg, #66BB6A, #43A047); }
    &.purple { background: linear-gradient(135deg, #AB47BC, #7B1FA2); }
    &.orange { background: linear-gradient(135deg, #FFA726, #FB8C00); }
  }

  .stat-divider {
    width: 2rpx;
    height: 48rpx;
    background: linear-gradient(180deg, transparent, $border, transparent);
  }

  .stat-num {
    font-size: 32rpx;
    font-weight: bold;
    color: $text;
  }

  .stat-label {
    font-size: 22rpx;
    color: $text-secondary;
  }
}

.menu-card {
  @include card;
  overflow: hidden;
  margin-bottom: 32rpx;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 32rpx;
  font-size: 28rpx;
  color: $text;
  transition: background 0.2s;
  &:active { background: $bg; }

  .menu-left {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }
}

.menu-dot {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  @include flex-center;
  flex-shrink: 0;
  &.red { background: linear-gradient(135deg, #EF5350, #C62828); }
  &.blue { background: linear-gradient(135deg, #42A5F5, #1565C0); }
  &.gray { background: linear-gradient(135deg, #90A4AE, #546E7A); }
  &.orange { background: linear-gradient(135deg, #FFA726, #E65100); }
}

.menu-divider {
  height: 2rpx;
  margin: 0 32rpx;
  background: linear-gradient(90deg, transparent, $border, transparent);
}
</style>
