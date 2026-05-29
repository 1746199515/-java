<template>
  <div class="page-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">添加客户</el-button>
    </div>
    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="操作">
        <template #default="scope">
           <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
           <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑' : '添加'">
      <el-form :model="form">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const fetchData = async () => {
  const res = await request.get('/customers')
  tableData.value = res.data.data
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await request.delete(`/customers/${id}`)
  fetchData()
}

const submit = async () => {
  if (isEdit.value) await request.put('/customers', form.value)
  else await request.post('/customers', form.value)
  dialogVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>
<style scoped> .page-container { padding: 20px; } .toolbar { margin-bottom: 20px; } </style>
