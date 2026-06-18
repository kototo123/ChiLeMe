<template>
  <view class="community">
    <NavBar title="社区" icon="community" />
    <view class="community-content">
    <view class="header-actions">
      <button class="publish-btn" @click="goPublish"><Icon name="pen" size="sm" /> 发布</button>
    </view>

    <view class="feed">
      <view class="post-card" v-for="post in postList" :key="post.id" @click="goDetail(post.id)">
        <view class="post-user">
          <image class="user-avatar" :src="post.userAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <text class="user-name">{{ post.userNickname }}</text>
        </view>

        <text class="post-content">{{ post.content }}</text>

        <view class="post-images" v-if="post.images && post.images.length > 0">
          <image v-for="(img, idx) in post.images.slice(0, 3)" :key="idx"
            :src="img" mode="aspectFill" class="post-image"></image>
        </view>

        <view class="post-tags" v-if="post.tags && post.tags.length > 0">
          <text class="post-tag" v-for="tag in post.tags" :key="tag">{{ tag }}</text>
        </view>

        <view class="post-actions">
          <view class="action" :class="{ active: post.liked }" @click.stop="toggleLike(post)">
            <Icon :name="post.liked ? 'liked' : 'like'" size="sm" />
            <text class="action-count">{{ post.likeCount || '' }}</text>
          </view>
          <view class="action">
            <Icon name="comment" size="sm" />
            <text class="action-count">{{ post.commentCount || '' }}</text>
          </view>
          <view class="action" :class="{ active: post.favorited }" @click.stop="toggleFavorite(post)">
            <Icon :name="post.favorited ? 'starred' : 'star'" size="sm" />
            <text class="action-count">{{ post.favoriteCount || '' }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="load-more" @click="loadMore" v-if="hasMore">加载更多</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { postApi } from '@/api/post'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const postList = ref([])
const page = ref(1)
const hasMore = ref(true)

onShow(() => {
  loadPosts(true)
})

async function loadPosts(refresh = false) {
  if (refresh) { page.value = 1; hasMore.value = true }
  try {
    const res = await postApi.getFeed(page.value, 20)
    if (refresh) postList.value = res.records || []
    else postList.value.push(...(res.records || []))
    hasMore.value = res.records?.length >= 20
  } catch (e) {}
}

async function loadMore() {
  page.value++
  await loadPosts()
}

async function toggleLike(post) {
  try {
    if (post.liked) {
      await postApi.unlike(post.id)
      post.liked = false
      post.likeCount--
    } else {
      await postApi.like(post.id)
      post.liked = true
      post.likeCount++
    }
  } catch (e) {}
}

async function toggleFavorite(post) {
  try {
    if (post.favorited) {
      await postApi.unfavorite(post.id)
      post.favorited = false
      post.favoriteCount--
    } else {
      await postApi.favorite(post.id)
      post.favorited = true
      post.favoriteCount++
    }
  } catch (e) {}
}

function goPublish() {
  uni.navigateTo({ url: '/pages/community/publish' })
}

function goDetail(id) {
  uni.navigateTo({ url: '/pages/community/detail?id=' + id })
}
</script>

<style lang="scss">
.community { background: #F0F7FF; min-height: 100vh; }
.community-content { padding-top: 8rpx; }

.header-actions {
  padding: 20rpx 32rpx;
  display: flex;
  justify-content: flex-end;

  .publish-btn {
    background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
    color: #fff;
    font-size: 26rpx;
    padding: 16rpx 32rpx;
    border-radius: 40rpx;
    border: none;
    display: flex;
    align-items: center;
    gap: 6rpx;
    box-shadow: 0 4rpx 16rpx rgba(91, 155, 213,0.3);
  }
}

.post-card {
  background: #fff;
  border-radius: 20rpx;
  margin: 16rpx 32rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
  transition: transform 0.2s;
  &:active { transform: scale(0.98); }
}

.post-user {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;

  .user-avatar {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    margin-right: 16rpx;
    border: 2rpx solid #E8F0FE;
  }

  .user-name { font-size: 28rpx; color: #333; font-weight: 500; }
}

.post-content { font-size: 28rpx; color: #333; line-height: 1.6; display: block; margin-bottom: 16rpx; }

.post-images {
  display: flex;
  gap: 8rpx;
  margin-bottom: 16rpx;

  .post-image {
    width: 200rpx;
    height: 200rpx;
    border-radius: 12rpx;
  }
}

.post-tags { margin-bottom: 16rpx; display: flex; flex-wrap: wrap; gap: 8rpx; }

.post-tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  background: #E8F0FE;
  color: #5B9BD5;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.post-actions {
  display: flex;
  gap: 48rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid #f5f5f5;

  .action {
    display: flex;
    align-items: center;
    gap: 6rpx;
    font-size: 24rpx;
    color: #999;
    transition: color 0.2s;

    &.active { color: #5B9BD5; }
    &:active { opacity: 0.6; }
  }

  .action-count { font-size: 22rpx; color: inherit; }
}

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 26rpx;
  color: #ccc;
}
</style>
