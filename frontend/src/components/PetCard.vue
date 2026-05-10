<template>
  <router-link
    :to="`/pet/${pet.id}`"
    class="card-playful block transition-transform hover:scale-[1.02]"
    :style="{ '--shadow-color': shadowColor }"
  >
    <div class="relative">
      <img
        :src="petImage"
        :alt="pet.name"
        class="w-full h-32 md:h-40 lg:h-48 object-cover border-b-3 border-black"
      />
      <div
        v-if="pet.status !== 1"
        class="absolute inset-0 bg-black/50 flex items-center justify-center"
      >
        <span class="bg-gray-200 text-gray-600 px-3 py-1 rounded-full border-2 border-black text-xs font-bold">
          {{ pet.status === 2 ? '已领养' : '已下架' }}
        </span>
      </div>
    </div>
    <div class="p-3 md:p-4">
      <div class="flex justify-between items-center mb-1">
        <h4 class="font-black text-sm md:text-base truncate">{{ pet.name }}</h4>
        <span
          class="px-2 py-0.5 rounded-full border-2 border-black text-[10px] font-bold"
          :class="pet.gender === '公' ? 'bg-blue-100 text-blue-600' : 'bg-pink-100 text-pink-600'"
        >
          {{ pet.gender === '公' ? '男生' : '女生' }}
        </span>
      </div>
      <p class="text-[10px] md:text-xs font-bold opacity-50 mb-2 uppercase tracking-wider">
        {{ pet.breed }} • {{ formatAge(pet.age) }}
      </p>
      <p class="text-[10px] md:text-xs font-bold opacity-60 line-clamp-2 mb-2">
        {{ truncateText(pet.personality, 30) }}
      </p>
      <div class="flex items-center gap-1 text-[10px] font-black" :class="statusColor">
        <i :class="statusIcon"></i>
        {{ statusText }}
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'
import { formatAge, truncateText, getRandomShadowColor } from '@/utils/format'

const props = defineProps({
  pet: {
    type: Object,
    required: true
  }
})

const shadowColor = getRandomShadowColor()

const petImage = computed(() => {
  if (props.pet.image) {
    return props.pet.image
  }
  return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=800'
})

const statusText = computed(() => {
  if (props.pet.status === 1) return '可领养'
  if (props.pet.status === 2) return '已领养'
  return '已下架'
})

const statusColor = computed(() => {
  if (props.pet.status === 1) return 'text-green-500'
  if (props.pet.status === 2) return 'text-gray-500'
  return 'text-red-500'
})

const statusIcon = computed(() => {
  if (props.pet.status === 1) return 'fas fa-check-circle'
  if (props.pet.status === 2) return 'fas fa-heart'
  return 'fas fa-times-circle'
})
</script>
