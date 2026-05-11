<template>
  <div class="space-y-4">
    <!-- 移动端筛选器 -->
    <div class="lg:hidden">
      <div class="grid grid-cols-2 gap-3 mb-4">
        <div
          @click="showTypeDropdown = !showTypeDropdown"
          class="bg-white border-3 border-black p-3 rounded-xl font-bold flex justify-between items-center text-xs cursor-pointer relative"
        >
          <span>类型: {{ selectedTypeLabel }}</span>
          <i class="fas fa-chevron-down"></i>
          <div
            v-if="showTypeDropdown"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-10"
          >
            <div
              v-for="type in petTypes"
              :key="type.value"
              @click.stop="selectType(type.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-gray-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-50 text-pink-500': filters.type === type.value }"
            >
              {{ type.label }}
            </div>
          </div>
        </div>
        <div
          @click="showSortDropdown = !showSortDropdown"
          class="bg-white border-3 border-black p-3 rounded-xl font-bold flex justify-between items-center text-xs cursor-pointer relative"
        >
          <span>排序: {{ selectedSortLabel }}</span>
          <i class="fas fa-chevron-down"></i>
          <div
            v-if="showSortDropdown"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-10"
          >
            <div
              v-for="sort in sortOptions"
              :key="sort.value"
              @click.stop="selectSort(sort.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-gray-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-50 text-pink-500': filters.sort === sort.value }"
            >
              {{ sort.label }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- PC端筛选器 -->
    <div class="hidden lg:block bg-white border-4 border-black rounded-3xl p-6 shadow-playful">
      <div class="grid grid-cols-6 gap-4">
        <!-- 类型 -->
        <div class="relative" ref="typeDropdownRef">
          <label class="block font-black text-sm mb-2">宠物类型</label>
          <div
            @click="toggleDropdown('type')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedTypeLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'type'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              v-for="type in petTypes"
              :key="type.value"
              @click="selectTypePC(type.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.type === type.value }"
            >
              {{ type.label }}
            </div>
          </div>
        </div>

        <!-- 品种 -->
        <div class="relative" ref="breedDropdownRef">
          <label class="block font-black text-sm mb-2">品种</label>
          <div
            @click="toggleDropdown('breed')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedBreedLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'breed'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              @click="selectBreed('')"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg"
              :class="{ 'bg-pink-100 text-pink-500': !filters.breed }"
            >
              全部品种
            </div>
            <div
              v-for="breed in breeds"
              :key="breed.id"
              @click="selectBreed(breed.breedName)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.breed === breed.breedName }"
            >
              {{ breed.breedName }}
            </div>
          </div>
        </div>

        <!-- 年龄 -->
        <div class="relative" ref="ageDropdownRef">
          <label class="block font-black text-sm mb-2">年龄</label>
          <div
            @click="toggleDropdown('age')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedAgeLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'age'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              v-for="age in ageRanges"
              :key="age.value"
              @click="selectAge(age.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.ageRange === age.value }"
            >
              {{ age.label }}
            </div>
          </div>
        </div>

        <!-- 性别 -->
        <div class="relative" ref="genderDropdownRef">
          <label class="block font-black text-sm mb-2">性别</label>
          <div
            @click="toggleDropdown('gender')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedGenderLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'gender'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              v-for="g in genders"
              :key="g.value"
              @click="selectGender(g.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.gender === g.value }"
            >
              {{ g.label }}
            </div>
          </div>
        </div>

        <!-- 体型 -->
        <div class="relative" ref="sizeDropdownRef">
          <label class="block font-black text-sm mb-2">体型</label>
          <div
            @click="toggleDropdown('size')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedSizeLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'size'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              v-for="s in sizes"
              :key="s.value"
              @click="selectSize(s.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.size === s.value }"
            >
              {{ s.label }}
            </div>
          </div>
        </div>

        <!-- 排序 -->
        <div class="relative" ref="sortDropdownRef">
          <label class="block font-black text-sm mb-2">排序</label>
          <div
            @click="toggleDropdown('sortPC')"
            class="input-playful text-sm cursor-pointer flex justify-between items-center"
          >
            <span>{{ selectedSortLabel }}</span>
            <i class="fas fa-chevron-down text-xs"></i>
          </div>
          <div
            v-if="openDropdown === 'sortPC'"
            class="absolute top-full left-0 right-0 mt-2 bg-white border-3 border-black rounded-xl shadow-playful z-20 max-h-48 overflow-y-auto"
          >
            <div
              v-for="sort in sortOptions"
              :key="sort.value"
              @click="selectSortPC(sort.value)"
              class="px-4 py-2 font-bold text-sm hover:bg-pink-50 cursor-pointer first:rounded-t-lg last:rounded-b-lg"
              :class="{ 'bg-pink-100 text-pink-500': filters.sort === sort.value }"
            >
              {{ sort.label }}
            </div>
          </div>
        </div>
      </div>

      <!-- 重置按钮 -->
      <div class="mt-4 flex justify-end">
        <button
          @click="resetFilters"
          class="px-4 py-2 border-2 border-black rounded-xl font-bold text-sm hover:bg-gray-50 transition-colors"
        >
          <i class="fas fa-redo mr-2"></i>重置筛选
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { breedApi } from '@/api/breed'

const props = defineProps({
  initialType: {
    type: String,
    default: ''
  },
  initialKeyword: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['filter'])

const filters = ref({
  type: '',
  breed: '',
  ageRange: '',
  gender: '',
  size: '',
  sort: 'latest',
  keyword: ''
})

const breeds = ref([])
const showTypeDropdown = ref(false)
const showSortDropdown = ref(false)
const openDropdown = ref('')

const petTypes = [
  { label: '全部', value: '' },
  { label: '猫', value: '猫' },
  { label: '狗', value: '狗' },
  { label: '其他', value: '其他' }
]

const ageRanges = [
  { label: '全部年龄', value: '' },
  { label: '幼年(0-1岁)', value: '0-12' },
  { label: '成年(1-7岁)', value: '12-84' },
  { label: '老年(7岁以上)', value: '84-999' }
]

const genders = [
  { label: '全部', value: '' },
  { label: '公', value: '公' },
  { label: '母', value: '母' }
]

const sizes = [
  { label: '全部', value: '' },
  { label: '小型', value: '小型' },
  { label: '中型', value: '中型' },
  { label: '大型', value: '大型' }
]

const sortOptions = [
  { label: '最新上架', value: 'latest' },
  { label: '年龄升序', value: 'age_asc' },
  { label: '年龄降序', value: 'age_desc' }
]

const selectedTypeLabel = computed(() => {
  return petTypes.find(t => t.value === filters.value.type)?.label || '全部'
})

const selectedBreedLabel = computed(() => {
  return filters.value.breed || '全部品种'
})

const selectedAgeLabel = computed(() => {
  return ageRanges.find(a => a.value === filters.value.ageRange)?.label || '全部年龄'
})

const selectedGenderLabel = computed(() => {
  return genders.find(g => g.value === filters.value.gender)?.label || '全部'
})

const selectedSizeLabel = computed(() => {
  return sizes.find(s => s.value === filters.value.size)?.label || '全部'
})

const selectedSortLabel = computed(() => {
  return sortOptions.find(s => s.value === filters.value.sort)?.label || '最新'
})

const toggleDropdown = (name) => {
  openDropdown.value = openDropdown.value === name ? '' : name
}

const loadBreeds = async () => {
  try {
    const res = await breedApi.getList(filters.value.type || undefined)
    if (res.code === 200 && res.data && res.data.length > 0) {
      // 后端返回 [{petType, breeds: [字符串数组]}]，需要转换为 [{breedName}] 格式
      const breedList = res.data[0].breeds || []
      breeds.value = breedList.map(name => ({ breedName: name }))
    } else {
      breeds.value = []
    }
  } catch (e) {
    console.error('Failed to load breeds:', e)
  }
}

const selectType = (value) => {
  filters.value.type = value
  filters.value.breed = ''
  showTypeDropdown.value = false
  loadBreeds()
  onFilterChange()
}

const selectTypePC = (value) => {
  filters.value.type = value
  filters.value.breed = ''
  openDropdown.value = ''
  loadBreeds()
  onFilterChange()
}

const selectBreed = (value) => {
  filters.value.breed = value
  openDropdown.value = ''
  onFilterChange()
}

const selectAge = (value) => {
  filters.value.ageRange = value
  openDropdown.value = ''
  onFilterChange()
}

const selectGender = (value) => {
  filters.value.gender = value
  openDropdown.value = ''
  onFilterChange()
}

const selectSize = (value) => {
  filters.value.size = value
  openDropdown.value = ''
  onFilterChange()
}

const selectSort = (value) => {
  filters.value.sort = value
  showSortDropdown.value = false
  onFilterChange()
}

const selectSortPC = (value) => {
  filters.value.sort = value
  openDropdown.value = ''
  onFilterChange()
}

const onFilterChange = () => {
  emit('filter', { ...filters.value })
}

const resetFilters = () => {
  filters.value = {
    type: '',
    breed: '',
    ageRange: '',
    gender: '',
    size: '',
    sort: 'latest',
    keyword: ''
  }
  breeds.value = []
  onFilterChange()
}

// 初始化时应用外部传入的参数
const initFilters = () => {
  if (props.initialType) {
    filters.value.type = props.initialType
    loadBreeds()
  }
  if (props.initialKeyword) {
    filters.value.keyword = props.initialKeyword
  }
}

watch(() => filters.value.type, () => {
  if (filters.value.type) {
    loadBreeds()
  } else {
    breeds.value = []
  }
})

// 监听外部参数变化
watch(() => props.initialType, (newVal) => {
  if (newVal !== filters.value.type) {
    filters.value.type = newVal
    filters.value.breed = ''
    if (newVal) {
      loadBreeds()
    }
  }
})

watch(() => props.initialKeyword, (newVal) => {
  if (newVal !== filters.value.keyword) {
    filters.value.keyword = newVal
  }
})

// 点击外部关闭下拉框
const handleClickOutside = (e) => {
  if (!e.target.closest('.relative')) {
    showTypeDropdown.value = false
    showSortDropdown.value = false
    openDropdown.value = ''
  }
}

onMounted(() => {
  initFilters()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 暴露方法给父组件
defineExpose({
  resetFilters
})
</script>
