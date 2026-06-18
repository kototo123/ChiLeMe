<template>
  <view class="checkin">
    <NavBar title="打卡" icon="checkin" />
    <view class="checkin-content">
      <view class="status-bar">
        <view class="status-item" :class="{ active: tab === 'checkin' }" @click="tab = 'checkin'">
          <Icon :name="tab === 'checkin' ? 'checkin' : 'time'" size="sm" />
          <text>打卡</text>
        </view>
        <view class="status-item" :class="{ active: tab === 'history' }" @click="tab = 'history'">
          <Icon :name="tab === 'history' ? 'calendar' : 'calendar'" size="sm" />
          <text>历史</text>
        </view>
      </view>

    <view v-if="tab === 'checkin'" class="checkin-content">
      <view class="time-display">
        <Icon name="clock" size="md" />
        <text class="date">{{ currentDate }}</text>
        <text class="weekday">{{ currentWeekday }}</text>
      </view>

      <view class="status-card" v-if="todayRecord">
        <view class="status-badge" :class="statusClass">
          <Icon :name="statusIcon(todayRecord)" size="sm" /> {{ todayRecord.statusDesc }}
        </view>
        <text class="breakfast-content">{{ todayRecord.content }}</text>
        <image v-if="todayRecord.image" :src="todayRecord.image" class="preview-img" mode="aspectFill"></image>
        <text class="ai-comment" v-if="todayRecord.aiComment"><Icon name="ai" size="sm" /> {{ todayRecord.aiComment }}</text>
      </view>

      <view class="form" v-else>
        <view class="form-group">
          <textarea v-model="content" placeholder="今天早餐吃了什么？" maxlength="500" class="textarea" />
        </view>

        <view class="form-group">
          <view class="tag-list">
            <view class="tag" v-for="tag in availableTags" :key="tag"
              :class="{ active: selectedTags.includes(tag) }"
              @click="toggleTag(tag)">
              <text>{{ tagIcons[tag] || '🍽' }} {{ tag }}</text>
            </view>
          </view>
        </view>

        <view class="form-group">
          <view class="upload-btn" @click="chooseImage">
            <Icon name="camera" size="lg" v-if="!image" />
            <text v-if="!image" class="upload-text">上传图片</text>
            <image v-else :src="image" class="upload-preview" mode="aspectFill" @click="chooseImage"></image>
          </view>
        </view>

        <button class="submit-btn" @click="handleCheckIn" :disabled="!content.trim()">
          <Icon name="checkin" size="sm" /> 打卡
        </button>
      </view>

      <button class="break-card-btn" v-if="!todayRecord && userStore.userInfo?.breakCardCount > 0"
        @click="handleBreakCard">
        <Icon name="breakcard" size="sm" /> 使用补签卡 (剩余{{ userStore.userInfo?.breakCardCount }}张)
      </button>
    </view>

    <view v-else class="history-content">
      <view class="calendar-link" @click="goCalendar">
        <Icon name="calendar" size="sm" /> 查看日历视图 <Icon name="arrow" size="sm" />
      </view>
      <view class="history-list">
        <view class="history-item" v-for="item in historyList" :key="item.id">
          <view class="history-header">
            <text class="history-date">{{ item.checkDate }}</text>
            <text class="history-status" :class="statusClass(item)"><Icon :name="statusIcon(item)" size="sm" /> {{ item.statusDesc }}</text>
          </view>
          <text class="history-text">{{ item.content }}</text>
            <text class="history-score"><Icon name="score" size="sm" /> +{{ item.score }}分</text>
        </view>
      </view>
      <view class="load-more" @click="loadMore" v-if="hasMore">加载更多</view>
    </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { checkinApi } from '@/api/checkin'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const tab = ref('checkin')
const content = ref('')
const image = ref('')
const selectedTags = ref([])
const todayRecord = ref(null)
const historyList = ref([])
const page = ref(1)
const hasMore = ref(true)

const availableTags = ['牛奶', '鸡蛋', '面包', '粥', '水果', '咖啡', '豆浆', '燕麦', '沙拉', '包子', '面条', '米饭']
const tagIcons = { '牛奶': '🥛', '鸡蛋': '🥚', '面包': '🍞', '粥': '🥣', '水果': '🍎', '咖啡': '☕', '豆浆': '🧋', '燕麦': '🌾', '沙拉': '🥗', '包子': '🥟', '面条': '🍜', '米饭': '🍚' }

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const currentWeekday = computed(() => {
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return '星期' + days[new Date().getDay()]
})

const statusClass = computed(() => (record) => {
  if (!record) return ''
  return record.status === 1 ? 'on-time' : record.status === 2 ? 'late' : 'break'
})
function statusIcon(record) {
  if (!record) return ''
  return record.status === 1 ? 'ontime' : record.status === 2 ? 'time' : 'breakcard'
}

