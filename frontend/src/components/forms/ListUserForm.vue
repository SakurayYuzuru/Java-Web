<template>
  <div class="user-list-container">
    <!-- 加载状态指示器 -->
    <div v-if="userStore.isLoading" class="loading-overlay">
      <div class="spinner"></div>
      正在加载用户数据...
    </div>

    <!-- 数据表格 -->
    <div v-else class="table-wrapper">
      <table class="user-table">
        <thead>
          <tr>
            <!-- 更改：显示动态计算的序号 -->
            <th>序号</th>
            <th>用户名</th>
            <th>邮箱</th>
            <!-- <th>角色 (Role)</th> 暂时移除，因为 Pinia Store 中未定义此字段 -->
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <!-- 使用 (user, index) 循环获取当前元素索引 -->
          <tr v-for="(user, index) in users" :key="user.id">
            <!-- 关键更改：计算连续序号 -->
            <td class="text-center">{{ (currentPage * pageSize) + index + 1 }}</td>
            <!-- 显示原始数据库 ID -->
            <td>{{ user.username }}</td>
            <td>{{ user.email || 'N/A' }}</td>
            <td class="action-cell">
              <button @click="openEditModal(user)" class="btn btn-edit">编辑</button>
              <button @click="openDeleteModal(user)" class="btn btn-delete">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页控制 -->
    <div class="pagination-controls" v-if="totalPages > 1">
      <button 
        @click="changePage(currentPage - 1)" 
        :disabled="currentPage === 0" 
        class="page-btn">
        &lt; 上一页
      </button>
      <span class="page-info">第 {{ currentPage + 1 }} 页 / 共 {{ totalPages }} 页 (总条目: {{ totalElements }})</span>
      <button 
        @click="changePage(currentPage + 1)" 
        :disabled="currentPage >= totalPages - 1" 
        class="page-btn">
        下一页 &gt;
      </button>
    </div>

    <!-- ---------------------------------------------------- -->
    <!-- 1. 编辑用户模态框 (Edit Modal) -->
    <!-- ---------------------------------------------------- -->
    <div v-if="isEditModalOpen" class="modal-overlay">
      <div class="modal-content">
        <h3 class="modal-title">编辑用户信息 (ID: {{ currentEditUser.id }})</h3>
        <form @submit.prevent="handleUpdateUser">
          
          <div class="form-group">
            <label>用户名:</label>
            <input type="text" v-model="currentEditUser.username" required />
          </div>
          
          <div class="form-group">
            <label>邮箱:</label>
            <input type="email" v-model="currentEditUser.email" placeholder="可选" />
          </div>

          <div class="form-group">
            <label>新密码:</label>
            <input type="password" v-model="currentEditUser.password" placeholder="留空则不修改" />
          </div>
          
          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="userStore.isLoading">
              {{ userStore.isLoading ? '保存中...' : '保存更改' }}
            </button>
            <button type="button" @click="isEditModalOpen = false" class="btn btn-secondary">取消</button>
          </div>
        </form>
        <p v-if="editError" class="error-message">{{ editError }}</p>
      </div>
    </div>
    
    <!-- ---------------------------------------------------- -->
    <!-- 2. 删除确认模态框 (Delete Modal) -->
    <!-- ---------------------------------------------------- -->
    <div v-if="isDeleteModalOpen" class="modal-overlay">
      <div class="modal-content delete-modal">
        <h3 class="modal-title delete-title">确认删除用户</h3>
        <p class="delete-message">您确定要删除用户 **{{ currentDeleteUser.username }}** (ID: {{ currentDeleteUser.id }}) 吗？此操作不可逆！</p>
        <div class="modal-actions">
          <button @click="handleDeleteUser" class="btn btn-delete" :disabled="userStore.isLoading">
            {{ userStore.isLoading ? '删除中...' : '确认删除' }}
          </button>
          <button @click="isDeleteModalOpen = false" class="btn btn-secondary">取消</button>
        </div>
        <p v-if="deleteError" class="error-message">{{ deleteError }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
// 导入 Pinia Store，假设路径正确
import { User } from '@/stores/user/Auth'; 

const userStore = User();

// --- 列表状态 ---
const users = ref([]);
const currentPage = ref(0); // 后端约定页码从 0 开始
const pageSize = ref(10); // 明确定义 pageSize 供计算使用
const totalPages = ref(0);
const totalElements = ref(0);

// --- 模态框状态 ---
const isEditModalOpen = ref(false);
const isDeleteModalOpen = ref(false);
const currentEditUser = reactive({ id: null, username: '', email: '', password: '' });
const currentDeleteUser = reactive({ id: null, username: '' });
const editError = ref('');
const deleteError = ref('');

// --- 数据获取逻辑 ---

/**
 * 从后端加载用户列表
 */
const fetchUsers = async () => {
  editError.value = '';
  deleteError.value = '';
  try {
    // 调用 Pinia Store 中的 queryUsers action
    // 注意：pageSize 在此用于 API 请求
    const data = await userStore.queryUsers(currentPage.value, pageSize.value);
    
    // 更新列表和分页信息
    users.value = data.content || [];
    totalPages.value = data.totalPages || 0;
    totalElements.value = data.totalElements || 0;

  } catch (error) {
    console.error('获取用户列表失败:', error);
    // 可以在这里设置一个全局或局部的错误提示
    // 由于在 Canvas 环境不推荐使用 alert()，这里使用 console.error
    console.error('操作提示：获取用户列表失败，请检查网络或权限。');
  }
};

/**
 * 改变当前页码并重新加载数据
 */
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
    fetchUsers();
  }
};

