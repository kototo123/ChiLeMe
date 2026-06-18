<template>
  <text class="icon" :class="[size, color]" :style="{ fontSize: sizeMap[size] }">{{ iconMap[name] }}</text>
</template>

<script setup>
const props = defineProps({
  name: { type: String, default: '' },
  size: { type: String, default: 'md' },
  color: { type: String, default: '' }
})

const sizeMap = { sm: '24rpx', md: '32rpx', lg: '48rpx', xl: '64rpx' }

const iconMap = {
  home: '🏠', community: '🌐', checkin: '☕', rank: '🏆', profile: '👤',
  like: '♡', liked: '♥', comment: '💬', star: '☆', starred: '★',
  camera: '📷', calendar: '📅', health: '❤️', posts: '📝', settings: '⚙',
  notification: '🔔', logout: '🚪', add: '+', close: '✕', check: '✓',
  warning: '⚠', time: '⏰', breakfast: '🥐', ontime: '✅', late: '⏳',
  breakcard: '🔄', medal: '🥇', medalsilver: '🥈', medalbronze: '🥉',
  food: '🍽', streak: '🔥', score: '⭐', ai: '💡', arrow: '›',
  arrowleft: '‹', edit: '✎', search: '🔍', location: '📍',
  gift: '🎁', crown: '👑', starfilled: '🌟', clock: '🕐',
  info: 'ℹ️', forward: '▶', back: '◀', up: '▲', down: '▼',
  heart: '💖', cup: '🏅', target: '🎯', sun: '☀️', moon: '🌙',
  book: '📖', pen: '✏', trash: '🗑', email: '📧', phone: '📞',
  friends: '👥', photo: '🖼', music: '🎵', fire: '🔥', thumbsup: '👍'
}
</script>

<style scoped>
.icon { display: inline-flex; align-items: center; justify-content: center; line-height: 1; }
</style>
