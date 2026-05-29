<template>
  <div class="page-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">添加分类</el-button>
    </div>
    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="scope">
           <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
           <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑' : '添加'">
      <el-form :model="form">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
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
import { ElMessage } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const fetchData = async () => {
  const res = await request.get('/categories')
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
  await request.delete(`/categories/${id}`)
  fetchData()
}

const submit = async () => {
  if (isEdit.value) await request.put('/categories', form.value)
  else await request.post('/categories', form.value)
  dialogVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>
<style scoped> .page-container { padding: 20px; } .toolbar { margin-bottom: 20px; } </style>
