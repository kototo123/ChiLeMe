<template>
  <view class="checkin">
    <NavBar title="打卡" icon="checkin" />
    <view class="checkin-body">
      <view class="tab-bar">
        <view class="tab-item" :class="{ active: tab === 'checkin' }" @click="tab = 'checkin'">
          <Icon name="checkin" size="sm" />
          <text>打卡</text>
        </view>
        <view class="tab-item" :class="{ active: tab === 'history' }" @click="tab = 'history'">
          <Icon name="calendar" size="sm" />
          <text>历史</text>
        </view>
        <view class="tab-indicator" :class="tab === 'history' ? 'right' : 'left'"></view>
      </view>

      <view v-if="tab === 'checkin'" class="checkin-tab">
        <view class="time-display">
          <view class="time-icon"><Icon name="clock" size="md" color="#5B9BD5" /></view>
          <view class="time-info">
            <text class="time-date">{{ currentDate }}</text>
            <text class="time-weekday">{{ currentWeekday }}</text>
          </view>
        </view>

        <view class="status-card" v-if="todayRecord">
          <view class="status-badge" :class="statusClass(todayRecord)">
            <Icon :name="statusIcon(todayRecord)" size="sm" /> {{ todayRecord.statusDesc }}
          </view>
          <text class="breakfast-text">{{ todayRecord.content }}</text>
          <image v-if="todayRecord.image" :src="todayRecord.image" class="preview-img" mode="aspectFill"></image>
          <view class="ai-comment" v-if="todayRecord.aiComment">
            <Icon name="ai" size="sm" color="#5B9BD5" /> <text>{{ todayRecord.aiComment }}</text>
          </view>
        </view>

        <view class="form-card" v-else>
          <view class="form-section">
            <textarea v-model="content" placeholder="今天早餐吃了什么？分享一下～" maxlength="500" class="textarea" />
          </view>

          <view class="form-section">
            <text class="form-label">标签</text>
            <view class="tag-list">
              <view class="tag" v-for="tag in availableTags" :key="tag"
                :class="{ active: selectedTags.includes(tag) }"
                @click="toggleTag(tag)">
                <text>{{ tagIcons[tag] || '🍽' }} {{ tag }}</text>
              </view>
            </view>
          </view>

          <view class="form-section">
            <text class="form-label">图片</text>
            <view class="upload-area" @click="chooseImage">
              <image v-if="image" :src="image" class="upload-preview" mode="aspectFill"></image>
              <view v-else class="upload-placeholder">
                <Icon name="camera" size="lg" color="#B0BEC5" />
                <text class="upload-text">添加照片</text>
              </view>
            </view>
          </view>

          <button class="submit-btn" @click="handleCheckIn" :disabled="!content.trim()">
            <Icon name="checkin" size="sm" /> 打卡
          </button>
        </view>

        <button class="break-card-btn" v-if="!todayRecord && userStore.userInfo?.breakCardCount > 0"
          @click="handleBreakCard">
          <Icon name="breakcard" size="sm" /> 使用补签卡（剩余{{ userStore.userInfo?.breakCardCount }}张）
        </button>
      </view>

      <view v-else class="history-tab">
        <view class="calendar-link" @click="goCalendar">
          <Icon name="calendar" size="sm" color="#5B9BD5" /> 日历视图 <Icon name="arrow" size="sm" color="#5B9BD5" />
        </view>
        <view class="history-list">
          <view class="history-item" v-for="item in historyList" :key="item.id">
            <view class="history-top">
              <text class="history-date">{{ item.checkDate }}</text>
              <text class="history-status" :class="statusClass(item)">
                <Icon :name="statusIcon(item)" size="sm" /> {{ item.statusDesc }}
              </text>
            </view>
            <text class="history-text">{{ item.content }}</text>
            <view class="history-footer">
              <Icon name="score" size="sm" color="#5B9BD5" />
              <text class="history-score">+{{ item.score }}分</text>
            </view>
          </view>
        </view>
        <view class="load-more" @click="loadMore" v-if="hasMore">
          <text>加载更多</text>
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
@import '@/uni.scss';

.checkin {
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
    gap: 8rpx;
    font-size: 26rpx;
    color: $text-secondary;
    padding: 16rpx 0;
    border-radius: 34rpx;
    z-index: 1;
    transition: all 0.3s;

    &.active {
      color: #fff;
      font-weight: 600;
    }
  }

  .tab-indicator {
    position: absolute;
    top: 6rpx;
    bottom: 6rpx;
    width: calc(50% - 6rpx);
    background: linear-gradient(135deg, $primary, $primary-dark);
    border-radius: 34rpx;
    transition: transform 0.3s cubic-bezier(.4,0,.2,1);
    &.left { transform: translateX(0); }
    &.right { transform: translateX(calc(100% + 6rpx)); }
  }
}

