<template>
  <div class="p-6 bg-white shadow-lg rounded-xl border border-gray-100 mt-8">
    <h2 class="text-2xl font-semibold text-gray-800 mb-6 border-b pb-3">文件列表与管理</h2>

    <!-- 加载状态和错误提示 -->
    <div v-if="fileStore.isStoreLoading" class="text-center py-12 text-gray-500">
      <svg class="animate-spin mx-auto h-8 w-8 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
      <p class="mt-2">正在加载文件数据...</p>
    </div>
    
    <div v-else-if="fileStore.error" class="bg-red-50 text-red-700 p-4 rounded-lg border border-red-200">
      <p>数据加载失败: {{ fileStore.error }}</p>
      <button @click="loadFiles" class="mt-2 text-sm underline hover:text-red-900">重新加载</button>
    </div>

    <!-- 文件列表表格 -->
    <div v-else>
      <div v-if="fileList.length === 0" class="text-center py-12 text-gray-500 border border-dashed rounded-lg">
        <p>暂无文件记录。</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <!-- 将 ID 标题改为 序号 -->
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">序号</th> 
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">文件名</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">描述</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">大小</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">上传时间</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <!-- 遍历时使用 index 索引 -->
            <tr v-for="(file, index) in fileList" :key="file.id" class="hover:bg-gray-50 transition duration-150">
              <!-- 使用 displayId 计算序号 -->
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ displayId(index) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 truncate max-w-xs">{{ file.name }}</td>
              <td class="px-6 py-4 text-sm text-gray-500 truncate max-w-sm">{{ file.description || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatBytes(file.size) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(file.uploadTime) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                <button 
                  @click="downloadFile(file)" 
                  class="text-blue-600 hover:text-blue-900 transition duration-150"
                  title="下载文件"
                >
                  <i class="fas fa-download"></i> 下载
                </button>
                <button 
                  @click="openEditModal(file)" 
                  class="text-yellow-600 hover:text-yellow-900 transition duration-150"
                  title="编辑元数据"
                >
                  <i class="fas fa-edit"></i> 编辑
                </button>
                <button 
                  @click="confirmDelete(file.id, file.name)" 
                  class="text-red-600 hover:text-red-900 transition duration-150"
                  title="删除文件"
                >
                  <i class="fas fa-trash-alt"></i> 删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页控件 -->
      <div v-if="fileList.length > 0" class="flex justify-between items-center mt-6 p-4 border-t border-gray-100">
        <span class="text-sm text-gray-600">
          共 {{ pagination.totalElements }} 条记录, 第 {{ pagination.page + 1 }} / {{ pagination.totalPages }} 页
        </span>
        <div class="space-x-2">
          <button 
            @click="changePage(pagination.page - 1)"
            :disabled="pagination.page === 0"
            class="px-4 py-2 text-sm font-medium rounded-lg transition duration-150"
            :class="pagination.page === 0 ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-blue-500 text-white hover:bg-blue-600'"
          >
            上一页
          </button>
          <button 
            @click="changePage(pagination.page + 1)"
            :disabled="pagination.last"
            class="px-4 py-2 text-sm font-medium rounded-lg transition duration-150"
            :class="pagination.last ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-blue-500 text-white hover:bg-blue-600'"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑模态框 -->
    <div v-if="isEditModalOpen" class="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-white p-6 rounded-xl shadow-2xl w-full max-w-md">
        <h3 class="text-xl font-semibold mb-4 border-b pb-2">编辑文件信息</h3>
        <form @submit.prevent="saveEdit">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">文件名</label>
            <input 
              v-model="currentEditFile.name" 
              type="text" 
              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
            <textarea 
              v-model="currentEditFile.description" 
              rows="3"
              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 resize-none"
            ></textarea>
          </div>
          
          <div v-if="editError" class="text-sm text-red-600 bg-red-50 p-3 rounded-lg mb-4">
            保存失败: {{ editError }}
          </div>

          <div class="flex justify-end space-x-3">
            <button 
              type="button" 
              @click="isEditModalOpen = false"
              class="px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-100 transition duration-150"
            >
              取消
            </button>
            <button 
              type="submit" 
              :disabled="fileStore.isStoreLoading"
              class="px-4 py-2 text-white font-bold rounded-xl transition duration-150"
              :class="fileStore.isStoreLoading ? 'bg-blue-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'"
            >
              <span v-if="fileStore.isStoreLoading">保存中...</span>
              <span v-else>保存更改</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { FileStore } from '@/stores/file';

const fileStore = FileStore();

// 状态
const currentQuery = reactive({
  page: 0,
  size: 10,
  sortBy: 'uploadTime',
  sortDirection: 'desc'
});

// 计算属性
const fileList = computed(() => fileStore.getFileList);
const pagination = computed(() => fileStore.getPagination);

// 编辑模态框相关
const isEditModalOpen = ref(false);
const currentEditFile = ref({ id: null, name: '', description: '' });
const editError = ref(null);

// 生命周期钩子：组件挂载时加载文件列表
onMounted(() => {
  loadFiles();
});

/**
 * 计算基于分页的序号
 * @param {number} index - 当前页数组中的索引 (从 0 开始)
 * @returns {number} 页面显示的序号 (从 1 开始)
 */
const displayId = (index) => {
    // 公式: (当前页码 * 每页大小) + 索引 + 1
    return pagination.value.page * pagination.value.size + index + 1;
};

/**
 * 加载文件列表
 */
const loadFiles = () => {
  fileStore.fetchFiles(currentQuery);
};

/**
 * 改变页码并重新加载
 * @param {number} newPage 
 */
const changePage = (newPage) => {
  if (newPage >= 0 && newPage < pagination.value.totalPages) {
    currentQuery.page = newPage;
    loadFiles();
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
 * 格式化时间戳
 * @param {string} dateString - ISO 8601 格式时间字符串
 */
const formatDate = (dateString) => {
    if (!dateString) return '-';
    // 假设后端返回的是 ISO 格式的字符串 (如 2023-10-27T10:30:00)
    return new Date(dateString).toLocaleString('zh-CN', { 
        year: 'numeric', month: '2-digit', day: '2-digit', 
        hour: '2-digit', minute: '2-digit', second: '2-digit' 
    });
};

/**
 * 下载文件
 */
const downloadFile = (file) => {
  fileStore.downloadFile(file.id, file.name);
};

/**
 * 打开编辑模态框
 */
const openEditModal = (file) => {
  // 深拷贝文件数据到编辑对象
  currentEditFile.value = { ...file };
  editError.value = null;
  isEditModalOpen.value = true;
};

/**
 * 保存编辑后的文件信息
 */
const saveEdit = async () => {
  editError.value = null;
  const { id, name, description } = currentEditFile.value;

  try {
    await fileStore.updateFile(id, { name, description });
    isEditModalOpen.value = false;
  } catch (err) {
    editError.value = err.response?.data?.message || '未知错误';
  }
};

/**
 * 确认删除文件
 */
const confirmDelete = async (id, name) => {
  // 实际项目中应使用自定义模态框，这里使用简化的原生 confirm
  if (window.confirm(`确定要删除文件 "${name}" 吗？此操作不可逆！`)) {
    try {
      await fileStore.deleteFile(id);
      // 如果当前页文件删除后变空，并且不是第一页，则重新加载上一页
      if (fileList.value.length === 0 && currentQuery.page > 0) {
          currentQuery.page -= 1;
      }
      loadFiles(); // 重新加载以更新分页信息
    } catch (err) {
      alert(`删除失败: ${fileStore.error}`);
    }
  }
};
</script>

<style scoped>
/* 确保表格内的文本不溢出 */
.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>