onShow(async () => {
  if (userStore.isLoggedIn) {
    await loadToday()
    await loadHistory()
    await userStore.refreshUserInfo()
  }
})

async function loadToday() {
  try {
    todayRecord.value = await checkinApi.getToday()
  } catch (e) {}
}

async function loadHistory() {
  try {
    const list = await checkinApi.getHistory(1, 20)
    historyList.value = list || []
    page.value = 1
  } catch (e) {}
}

async function loadMore() {
  page.value++
  try {
    const list = await checkinApi.getHistory(page.value, 20)
    if (list && list.length > 0) {
      historyList.value.push(...list)
    } else {
      hasMore.value = false
    }
  } catch (e) {
    page.value--
  }
}

function toggleTag(tag) {
  const idx = selectedTags.value.indexOf(tag)
  if (idx > -1) {
    selectedTags.value.splice(idx, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      image.value = res.tempFilePaths[0]
      // TODO: upload to OSS
    }
  })
}

async function handleCheckIn() {
  try {
    const result = await checkinApi.checkIn({
      content: content.value,
      image: image.value,
      tags: selectedTags.value
    })
    uni.showToast({ title: '打卡成功！', icon: 'success' })
    todayRecord.value = result
    content.value = ''
    image.value = ''
    selectedTags.value = []
    await userStore.refreshUserInfo()
  } catch (e) {}
}

async function handleBreakCard() {
  try {
    await checkinApi.useBreakCard()
    uni.showToast({ title: '补签成功', icon: 'success' })
    await loadToday()
    await userStore.refreshUserInfo()
  } catch (e) {}
}

function goCalendar() {
  uni.navigateTo({ url: '/pages/checkin/calendar' })
}
</script>

<style lang="scss">
.checkin {
  min-height: 100vh;
  background: #F0F7FF;
}

.status-bar {
  display: flex;
  background: #fff;
  padding: 20rpx 0;
  margin-bottom: 20rpx;

  .status-item {
    flex: 1;
    text-align: center;
    font-size: 30rpx;
    color: #999;
    padding-bottom: 16rpx;
    border-bottom: 4rpx solid transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;

    &.active {
      color: #5B9BD5;
      border-bottom-color: #5B9BD5;
    }
  }
}

.time-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 40rpx 0 20rpx;

  .date { font-size: 36rpx; color: #333; font-weight: 500; }
  .weekday { font-size: 28rpx; color: #999; }
}

.status-card {
  background: #fff;
  border-radius: 16rpx;
  margin: 20rpx 32rpx;
  padding: 32rpx;

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6rpx;
    padding: 6rpx 20rpx;
    border-radius: 20rpx;
    font-size: 24rpx;
    margin-bottom: 16rpx;

    &.on-time { background: #E8F5E9; color: #4CAF50; }
    &.late { background: #FFF3E0; color: #FF9800; }
    &.break { background: #E3F2FD; color: #2196F3; }
  }

  .breakfast-content { font-size: 30rpx; color: #333; display: block; margin-bottom: 16rpx; }
  .preview-img { width: 200rpx; height: 200rpx; border-radius: 12rpx; }
  .ai-comment { font-size: 26rpx; color: #666; display: block; margin-top: 16rpx; }
}

.form { margin: 20rpx 32rpx; }

.form-group { margin-bottom: 24rpx; }

.textarea {
  width: 100%;
  height: 200rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .tag {
    padding: 10rpx 28rpx;
    background: #f5f5f5;
    border-radius: 30rpx;
    font-size: 24rpx;
    color: #666;

    &.active {
      background: #E8F0FE;
      color: #5B9BD5;
    }
  }
}

.upload-btn {
  width: 160rpx;
  height: 160rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #999;
  border: 2rpx dashed #ddd;

  .upload-text { font-size: 22rpx; }

  .upload-preview {
    width: 100%;
    height: 100%;
    border-radius: 12rpx;
  }
}

.submit-btn {
  height: 88rpx;
  background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  border: none;

  &[disabled] {
    opacity: 0.5;
  }
}

.break-card-btn {
  margin: 20rpx 32rpx;
  height: 72rpx;
  background: #fff;
  color: #5B9BD5;
  border-radius: 36rpx;
  font-size: 28rpx;
  border: 2rpx solid #5B9BD5;
}

.calendar-link {
  text-align: right;
  padding: 20rpx 32rpx;
  font-size: 28rpx;
  color: #5B9BD5;
}

.history-list { margin: 0 32rpx; }

.history-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.history-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.history-date { font-size: 28rpx; color: #333; }

.history-status {
  font-size: 24rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
  &.on-time { color: #4CAF50; }
  &.late { color: #FF9800; }
  &.break { color: #2196F3; }
}

.history-text { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.history-score { font-size: 24rpx; color: #5B9BD5; }

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 28rpx;
  color: #999;
}
</style>
