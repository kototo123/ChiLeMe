<template>
  <view class="detail-wrapper">
    <NavBar title="帖子详情" icon="comment" showBack />
    <view class="detail">
    <view class="post-section" v-if="post">
      <view class="post-user">
        <image :src="post.userAvatar || '/static/images/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
        <view>
          <text class="user-name">{{ post.userNickname }}</text>
          <text class="post-time">{{ post.createdAt }}</text>
        </view>
      </view>

      <text class="post-content">{{ post.content }}</text>

      <view class="post-images" v-if="post.images && post.images.length > 0">
        <image v-for="(img, idx) in post.images" :key="idx" :src="img" mode="aspectFill" class="detail-image"></image>
      </view>

      <view class="post-tags" v-if="post.tags">
        <text class="tag" v-for="tag in post.tags" :key="tag">{{ tag }}</text>
      </view>

      <view class="post-actions-bar">
        <view class="action" :class="{ active: post.liked }" @click="toggleLike">
          <Icon :name="post.liked ? 'liked' : 'like'" size="sm" />
          <text>{{ post.likeCount }}</text>
        </view>
        <view class="action">
          <Icon name="comment" size="sm" />
          <text>{{ post.commentCount }}</text>
        </view>
        <view class="action" :class="{ active: post.favorited }" @click="toggleFavorite">
          <Icon :name="post.favorited ? 'starred' : 'star'" size="sm" />
          <text>{{ post.favoriteCount }}</text>
        </view>
      </view>
    </view>

    <view class="comment-section">
      <view class="section-title">评论 ({{ post?.commentCount || 0 }})</view>

      <view class="comment-input-area">
        <input v-model="commentText" class="comment-input" placeholder="写下你的评论..." />
        <button class="send-btn" @click="addComment"><Icon name="pen" size="sm" /> 发送</button>
      </view>

      <view class="comment-list">
        <view class="comment-item" v-for="c in comments" :key="c.id">
          <text class="comment-user">{{ c.userNickname || '用户' }}</text>
          <text class="comment-content">{{ c.content }}</text>
          <text class="comment-time">{{ c.createdAt }}</text>
        </view>
      </view>
    </view>
  </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { postApi } from '@/api/post'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const userStore = useUserStore()
const postId = ref('')
const post = ref(null)
const comments = ref([])
const commentText = ref('')

onLoad((query) => {
  postId.value = query.id
  loadDetail()
  loadComments()
})

async function loadDetail() {
  try {
    post.value = await postApi.getDetail(postId.value)
  } catch (e) {}
}

async function loadComments() {
  try {
    const res = await postApi.getComments(postId.value, 1, 50)
    comments.value = res.records || []
  } catch (e) {}
}

async function toggleLike() {
  try {
    if (post.value.liked) {
      await postApi.unlike(post.value.id)
      post.value.liked = false
      post.value.likeCount--
    } else {
      await postApi.like(post.value.id)
      post.value.liked = true
      post.value.likeCount++
    }
  } catch (e) {}
}

async function toggleFavorite() {
  try {
    if (post.value.favorited) {
      await postApi.unfavorite(post.value.id)
      post.value.favorited = false
      post.value.favoriteCount--
    } else {
      await postApi.favorite(post.value.id)
      post.value.favorited = true
      post.value.favoriteCount++
    }
  } catch (e) {}
}

async function addComment() {
  if (!commentText.value.trim()) return
  try {
    await postApi.addComment(postId.value, commentText.value)
    commentText.value = ''
    uni.showToast({ title: '评论成功', icon: 'success' })
    loadComments()
    if (post.value) post.value.commentCount++
  } catch (e) {}
}
</script>

<style lang="scss">
.detail-wrapper { background: #F0F7FF; min-height: 100vh; }
.detail { padding-bottom: 40rpx; }

.post-section {
  background: #fff;
  padding: 32rpx;
  margin-bottom: 16rpx;
}

.post-user {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;

  .user-avatar {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    margin-right: 16rpx;
  }

  .user-name { font-size: 30rpx; color: #333; font-weight: 500; display: block; }
  .post-time { font-size: 22rpx; color: #999; }
}

.post-content { font-size: 30rpx; color: #333; line-height: 1.7; display: block; margin-bottom: 20rpx; }

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-bottom: 20rpx;

  .detail-image {
    width: 30%;
    height: 220rpx;
    border-radius: 8rpx;
  }
}

.post-tags { margin-bottom: 20rpx; }

.tag {
  display: inline-block;
  padding: 6rpx 20rpx;
  background: #E8F0FE;
  color: #5B9BD5;
  border-radius: 20rpx;
  font-size: 22rpx;
  margin-right: 8rpx;
}

.post-actions-bar {
  display: flex;
  gap: 60rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid #f0f0f0;
}

.action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #999;
  transition: color 0.2s;

  &.active { color: #5B9BD5; }
  &:active { opacity: 0.6; }
}

.comment-section {
  background: #fff;
  padding: 32rpx;
}

.section-title { font-size: 30rpx; font-weight: 500; color: #333; margin-bottom: 24rpx; }

.comment-input-area {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;

  .comment-input {
    flex: 1;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 24rpx;
    font-size: 26rpx;
  }

  .send-btn {
    margin-left: 16rpx;
    padding: 12rpx 32rpx;
    background: #5B9BD5;
    color: #fff;
    border-radius: 36rpx;
    font-size: 26rpx;
    border: none;
  }
}

.comment-item {
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
}

.comment-user { font-size: 26rpx; color: #5B9BD5; font-weight: 500; display: block; margin-bottom: 8rpx; }
.comment-content { font-size: 28rpx; color: #333; display: block; margin-bottom: 8rpx; }
.comment-time { font-size: 22rpx; color: #999; }
</style>