// --- 生命周期钩子：组件挂载后立即加载数据 ---
onMounted(fetchUsers);

// --- 编辑用户逻辑 ---

/**
 * 打开编辑模态框
 */
const openEditModal = (user) => {
  editError.value = '';
  // 复制用户数据到响应式对象，避免直接修改列表数据
  currentEditUser.id = user.id;
  currentEditUser.username = user.username;
  currentEditUser.email = user.email || '';
  currentEditUser.password = ''; // 密码字段默认清空
  isEditModalOpen.value = true;
};

/**
 * 提交更新用户信息的请求
 */
const handleUpdateUser = async () => {
  editError.value = '';
  const updateData = {
    username: currentEditUser.username,
    email: currentEditUser.email,
    // 只有当用户输入了新密码时才发送 password 字段
    ...(currentEditUser.password && { password: currentEditUser.password }), 
  };
  
  try {
    await userStore.updateUser(currentEditUser.id, updateData);
    isEditModalOpen.value = false;
    await fetchUsers(); // 重新加载数据以更新列表

  } catch (error) {
    const message = error.response?.data?.message || '更新失败，请重试。';
    editError.value = message;
    console.error('用户更新失败:', error);
  }
};


// --- 删除用户逻辑 ---

/**
 * 打开删除确认模态框
 */
const openDeleteModal = (user) => {
  deleteError.value = '';
  currentDeleteUser.id = user.id;
  currentDeleteUser.username = user.username;
  isDeleteModalOpen.value = true;
};

/**
 * 提交删除用户的请求
 */
const handleDeleteUser = async () => {
  deleteError.value = '';
  try {
    await userStore.deleteUser(currentDeleteUser.id);
    isDeleteModalOpen.value = false;
    // 删除成功后，保持当前页码并重新加载
    await fetchUsers(); 

  } catch (error) {
    const message = error.response?.data?.message || '删除失败，请重试。';
    deleteError.value = message;
    console.error('用户删除失败:', error);
  }
};
</script>

<style scoped>
/* --------------------------------------
 * 列表主样式
 * -------------------------------------- */
.user-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #00796b;
  margin-bottom: 25px;
  border-bottom: 3px solid #b2dfdb;
  padding-bottom: 10px;
}

.loading-overlay {
  text-align: center;
  padding: 50px;
  font-size: 1.1rem;
  color: #00796b;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #00796b;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.table-wrapper {
  overflow-x: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
  background-color: white;
}

.user-table th, .user-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.user-table th {
  background-color: #e0f2f1;
  color: #004d40;
  font-weight: 600;
  text-transform: uppercase;
}

.user-table tr:hover {
  background-color: #f5f5f5;
}

/* 序号居中 */
.user-table td:first-child {
  text-align: center;
  width: 50px; /* 限制序号宽度 */
}

.action-cell {
  white-space: nowrap;
}

/* 按钮样式 */
.btn {
  padding: 8px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s, box-shadow 0.2s;
  margin-right: 8px;
}

.btn-edit {
  background-color: #2196f3; /* 蓝色 */
  color: white;
}

.btn-edit:hover {
  background-color: #1e88e5;
  box-shadow: 0 2px 5px rgba(33, 150, 243, 0.3);
}

.btn-delete {
  background-color: #f44336; /* 红色 */
  color: white;
}

.btn-delete:hover {
  background-color: #e53935;
  box-shadow: 0 2px 5px rgba(244, 67, 54, 0.3);
}

/* --------------------------------------
 * 分页样式
 * -------------------------------------- */
.pagination-controls {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #00796b;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page-btn:hover:not(:disabled) {
  background-color: #00695c;
}

.page-btn:disabled {
  background-color: #b2dfdb;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.95rem;
  color: #4b5563;
}

/* --------------------------------------
 * 模态框样式 (Modal)
 * -------------------------------------- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  transform: scale(0.95);
  animation: modal-in 0.3s forwards;
}

@keyframes modal-in {
  to { transform: scale(1); }
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #00796b;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: #00796b;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
}

.btn-primary {
  background-color: #00796b;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #00695c;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e0e0e0;
}

/* 删除模态框特定样式 */
.delete-modal {
  border-left: 5px solid #f44336;
}
.delete-title {
  color: #f44336;
}
.delete-message {
  line-height: 1.5;
  color: #4b5563;
}

/* 错误信息样式 */
.error-message {
    color: #f44336;
    background-color: #fde0df;
    padding: 10px;
    border-radius: 6px;
    margin-top: 15px;
    border: 1px solid #f88;
}

/* 响应式调整 */
@media (max-width: 600px) {
    .user-table th, .user-table td {
        padding: 8px 10px;
    }
    .action-cell {
        display: flex;
        flex-direction: column;
        gap: 5px;
    }
}
</style>