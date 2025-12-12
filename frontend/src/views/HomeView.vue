<template>
  <div class="home-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通讯录管理系统 - 学号: 832302220/832302126</span>
          <div class="action-bar">
            <!-- 导入按钮 -->
            <el-button type="info" @click="handleImport" :icon="Upload">
              导入数据
            </el-button>
            <!-- 批量删除按钮 -->
            <el-button
                type="danger"
                @click="handleBatchDelete"
                :disabled="selectedContacts.length === 0"
                :icon="Delete"
            >
              批量删除 ({{ selectedContacts.length }})
            </el-button>
            <el-button type="success" @click="handleExport" :icon="Download">
              导出数据
            </el-button>

            <!-- 收藏筛选按钮 -->
            <el-button
                type="warning"
                @click="toggleBookmarkFilter"
                :icon="StarFilled"
                :plain="!showBookmarkedOnly"
            >
              {{ showBookmarkedOnly ? '显示全部' : '仅显示收藏' }}
              <span v-if="bookmarkedCount > 0">({{ bookmarkedCount }})</span>
            </el-button>

            <el-button type="primary" @click="handleAdd" :icon="Plus">添加联系人</el-button>

            <!-- 搜索区域 -->
            <div class="search-area">
              <el-select
                  v-model="searchField"
                  placeholder="搜索字段"
                  style="width: 120px;"
                  @change="handleSearchFieldChange"
              >
                <el-option label="全部字段" value="all" />
                <el-option label="姓名" value="name" />
                <el-option label="电话" value="phone" />
                <el-option label="邮箱" value="email" />
                <el-option label="公司" value="company" />
                <el-option label="地址" value="address" />
              </el-select>

              <el-input
                  v-model="searchKeyword"
                  :placeholder="getSearchPlaceholder()"
                  style="width: 200px; margin-left: 5px;"
                  :prefix-icon="Search"
                  @input="handleSearch"
                  @keyup.enter="handleSearch"
              />

              <el-button
                  type="primary"
                  style="margin-left: 5px;"
                  @click="handleSearch"
                  :loading="searchLoading"
              >
                搜索
              </el-button>

              <el-button
                  v-if="searchKeyword"
                  @click="clearSearch"
              >
                清除
              </el-button>
            </div>

            <!-- 调试按钮 -->
            <el-button type="warning" @click="showDebugInfo" style="margin-left: 10px;">
              调试信息
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索和收藏筛选信息 -->
      <div class="filter-info">
        <el-text v-if="isSearching" type="primary">
          <el-icon><Search /></el-icon>
          搜索 "{{ searchKeyword }}" 在 {{ getSearchFieldText() }} 中，找到 {{ filteredContacts.length }} 个结果
          <el-button type="text" @click="clearSearch" style="margin-left: 10px;">显示全部</el-button>
        </el-text>

        <el-text v-if="showBookmarkedOnly" type="warning">
          <el-icon><StarFilled /></el-icon>
          显示收藏的联系人 ({{ bookmarkedCount }} 个)
          <el-button type="text" @click="showBookmarkedOnly = false" style="margin-left: 10px;">显示全部</el-button>
        </el-text>
      </div>

      <!-- 数据统计面板 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#409EFF">
                <User />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">联系人总数</div>
                <div class="stat-value">{{ contacts.length }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#E6A23C">
                <StarFilled />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">收藏联系人</div>
                <div class="stat-value">{{ bookmarkedCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#67C23A">
                <Message />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">有邮箱的联系人</div>
                <div class="stat-value">{{ contactsWithEmail }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#E6A23C">
                <OfficeBuilding />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">有公司的联系人</div>
                <div class="stat-value">{{ contactsWithCompany }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#F56C6C">
                <Picture />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">有头像的联系人</div>
                <div class="stat-value">{{ contactsWithAvatar }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-item">
              <el-icon size="30" color="#909399">
                <Clock />
              </el-icon>
              <div class="stat-content">
                <div class="stat-label">最近更新</div>
                <div class="stat-value">{{ lastUpdatedTime }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-table
          :data="filteredContacts"
          v-loading="loading"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          empty-text="暂无联系人数据"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 收藏列 -->
        <el-table-column label="收藏" width="80">
          <template #default="scope">
            <el-button
                :type="scope.row.bookmarked ? 'warning' : 'default'"
                :icon="scope.row.bookmarked ? StarFilled : Star"
                circle
                size="small"
                @click="toggleContactBookmark(scope.row)"
                :loading="scope.row.id === togglingBookmarkId"
            />
          </template>
        </el-table-column>

        <el-table-column label="头像" width="80">
          <template #default="scope">
            <el-avatar
                :size="40"
                :src="getAvatarUrl(scope.row)"
                fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" sortable />
        <el-table-column prop="phone" label="电话" width="150" sortable />
        <el-table-column prop="email" label="邮箱" width="200">
          <template #default="scope">
            <span v-if="scope.row.email">{{ scope.row.email }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="wechat" label="微信" width="150">
          <template #default="scope">
            <span v-if="scope.row.wechat">{{ scope.row.wechat }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="qq" label="QQ" width="120">
          <template #default="scope">
            <span v-if="scope.row.qq">{{ scope.row.qq }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="address" label="地址">
          <template #default="scope">
            <span v-if="scope.row.address">{{ scope.row.address }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司" width="120">
          <template #default="scope">
            <span v-if="scope.row.company">{{ scope.row.company }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
          <template #default="scope">
            <span v-if="scope.row.createdTime">
              {{ formatDateTime(scope.row.createdTime) }}
            </span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <ContactForm
        v-model="dialogVisible"
        :contact="currentContact"
        :is-edit="isEdit"
        @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Download, Delete, User, Message, OfficeBuilding, Picture, Upload,
  Star, StarFilled, Clock
} from '@element-plus/icons-vue'
import ContactForm from '../components/ContactForm.vue'
import { contactsAPI } from '../api/contacts'
import { exportToCSV, exportToJSON } from '../utils/export'
import { parseCSV, parseJSON, validateContacts } from '../utils/import'
import axios from 'axios'

const contacts = ref([])
const loading = ref(false)
const searchLoading = ref(false)
const searchKeyword = ref('')
const searchField = ref('all')
const dialogVisible = ref(false)
const currentContact = ref(null)
const isEdit = ref(false)
const selectedContacts = ref([])
const showBookmarkedOnly = ref(false)
const togglingBookmarkId = ref(null)

// 计算属性：统计信息
const contactsWithEmail = computed(() => {
  return contacts.value.filter(contact => contact.email && contact.email.trim()).length
})

const contactsWithCompany = computed(() => {
  return contacts.value.filter(contact => contact.company && contact.company.trim()).length
})

const contactsWithAvatar = computed(() => {
  return contacts.value.filter(contact => contact.avatar && contact.avatar.startsWith('data:')).length
})

const bookmarkedCount = computed(() => {
  return contacts.value.filter(contact => contact.bookmarked).length
})

const isSearching = computed(() => {
  return searchKeyword.value.trim() !== ''
})

const filteredContacts = computed(() => {
  let result = contacts.value

  // 应用收藏筛选
  if (showBookmarkedOnly.value) {
    result = result.filter(contact => contact.bookmarked)
  }

  // 应用搜索筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    const field = searchField.value

    result = result.filter(contact => {
      if (field === 'all') {
        // 搜索所有字段
        return (
            (contact.name && contact.name.toLowerCase().includes(keyword)) ||
            (contact.phone && contact.phone.toLowerCase().includes(keyword)) ||
            (contact.email && contact.email.toLowerCase().includes(keyword)) ||
            (contact.company && contact.company.toLowerCase().includes(keyword)) ||
            (contact.address && contact.address.toLowerCase().includes(keyword))
        )
      } else {
        // 搜索指定字段
        const fieldValue = contact[field]
        return fieldValue && fieldValue.toString().toLowerCase().includes(keyword)
      }
    })
  }

  return result
})

const lastUpdatedTime = computed(() => {
  if (contacts.value.length === 0) return '无'

  const latest = contacts.value.reduce((latest, contact) => {
    if (!contact.updatedTime) return latest
    const contactTime = new Date(contact.updatedTime)
    return contactTime > latest ? contactTime : latest
  }, new Date(0))

  if (latest.getTime() === 0) return '无'

  const now = new Date()
  const diff = now - latest
  const diffMinutes = Math.floor(diff / (1000 * 60))
  const diffHours = Math.floor(diff / (1000 * 60 * 60))
  const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  return `${diffDays}天前`
})

// 获取搜索字段的显示文本
const getSearchFieldText = () => {
  const fieldMap = {
    'all': '全部字段',
    'name': '姓名',
    'phone': '电话',
    'email': '邮箱',
    'company': '公司',
    'address': '地址'
  }
  return fieldMap[searchField.value] || '全部字段'
}

// 获取搜索框占位符
const getSearchPlaceholder = () => {
  const field = getSearchFieldText()
  return `在${field}中搜索...`
}

// 获取头像URL
const getAvatarUrl = (contact) => {
  if (contact.avatar && contact.avatar.startsWith('data:')) {
    return contact.avatar
  }
  return generateAvatar(contact.name)
}

// 生成默认头像
const generateAvatar = (name) => {
  if (!name) return ''
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  const color = colors[name.length % colors.length]
  const initial = name.charAt(0).toUpperCase()

  const canvas = document.createElement('canvas')
  canvas.width = 100
  canvas.height = 100
  const ctx = canvas.getContext('2d')

  ctx.fillStyle = color
  ctx.fillRect(0, 0, 100, 100)

  ctx.fillStyle = '#FFFFFF'
  ctx.font = '40px Arial'
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(initial, 50, 50)

  return canvas.toDataURL()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''

  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 切换收藏筛选
const toggleBookmarkFilter = () => {
  showBookmarkedOnly.value = !showBookmarkedOnly.value
}

// 切换联系人收藏状态
const toggleContactBookmark = async (contact) => {
  try {
    togglingBookmarkId.value = contact.id

    // 调用API切换收藏状态
    const updatedContact = await contactsAPI.toggleBookmark(contact.id)

    // 更新本地数据
    const index = contacts.value.findIndex(c => c.id === contact.id)
    if (index !== -1) {
      contacts.value[index] = updatedContact
    }

    ElMessage.success(
        updatedContact.bookmarked ?
            '已收藏联系人' :
            '已取消收藏联系人'
    )
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  } finally {
    togglingBookmarkId.value = null
  }
}

// 显示调试信息
const showDebugInfo = () => {
  console.log('当前联系人数据:', contacts.value)
  console.log('收藏联系人:', contacts.value.filter(c => c.bookmarked))
  console.log('筛选状态:', {
    showBookmarkedOnly: showBookmarkedOnly.value,
    bookmarkedCount: bookmarkedCount.value,
    searchKeyword: searchKeyword.value,
    searchField: searchField.value
  })
  ElMessage.info('调试信息已输出到控制台，请按F12查看')
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedContacts.value = selection
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedContacts.value.length === 0) {
    ElMessage.warning('请选择要删除的联系人')
    return
  }

  try {
    await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedContacts.value.length} 个联系人吗？此操作不可恢复！`,
        '批量删除确认',
        {
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消'
        }
    )

    const deletePromises = selectedContacts.value.map(contact =>
        contactsAPI.deleteContact(contact.id)
    )

    await Promise.all(deletePromises)

    ElMessage.success(`成功删除 ${selectedContacts.value.length} 个联系人`)
    selectedContacts.value = []
    loadContacts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 搜索字段变化
const handleSearchFieldChange = () => {
  if (searchKeyword.value.trim()) {
    handleSearch()
  }
}

// 搜索联系人
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    loadContacts()
    return
  }

  searchLoading.value = true
  try {
    let response = []

    if (searchField.value === 'all') {
      response = await contactsAPI.searchContacts(keyword)
    } else {
      const allContacts = await contactsAPI.getAllContacts()
      response = allContacts.filter(contact => {
        const fieldValue = contact[searchField.value] || ''
        return fieldValue.toString().toLowerCase().includes(keyword.toLowerCase())
      })
    }

    contacts.value = response
  } catch (error) {
    ElMessage.error('搜索失败: ' + (error.response?.data?.message || error.message))
  } finally {
    searchLoading.value = false
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  searchField.value = 'all'
  loadContacts()
}

// // 导入数据
// const handleImport = () => {
//   const input = document.createElement('input')
//   input.type = 'file'
//   input.accept = '.json,.csv,.xlsx,.xls,.txt'
//
//   input.onchange = async (e) => {
//     const file = e.target.files[0]
//     if (!file) return
//
//     try {
//
//       // 检查文件类型
//       const fileName = file.name.toLowerCase()
//
//       if (fileName.endsWith('.xlsx') || fileName.endsWith('.xls')) {
//         // Excel文件处理
//         await handleExcelImport(file)
//       } else {
//         // 原有的CSV/JSON处理逻辑
//         const text = await readFileAsText(file)
//         let importedContacts = []
//
//         if (fileName.endsWith('.json')) {
//           importedContacts = parseJSON(text)
//         } else if (fileName.endsWith('.csv')) {
//           importedContacts = parseCSV(text)
//         } else {
//           if (text.trim().startsWith('[') || text.trim().startsWith('{')) {
//             importedContacts = parseJSON(text)
//           } else {
//             importedContacts = parseCSV(text)
//           }
//         }
//
//       if (importedContacts.length === 0) {
//         ElMessage.warning('文件中没有找到有效的联系人数据')
//         return
//       }
//
//       const errors = validateContacts(importedContacts)
//       if (errors.length > 0) {
//         ElMessageBox.alert(
//             `发现 ${errors.length} 个数据错误：\n\n${errors.slice(0, 10).join('\n')}${errors.length > 10 ? '\n...（只显示前10个错误）' : ''}`,
//             '数据验证失败',
//             { type: 'error' }
//         )
//         return
//       }
//
//       ElMessageBox.confirm(
//           `准备导入 ${importedContacts.length} 个联系人：\n\n` +
//           `• 姓名: ${importedContacts[0].name} - ${importedContacts[0].phone}\n` +
//           (importedContacts.length > 1 ? `• 姓名: ${importedContacts[1].name} - ${importedContacts[1].phone}\n` : '') +
//           (importedContacts.length > 2 ? `• ... 还有 ${importedContacts.length - 2} 个联系人\n` : '') +
//           `\n是否继续导入？`,
//           '导入确认',
//           {
//             type: 'info',
//             confirmButtonText: '开始导入',
//             cancelButtonText: '取消',
//             dangerouslyUseHTMLString: true
//           }
//       ).then(async () => {
//         await importContacts(importedContacts)
//       })
//
//     } catch (error) {
//       console.error('导入错误:', error)
//       ElMessage.error(`导入失败：${error.message}`)
//     }
//   }
//
//   input.click()
// }

// 导入数据
const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json,.csv,.xlsx,.xls,.txt'

  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    try {
      // 检查文件类型
      const fileName = file.name.toLowerCase()

      if (fileName.endsWith('.xlsx') || fileName.endsWith('.xls')) {
        // Excel文件处理
        await handleExcelImport(file)
      } else {
        // 原有的CSV/JSON处理逻辑
        const text = await readFileAsText(file)
        let importedContacts = []

        if (fileName.endsWith('.json')) {
          importedContacts = parseJSON(text)
        } else if (fileName.endsWith('.csv')) {
          importedContacts = parseCSV(text)
        } else {
          if (text.trim().startsWith('[') || text.trim().startsWith('{')) {
            importedContacts = parseJSON(text)
          } else {
            importedContacts = parseCSV(text)
          }
        }

        if (importedContacts.length === 0) {
          ElMessage.warning('文件中没有找到有效的联系人数据')
          return
        }

        const errors = validateContacts(importedContacts)
        if (errors.length > 0) {
          ElMessageBox.alert(
              `发现 ${errors.length} 个数据错误：\n\n${errors.slice(0, 10).join('\n')}${errors.length > 10 ? '\n...（只显示前10个错误）' : ''}`,
              '数据验证失败',
              { type: 'error' }
          )
          return
        }

        await ElMessageBox.confirm(
            `准备导入 ${importedContacts.length} 个联系人：\n\n` +
            `• 姓名: ${importedContacts[0].name} - ${importedContacts[0].phone}\n` +
            (importedContacts.length > 1 ? `• 姓名: ${importedContacts[1].name} - ${importedContacts[1].phone}\n` : '') +
            (importedContacts.length > 2 ? `• ... 还有 ${importedContacts.length - 2} 个联系人\n` : '') +
            `\n是否继续导入？`,
            '导入确认',
            {
              type: 'info',
              confirmButtonText: '开始导入',
              cancelButtonText: '取消',
              dangerouslyUseHTMLString: true
            }
        )

        await importContacts(importedContacts)
      } // 关键：补全else的闭合大括号
    } catch (error) { // 现在catch正确配对try
      console.error('导入错误:', error)
      ElMessage.error(`导入失败：${error.message}`)
    } // 补全input.onchange的try/catch闭合
  } // 补全input.onchange的闭合

  input.click()
}

  const handleExcelImport = async (file) => {
    loading.value = true

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await axios.post('/api/contacts/import/excel', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
      })

      if (response.data.includes('成功导入')) {
        if (response.data.includes('失败')) {
          // 显示详细的导入结果
          ElMessageBox.alert(response.data, '导入结果', {
            type: 'warning',
            showClose: false
          })
        } else {
          ElMessage.success(response.data)
        }
        loadContacts()
      } else {
        ElMessage.warning(response.data)
      }
    } catch (error) {
      console.error('Excel导入错误:', error)
      ElMessage.error('Excel导入失败: ' + (error.response?.data || error.message))
    } finally {
      loading.value = false
    }
  }

// 读取文件内容
const readFileAsText = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => resolve(e.target.result)
    reader.onerror = (e) => reject(new Error('文件读取失败'))
    reader.readAsText(file, 'UTF-8')
  })
}

// 执行导入操作
const importContacts = async (contactsToImport) => {
  loading.value = true

  try {
    let successCount = 0
    let errorCount = 0
    const errors = []

    for (let i = 0; i < contactsToImport.length; i++) {
      const contact = contactsToImport[i]
      try {
        await contactsAPI.addContact(contact)
        successCount++

        if (successCount % 10 === 0) {
          ElMessage.info(`已导入 ${successCount}/${contactsToImport.length} 个联系人...`)
        }
      } catch (error) {
        errorCount++
        errors.push(`第 ${i + 1} 行: ${contact.name} - ${error.response?.data?.message || error.message}`)

        if (errorCount > 10) {
          errors.push('... 错误过多，已停止导入')
          break
        }
      }
    }

    let message = `导入完成！成功: ${successCount}，失败: ${errorCount}`
    if (errors.length > 0) {
      ElMessageBox.alert(
          `${message}\n\n失败详情：\n${errors.slice(0, 10).join('\n')}`,
          '导入结果',
          { type: errorCount > 0 ? 'warning' : 'success' }
      )
    } else {
      ElMessage.success(message)
    }

    loadContacts()

  } catch (error) {
    ElMessage.error('导入过程发生错误: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 导出数据
const handleExport = () => {
  if (!contacts.value || contacts.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  ElMessageBox.confirm('请选择导出格式', '导出数据', {
    confirmButtonText: 'Excel格式',
    cancelButtonText: 'CSV/JSON格式',
    type: 'info'
  }).then(async () => {
    // Excel导出
    try {
      const response = await axios.get('/api/contacts/export/excel', {
        responseType: 'blob',
        baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
      })

      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      const filename = response.headers['content-disposition']
          ? response.headers['content-disposition'].split('filename=')[1]
          : `contacts_${new Date().toISOString().split('T')[0]}.xlsx`

      link.href = url
      link.setAttribute('download', filename.replace(/"/g, ''))
      document.body.appendChild(link)
      link.click()
      link.remove()

      ElMessage.success('Excel导出成功')
    } catch (error) {
      ElMessage.error('Excel导出失败: ' + (error.response?.data?.message || error.message))
    }
  }).catch((action) => {
    if (action !== 'cancel') {
      // 原有的CSV/JSON导出逻辑
      ElMessageBox.confirm('请选择格式', '导出数据', {
        confirmButtonText: 'CSV格式',
        cancelButtonText: 'JSON格式',
        type: 'info'
      }).then(() => {
        exportToCSV(contacts.value, `contacts_${new Date().toISOString().split('T')[0]}.csv`)
        ElMessage.success('CSV导出成功')
      }).catch((action2) => {
        if (action2 !== 'cancel') {
          exportToJSON(contacts.value, `contacts_${new Date().toISOString().split('T')[0]}.json`)
          ElMessage.success('JSON导出成功')
        }
      })
    }
  })
}

// 加载联系人列表
const loadContacts = async () => {
  loading.value = true
  try {
    const response = await contactsAPI.getAllContacts()
    contacts.value = response
  } catch (error) {
    ElMessage.error('加载联系人失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 添加联系人
const handleAdd = () => {
  currentContact.value = null
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑联系人
const handleEdit = (contact) => {
  currentContact.value = { ...contact }
  isEdit.value = true
  dialogVisible.value = true
}

// 删除联系人
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个联系人吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    await contactsAPI.deleteContact(id)
    ElMessage.success('删除成功')
    loadContacts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}





// 表单提交成功
const handleFormSuccess = () => {
  loadContacts()
  dialogVisible.value = false
}

onMounted(() => {
  loadContacts()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 10px;
}

.action-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.search-area {
  display: flex;
  align-items: center;
}

.filter-info {
  margin-bottom: 15px;
  padding: 10px;
  background: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-info .el-text {
  display: flex;
  align-items: center;
}

.box-card {
  width: 100%;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-content {
  margin-left: 10px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }

  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-area {
    justify-content: center;
    margin-top: 10px;
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 10px;
  }

  .search-area {
    flex-direction: column;
    gap: 10px;
  }

  .search-area .el-select,
  .search-area .el-input {
    width: 100% !important;
    margin-left: 0 !important;
  }

  .el-col {
    margin-bottom: 10px;
  }
}
</style>
