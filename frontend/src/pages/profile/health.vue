<template>
  <view class="health-wrapper">
    <NavBar title="健康档案" icon="health" showBack />
    <view class="health-page">
    <view class="form-card">
      <view class="form-group">
        <view class="label"><Icon name="target" size="sm" /> 身高 (cm)</view>
        <input v-model="height" type="digit" class="input" placeholder="输入身高" />
      </view>
      <view class="form-group">
        <view class="label"><Icon name="health" size="sm" /> 体重 (kg)</view>
        <input v-model="weight" type="digit" class="input" placeholder="输入体重" />
      </view>
      <button class="save-btn" @click="saveRecord"><Icon name="check" size="sm" /> 保存记录</button>
    </view>

    <view class="records-section" v-if="records.length > 0">
      <view class="section-title"><Icon name="calendar" size="sm" /> 历史记录</view>
      <view class="record-item" v-for="r in records" :key="r.id">
        <view class="record-info">
          <text class="record-date">{{ r.recordDate }}</text>
          <text class="record-data">{{ r.weight }}kg</text>
          <text class="record-data" v-if="r.bmi">BMI {{ r.bmi }}</text>
        </view>
        <text class="ai-advice" v-if="r.aiAdvice"><Icon name="ai" size="sm" /> {{ r.aiAdvice }}</text>
      </view>
    </view>
  </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { healthApi } from '@/api/health'
import Icon from '@/components/Icon.vue'
import NavBar from '@/components/NavBar.vue'

const height = ref('')
const weight = ref('')
const records = ref([])

onShow(() => loadRecords())

async function loadRecords() {
  try { records.value = await healthApi.getRecords() || [] } catch (e) {}
}

async function saveRecord() {
  if (!weight.value) return uni.showToast({ title: '请输入体重', icon: 'none' })
  try {
    await healthApi.addRecord({
      height: height.value ? parseFloat(height.value) : null,
      weight: parseFloat(weight.value),
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
    height.value = ''
    weight.value = ''
    loadRecords()
  } catch (e) {}
}
</script>

<style lang="scss">
.health-page { padding: 32rpx; }

.form-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.form-group { margin-bottom: 24rpx; }

.label { font-size: 28rpx; color: #333; display: block; margin-bottom: 12rpx; }

.input {
  height: 72rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.save-btn {
  width: 100%;
  height: 80rpx;
  background: linear-gradient(135deg, #5B9BD5, #4A8BC2);
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
  border: none;
  margin-top: 16rpx;
}

.section-title { font-size: 30rpx; font-weight: 500; color: #333; margin-bottom: 20rpx; display: block; }

.record-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}

.record-info { display: flex; gap: 24rpx; align-items: center; margin-bottom: 12rpx; }

.record-date { font-size: 26rpx; color: #999; }

.record-data { font-size: 28rpx; color: #333; font-weight: 500; }

.ai-advice { font-size: 24rpx; color: #5B9BD5; display: block; }
</style>
