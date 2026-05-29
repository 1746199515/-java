<template>
  <div class="page-container">
    <div class="toolbar">
      <el-button type="primary" @click="showCreateDialog">新建订单</el-button>
      <el-button type="success" @click="exportOrders" :loading="downloadLoading">导出报表</el-button>
      <el-input v-model="searchNo" placeholder="搜索订单号" style="width: 200px; margin-left: 10px" @input="fetchData" />
    </div>

    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column type="expand">
        <template #default="props">
          <el-table :data="props.row.items" border size="small">
            <el-table-column label="商品ID" prop="productId" />
            <el-table-column label="单价" prop="price" />
            <el-table-column label="数量" prop="quantity" />
            <el-table-column label="小计" prop="amount" />
          </el-table>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="customerName" label="客户" />
      <el-table-column prop="totalAmount" label="总金额" />
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="status" label="状态" width="150">
        <template #default="scope">
          <el-select
            v-model="scope.row.status"
            size="small"
            @change="(val) => handleStatusChange(scope.row.id, val)"
          >
            <el-option label="待处理" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 20px; text-align: right">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- Create Order Dialog -->
    <el-dialog v-model="dialogVisible" title="新建订单" width="70%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户">
          <el-select v-model="form.customerId" placeholder="选择客户">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-divider>订单明细</el-divider>
        <el-button type="success" size="small" @click="addItem" style="margin-bottom: 10px">添加商品</el-button>

        <el-table :data="form.items" border>
          <el-table-column label="商品">
            <template #default="scope">
              <el-autocomplete
                v-model="scope.row.productName"
                :fetch-suggestions="querySearch"
                placeholder="输入名称搜索"
                @select="(item) => handleSelectProduct(item, scope.row)"
                @input="scope.row.productId = null"
                style="width: 100%"
              >
                <template #default="{ item }">
                  <div style="display: flex; justify-content: space-between">
                    <span>{{ item.name }}</span>
                    <span style="color: #999; font-size: 12px">库存: {{ item.stock }}</span>
                  </div>
                </template>
              </el-autocomplete>
            </template>
          </el-table-column>
          <el-table-column label="单价">
            <template #default="scope">￥{{ scope.row.price || 0 }}</template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.maxStock" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button type="danger" icon="Delete" circle size="small" @click="removeItem(scope.$index)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitting">确认订单</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const customers = ref([])
const loading = ref(false)
const downloadLoading = ref(false)
const submitting = ref(false)
const searchNo = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({ customerId: null, items: [] })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/orders', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        orderNo: searchNo.value
      }
    })
    tableData.value = res.data.data.records
    total.value = res.data.data.total
  } finally {
    loading.value = false
  }
}

const exportOrders = async () => {
  downloadLoading.value = true
  try {
    const res = await request.get('/excel/orders/export', {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '销售订单报表.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    downloadLoading.value = false
  }
}

const fetchResources = async () => {
  const cRes = await request.get('/customers')
  customers.value = cRes.data.data
}

const querySearch = async (queryString, cb) => {
  try {
    const res = await request.get('/products', {
      params: {
        page: 1,
        size: 50,
        name: queryString
      }
    })
    const list = res.data.data.records.map(p => ({
      value: p.name, // el-autocomplete displays this property
      ...p
    }))
    cb(list)
  } catch (error) {
    console.error(error)
    cb([])
  }
}

const showCreateDialog = () => {
  form.value = { customerId: null, items: [] }
  dialogVisible.value = true
  fetchResources()
}

const addItem = () => {
  form.value.items.push({ productId: null, productName: '', price: 0, quantity: 1, maxStock: 999 })
}

const removeItem = (index) => {
  form.value.items.splice(index, 1)
}

const handleSelectProduct = (item, row) => {
  row.productId = item.id
  row.productName = item.name
  row.price = item.price
  row.maxStock = item.stock
  row.quantity = 1
}

const handleStatusChange = async (id, status) => {
  try {
    await request.put(`/orders/${id}/status`, null, {
      params: { status }
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
    fetchData() // Revert change on error
  }
}

const submitOrder = async () => {
  if (!form.value.customerId) return ElMessage.warning('请选择客户')
  if (form.value.items.length === 0) return ElMessage.warning('请添加商品')

  // Validate products
  for (const item of form.value.items) {
    if (!item.productId) return ElMessage.warning('请选择有效的商品')
  }

  submitting.value = true
  try {
    await request.post('/orders', form.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    currentPage.value = 1 // Reset to first page to show new order
    fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.page-container { padding: 20px; }
.toolbar { margin-bottom: 20px; }
</style>
