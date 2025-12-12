<template>
  <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑联系人' : '添加联系人'"
      width="500px"
      :before-close="handleClose"
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
    >
      <!-- 头像上传 -->
      <el-form-item label="头像">
        <div style="display: flex; align-items: center; gap: 10px;">
          <el-avatar
              :size="60"
              :src="displayAvatar"
              fit="cover"
          />
          <el-upload
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
          >
            <el-button type="primary" size="small">上传头像</el-button>
          </el-upload>
          <el-button v-if="hasCustomAvatar" type="text" @click="removeAvatar">移除</el-button>
        </div>
        <div style="color: #909399; font-size: 12px; margin-top: 5px;">
          上传图片或保留默认姓名头像
        </div>
      </el-form-item>

      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入电话" />
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="地址">
        <el-input
            v-model="form.address"
            placeholder="请输入地址"
            type="textarea"
            :rows="3"
        />
      </el-form-item>

      <el-form-item label="公司">
        <el-input v-model="form.company" placeholder="请输入公司名称" />
      </el-form-item>

      <!-- 收藏状态（只读显示） -->
      <el-form-item label="收藏状态" v-if="isEdit">
        <el-switch
            v-model="form.bookmarked"
            disabled
            active-text="已收藏"
            inactive-text="未收藏"
        />
        <div style="color: #909399; font-size: 12px; margin-top: 5px;">
          收藏状态请在联系人列表中操作
        </div>
      </el-form-item>

      <el-form-item label="微信">
        <el-input
            v-model="form.wechat"
            placeholder="请输入微信账号"
            :maxlength="50"
        />
        <div style="color: #909399; font-size: 12px; margin-top: 5px;">
          可选填，最多50个字符
        </div>
      </el-form-item>

      <el-form-item label="QQ">
        <el-input
            v-model="form.qq"
            placeholder="请输入QQ号"
            :maxlength="20"
        />
        <div style="color: #909399; font-size: 12px; margin-top: 5px;">
          可选填，5-11位数字
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { contactsAPI } from '../api/contacts'

const props = defineProps({
  modelValue: Boolean,
  contact: Object,
  isEdit: Boolean
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(false)
const formRef = ref()
const loading = ref(false)
const hasCustomAvatar = ref(false)

// 表单数据 - 添加收藏字段
const form = ref({
  name: '',
  phone: '',
  email: '',
  address: '',
  company: '',
  avatar: '',
  wechat: '',
  qq: '',
  bookmarked: false  // 添加收藏字段
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 1, max: 50, message: '姓名长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { min: 6, max: 20, message: '电话长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 计算属性：显示的头像
const displayAvatar = computed(() => {
  if (form.value.avatar && form.value.avatar.startsWith('data:')) {
    return form.value.avatar
  }
  return generateAvatar(form.value.name)
})

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

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = (options) => {
  const file = options.file
  const reader = new FileReader()

  reader.onload = (e) => {
    form.value.avatar = e.target.result
    hasCustomAvatar.value = true
    ElMessage.success('头像上传成功')
  }
  reader.readAsDataURL(file)
}

// 移除头像
const removeAvatar = () => {
  form.value.avatar = ''
  hasCustomAvatar.value = false
  ElMessage.info('已移除头像')
}

// 监听对话框显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    nextTick(() => {
      if (props.isEdit && props.contact) {
        // 复制联系人数据，包括收藏状态
        form.value = {
          name: props.contact.name || '',
          phone: props.contact.phone || '',
          email: props.contact.email || '',
          address: props.contact.address || '',
          company: props.contact.company || '',
          avatar: props.contact.avatar || '',
          bookmarked: props.contact.bookmarked || false
        }
        hasCustomAvatar.value = !!(form.value.avatar && form.value.avatar.startsWith('data:'))
        console.log('编辑联系人数据:', form.value)
      } else {
        resetForm()
      }
    })
  }
})

// 监听内部dialogVisible变化
watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

// 重置表单
const resetForm = () => {
  form.value = {
    name: '',
    phone: '',
    email: '',
    address: '',
    company: '',
    avatar: '',
    bookmarked: false
  }
  hasCustomAvatar.value = false
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true

    console.log('提交的数据:', form.value)

    // 准备提交的数据
    const submitData = {
      name: form.value.name,
      phone: form.value.phone,
      email: form.value.email || null,
      address: form.value.address || null,
      company: form.value.company || null,
      wechat: form.value.wechat || null,
      qq: form.value.qq || null,
      avatar: form.value.avatar || null
      // 注意：我们不通过表单更新收藏状态，收藏状态有专门的接口
    }

    if (props.isEdit) {
      await contactsAPI.updateContact(props.contact.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await contactsAPI.addContact(submitData)
      ElMessage.success('添加成功')
    }

    emit('success')
    handleClose()
  } catch (error) {
    const errorMsg = error.response?.data?.message || error.message
    ElMessage.error((props.isEdit ? '更新失败: ' : '添加失败: ') + errorMsg)
    console.error('提交错误:', error)
  } finally {
    loading.value = false
  }
}
</script>