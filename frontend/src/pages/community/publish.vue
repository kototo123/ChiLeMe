<template>
  <view class="publish-wrapper">
    <NavBar title="发布" icon="pen" showBack />
    <view class="publish">
    <view class="form-group">
      <textarea v-model="content" placeholder="分享一下你的早餐吧～" maxlength="500" class="textarea" />
    </view>

    <view class="form-group">
      <view class="image-grid">
        <view class="image-wrapper" v-for="(img, idx) in images" :key="idx">
          <image :src="img" mode="aspectFill" class="preview-img"></image>
          <text class="remove-btn" @click="removeImage(idx)">×</text>
        </view>
        <view class="add-image" @click="chooseImage" v-if="images.length < 9">
          <Icon name="camera" size="lg" />
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <view class="form-group">
      <view class="tag-list">
        <text class="tag" v-for="tag in availableTags" :key="tag"
          :class="{ active: selectedTags.includes(tag) }"
          @click="toggleTag(tag)">{{ tagIcons[tag] || '' }} {{ tag }}</text>
      </view>
    </view>

    <view class="ai-switch">
      <view class="ai-label"><Icon name="ai" size="sm" /><text>AI帮写文案</text></view>
      <switch :checked="useAi" @change="useAi = !useAi" color="#5B9BD5" />
    </view>

      <button class="submit-btn" @click="handlePublish"><Icon name="pen" size="sm" /> 发布</button>
  </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store'
import { postApi } from '@/api/post'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const content = ref('')
const images = ref([])
const selectedTags = ref([])
const useAi = ref(false)

const availableTags = ['#西式', '#中式', '#减脂', '#增肌', '#素食', '#甜品', '#轻食', '#家常']
const tagIcons = { '#西式': '🍝', '#中式': '🥟', '#减脂': '🥗', '#增肌': '💪', '#素食': '🥦', '#甜品': '🍰', '#轻食': '🥒', '#家常': '🏠' }

function chooseImage() {
  uni.chooseImage({
    count: 9 - images.value.length,
    success: (res) => {
      images.value.push(...res.tempFilePaths)
    }
  })
}

function removeImage(idx) {
  images.value.splice(idx, 1)
}

function toggleTag(tag) {
  const idx = selectedTags.value.indexOf(tag)
  if (idx > -1) selectedTags.value.splice(idx, 1)
  else selectedTags.value.push(tag)
}

async function handlePublish() {
  if (!content.value.trim() && !useAi.value) {
    uni.showToast({ title: '请输入内容', icon: 'none' })
    return
  }
  try {
    await postApi.create({
      content: content.value,
      images: images.value,
      tags: selectedTags.value,
      useAi: useAi.value,
    })
    uni.showToast({ title: '发布成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e) {}
}
</script>

<style lang="scss">
.publish-wrapper { background: #F0F7FF; min-height: 100vh; }
.publish { padding: 24rpx 32rpx; }

.form-group { margin-bottom: 32rpx; }

.textarea {
  width: 100%;
  height: 280rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;

  .image-wrapper {
    position: relative;
    width: 200rpx;
    height: 200rpx;
  }

  .preview-img { width: 100%; height: 100%; border-radius: 12rpx; }

  .remove-btn {
    position: absolute;
    top: -12rpx;
    right: -12rpx;
    width: 40rpx;
    height: 40rpx;
    background: rgba(0, 0, 0, 0.5);
    color: #fff;
    border-radius: 50%;
    text-align: center;
    line-height: 40rpx;
    font-size: 28rpx;
  }

  .add-image {
    width: 200rpx;
    height: 200rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    font-size: 60rpx;
    color: #ccc;
    border: 2rpx dashed #ddd;
    .add-text { font-size: 22rpx; color: #999; }
  }
}

.tag-list { display: flex; flex-wrap: wrap; gap: 16rpx; }

.tag {
  padding: 10rpx 28rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: #666;

  &.active { background: #E8F0FE; color: #5B9BD5; }
}

.ai-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 32rpx;
  font-size: 28rpx;
  color: #333;
}

.submit-btn {
  height: 88rpx;
  background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  border: none;
}
</style>