.checkin-tab, .history-tab {
  padding: 0 32rpx;
}

.time-display {
  @include card;
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 32rpx;
  margin-bottom: 24rpx;

  .time-icon {
    width: 72rpx;
    height: 72rpx;
    background: rgba(91,155,213,0.1);
    border-radius: 50%;
    @include flex-center;
    flex-shrink: 0;
  }

  .time-info {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
  }

  .time-date {
    font-size: 36rpx;
    font-weight: 600;
    color: $text;
  }

  .time-weekday {
    font-size: 24rpx;
    color: $text-secondary;
  }
}

.status-card {
  @include card;
  padding: 32rpx;
  margin-bottom: 24rpx;

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6rpx;
    padding: 8rpx 24rpx;
    border-radius: 20rpx;
    font-size: 24rpx;
    font-weight: 500;
    margin-bottom: 20rpx;

    &.on-time { background: #E8F5E9; color: #2E7D32; }
    &.late { background: #FFF3E0; color: #E65100; }
    &.break { background: #E3F2FD; color: #1565C0; }
  }

  .breakfast-text {
    font-size: 30rpx;
    color: $text;
    display: block;
    margin-bottom: 16rpx;
    line-height: 1.6;
  }

  .preview-img {
    width: 200rpx;
    height: 200rpx;
    border-radius: 16rpx;
    margin-bottom: 16rpx;
  }

  .ai-comment {
    font-size: 26rpx;
    color: $text-secondary;
    display: flex;
    align-items: flex-start;
    gap: 8rpx;
    padding-top: 16rpx;
    border-top: 2rpx solid $border;
    line-height: 1.6;
  }
}

.form-card {
  @include card;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.form-section {
  margin-bottom: 28rpx;

  &:last-child { margin-bottom: 0; }
}

.form-label {
  font-size: 26rpx;
  font-weight: 500;
  color: $text;
  display: block;
  margin-bottom: 16rpx;
}

.textarea {
  width: 100%;
  height: 180rpx;
  background: $bg;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  border: 2rpx solid transparent;
  transition: border-color 0.2s;
  color: $text;
  &:focus { border-color: $primary; }
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;

  .tag {
    padding: 12rpx 28rpx;
    background: $bg;
    border-radius: 30rpx;
    font-size: 24rpx;
    color: $text-secondary;
    transition: all 0.2s;

    &.active {
      background: $primary-light;
      color: $primary;
      font-weight: 500;
    }
  }
}

.upload-area {
  width: 180rpx;
  height: 180rpx;

  .upload-placeholder {
    width: 100%;
    height: 100%;
    background: $bg;
    border-radius: 16rpx;
    border: 2rpx dashed $border;
    @include flex-center;
    flex-direction: column;
    gap: 8rpx;
  }

  .upload-text {
    font-size: 22rpx;
    color: $text-muted;
  }

  .upload-preview {
    width: 100%;
    height: 100%;
    border-radius: 16rpx;
  }
}

.submit-btn {
  @include btn-primary;
  width: 100%;
  margin-top: 8rpx;

  &[disabled] {
    opacity: 0.4;
  }
}

.break-card-btn {
  width: 100%;
  height: 80rpx;
  background: $card;
  color: $primary;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: 2rpx solid rgba($primary, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  box-shadow: $card-shadow;
}

.calendar-link {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8rpx;
  padding: 20rpx 0;
  font-size: 26rpx;
  color: $primary;
}

.history-list {
  .history-item {
    @include card;
    padding: 28rpx;
    margin-bottom: 16rpx;
  }

  .history-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12rpx;
  }

  .history-date {
    font-size: 28rpx;
    font-weight: 500;
    color: $text;
  }

  .history-status {
    font-size: 24rpx;
    display: flex;
    align-items: center;
    gap: 4rpx;
    padding: 4rpx 16rpx;
    border-radius: 16rpx;
    &.on-time { background: #E8F5E9; color: #2E7D32; }
    &.late { background: #FFF3E0; color: #E65100; }
    &.break { background: #E3F2FD; color: #1565C0; }
  }

  .history-text {
    font-size: 26rpx;
    color: $text-secondary;
    display: block;
    margin-bottom: 12rpx;
    line-height: 1.5;
  }

  .history-footer {
    display: flex;
    align-items: center;
    gap: 4rpx;
  }

  .history-score {
    font-size: 24rpx;
    color: $primary;
    font-weight: 500;
  }
}

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 26rpx;
  color: $text-muted;
}
</style>
