<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">硅谷进销存登录</h2>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: 'admin', password: '123456' })
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await request.post('/login', form.value)
    if (res.data.code === 200) {
      ElMessage.success('登录成功')
      sessionStorage.setItem('user', JSON.stringify(res.data.data))
      router.push('/')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    // 错误处理已在拦截器中完成，这里可以根据需要做额外处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
.login-title {
  text-align: center;
  margin: 0;
}
</style>
