<template>
  <div class="p-6 bg-white shadow-lg rounded-xl border border-gray-100">
    <h2 class="text-2xl font-semibold text-gray-800 mb-6 border-b pb-3">文件上传</h2>
    
    <form @submit.prevent="handleUpload" class="space-y-4">
      <!-- 文件选择区域 -->
      <div class="flex flex-col space-y-2">
        <label for="file-input" class="text-sm font-medium text-gray-700">选择文件</label>
        <input 
          type="file" 
          id="file-input" 
          ref="fileInputRef"
          @change="onFileChange"
          class="block w-full text-sm text-gray-500
            file:mr-4 file:py-2 file:px-4
            file:rounded-full file:border-0
            file:text-sm file:font-semibold
            file:bg-blue-50 file:text-blue-700
            hover:file:bg-blue-100"
          required
        />
        <p v-if="selectedFile" class="text-xs text-gray-500 mt-1">
          已选择: {{ selectedFile.name }} ({{ formatBytes(selectedFile.size) }})
        </p>
      </div>

      <!-- 描述输入区域 -->
      <div class="flex flex-col space-y-2">
        <label for="description" class="text-sm font-medium text-gray-700">文件描述 (可选)</label>
        <textarea
          id="description"
          v-model="description"
          rows="2"
          class="p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 transition duration-150 resize-none"
          placeholder="输入文件描述..."
        ></textarea>
      </div>

      <!-- 提交按钮 -->
      <button 
        type="submit" 
        :disabled="!selectedFile || fileStore.isStoreLoading"
        class="w-full py-3 px-4 text-white font-bold rounded-xl transition duration-200 
               "
        :class="[!selectedFile || fileStore.isStoreLoading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700']"
      >
        <span v-if="fileStore.isStoreLoading">
          <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
          正在上传...
        </span>
        <span v-else>立即上传</span>
      </button>

      <!-- 状态消息 -->
      <div v-if="successMessage" class="text-sm text-green-600 bg-green-50 p-3 rounded-lg border border-green-200">
        {{ successMessage }}
      </div>
      <div v-if="fileStore.error" class="text-sm text-red-600 bg-red-50 p-3 rounded-lg border border-red-200">
        上传失败: {{ fileStore.error }}
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { FileStore } from '@/stores/file'; // 导入 Pinia Store

const fileStore = FileStore();

// 响应式数据
const selectedFile = ref(null);
const description = ref('');
const fileInputRef = ref(null);
const successMessage = ref('');

/**
 * 文件输入框内容改变时触发
 * @param {Event} event 
 */
const onFileChange = (event) => {
  const files = event.target.files;
  if (files.length > 0) {
    selectedFile.value = files[0];
    successMessage.value = '';
  } else {
    selectedFile.value = null;
  }
};

/**
 * 格式化文件大小
 * @param {number} bytes 
 * @param {number} decimals 
 */
const formatBytes = (bytes, decimals = 2) => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

/**
 * 处理文件上传
 */
const handleUpload = async () => {
  if (!selectedFile.value) {
    alert('请先选择一个文件。');
    return;
  }
  
  successMessage.value = '';
  try {
    const uploadedFile = await fileStore.uploadFile(selectedFile.value, description.value);
    
    // 上传成功，清空表单
    selectedFile.value = null;
    description.value = '';
    if (fileInputRef.value) {
      fileInputRef.value.value = ''; // 清空 file input
    }
    
    successMessage.value = `文件 "${uploadedFile.name}" 上传成功！ID: ${uploadedFile.id}`;
    
  } catch (error) {
    // 错误信息已在 store 中设置
  }
};
</script>