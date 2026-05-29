<template>
  <div class="page-container">
    <h1>仪表盘</h1>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>商品总数</template>
          <div class="stat-num">{{ stats.productCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>今日订单</template>
          <div class="stat-num">{{ stats.todayOrderCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>总销售额</template>
          <div class="stat-num">￥{{ stats.totalSales || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({})

const fetchStats = async () => {
  const res = await request.get('/dashboard/stats')
  if (res.data.code === 200) {
    stats.value = res.data.data
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.page-container { padding: 20px; }
.stat-num { font-size: 24px; font-weight: bold; color: #409EFF; }
</style>
