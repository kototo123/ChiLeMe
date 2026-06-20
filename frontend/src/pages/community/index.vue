<template>
  <view class="community">
    <NavBar title="社区" icon="community" />
    <view class="community-body">
      <view class="header-actions">
        <button class="publish-btn" @click="goPublish">
          <Icon name="pen" size="sm" /> 发布帖子
        </button>
      </view>

      <view class="feed">
        <view class="post-card" v-for="post in postList" :key="post.id" @click="goDetail(post.id)">
          <view class="post-user">
            <image class="user-avatar" :src="post.userAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
            <view class="post-user-info">
              <text class="user-name">{{ post.userNickname }}</text>
              <text class="post-time">{{ post.createTime }}</text>
            </view>
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

      <view class="load-more" @click="loadMore" v-if="hasMore">
        <text>加载更多</text>
      </view>
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
@import '@/uni.scss';

.community {
  min-height: 100vh;
  background: $bg-gradient;
}

.community-body {
  padding: 0 32rpx;
}

.header-actions {
  padding: 20rpx 0;
  display: flex;
  justify-content: flex-end;

  .publish-btn {
    background: linear-gradient(135deg, $primary, $primary-dark);
    color: #fff;
    font-size: 26rpx;
    padding: 16rpx 32rpx;
    border-radius: 40rpx;
    border: none;
    display: flex;
    align-items: center;
    gap: 8rpx;
    box-shadow: 0 4rpx 16rpx rgba(91, 155, 213, 0.3);
    &:active { opacity: 0.9; }
  }
}

.feed {
  padding-bottom: 16rpx;
}

.post-card {
  @include card;
  padding: 28rpx;
  margin-bottom: 20rpx;
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
    border: 3rpx solid $primary-light;
    flex-shrink: 0;
  }

  .post-user-info {
    flex: 1;
  }

  .user-name {
    font-size: 28rpx;
    color: $text;
    font-weight: 500;
    display: block;
  }

  .post-time {
    font-size: 22rpx;
    color: $text-muted;
  }
}

.post-content {
  font-size: 28rpx;
  color: $text;
  line-height: 1.7;
  display: block;
  margin-bottom: 16rpx;
}

.post-images {
  display: flex;
  gap: 8rpx;
  margin-bottom: 16rpx;

  .post-image {
    width: 200rpx;
    height: 200rpx;
    border-radius: 16rpx;
    background: $bg;
  }
}

.post-tags {
  margin-bottom: 16rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.post-tag {
  display: inline-block;
  padding: 6rpx 18rpx;
  background: $primary-light;
  color: $primary;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.post-actions {
  display: flex;
  gap: 48rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid $border;

  .action {
    display: flex;
    align-items: center;
    gap: 6rpx;
    font-size: 24rpx;
    color: $text-secondary;
    transition: all 0.2s;

    &.active {
      color: $primary;
    }
    &:active { opacity: 0.6; }
  }

  .action-count {
    font-size: 22rpx;
    color: inherit;
  }
}

.load-more {
  text-align: center;
  padding: 32rpx;
  font-size: 26rpx;
  color: $text-muted;
}
</style>
