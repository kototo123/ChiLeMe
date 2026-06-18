<template>
  <view class="my-posts-wrapper">
    <NavBar title="我的帖子" icon="posts" showBack />
    <view class="my-posts">
    <view class="post-card" v-for="post in posts" :key="post.id" @click="goDetail(post.id)">
      <view class="post-header">
        <text class="post-content">{{ post.content?.slice(0, 100) }}{{ post.content?.length > 100 ? '...' : '' }}</text>
      </view>
      <view class="post-meta">
        <view><Icon name="liked" size="sm" color="red" /> {{ post.likeCount }}</view>
        <view><Icon name="comment" size="sm" /> {{ post.commentCount }}</view>
        <view><Icon name="starred" size="sm" color="gold" /> {{ post.favoriteCount }}</view>
      </view>
    </view>

    <view class="empty" v-if="posts.length === 0">
      <Icon name="posts" size="xl" color="#ccc" />
      <text>暂无帖子</text>
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
const posts = ref([])

onShow(async () => {
  if (userStore.userId) {
    try {
      const res = await postApi.getUserPosts(userStore.userId, 1, 50)
      posts.value = res.records || []
    } catch (e) {}
  }
})

function goDetail(id) {
  uni.navigateTo({ url: '/pages/community/detail?id=' + id })
}
</script>

<style lang="scss">
.my-posts { padding: 32rpx; }

.post-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 16rpx;
}

.post-content { font-size: 28rpx; color: #333; line-height: 1.6; display: block; margin-bottom: 16rpx; }

.post-meta {
  display: flex;
  gap: 32rpx;
  font-size: 24rpx;
  color: #999;

  view { display: flex; align-items: center; gap: 4rpx; }
}

.empty {
  text-align: center;
  padding: 80rpx 0;
  font-size: 28rpx;
  color: #999;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}
</style>
