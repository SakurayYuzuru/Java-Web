<template>
  <div class="student-list-container">
    <!-- 搜索/查询区域 -->
    <div class="search-area">
      <input
        v-model="searchName"
        class="input search-input"
        type="text"
        placeholder="输入学生姓名进行搜索"
        @keyup.enter="handleSearch"
      />
      <button @click="handleSearch" class="search-button" :disabled="store.isLoading">
        {{ store.isLoading ? '搜索中...' : '搜索' }}
      </button>
      <button @click="resetSearch" class="reset-button" :disabled="store.isLoading">
        重置
      </button>
    </div>

    <!-- 列表数据展示 -->
    <div class="table-wrapper">
      <table class="student-table">
        <thead>
          <tr>
            <th>姓名</th>
            <th>学号</th>
            <th>学校</th>
            <th>班级</th>
            <th>语文</th>
            <th>数学</th>
            <th>英语</th>
            <th>物理</th>
            <th>化学</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="store.isLoading && !students.length">
             <td colspan="10" class="text-center py-4 text-blue-500">
                <div class="loading-spinner"></div>
                加载中，请稍候...
            </td>
          </tr>
          <tr v-else-if="!students.length">
            <td colspan="10" class="text-center py-4 text-gray-500">暂无学生信息或未查询到结果。</td>
          </tr>
          <tr v-for="student in students" :key="student.id">
            <td>{{ student.studentName }}</td>
            <td>{{ student.studentId }}</td>
            <td>{{ student.school }}</td>
            <td>{{ student.className }}</td>
            <td>{{ student.chinese }}</td>
            <td>{{ student.math }}</td>
            <td>{{ student.english }}</td>
            <td>{{ student.physics }}</td>
            <td>{{ student.chemistry }}</td>
            <td>
              <button @click="handleDelete(student.id)" class="delete-button" :disabled="store.isLoading">
                删除
              </button>
              <!-- 暂不实现更新功能，只展示接口 -->
              <!-- <button @click="handleEdit(student)">编辑</button> -->
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-area" v-if="totalPages > 1">
      <button @click="prevPage" :disabled="currentPage === 1 || store.isLoading" class="page-button">
        &lt; 上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页 (共 {{ totalItems }} 条)</span>
      <button @click="nextPage" :disabled="currentPage === totalPages || store.isLoading" class="page-button">
        下一页 &gt;
      </button>
    </div>

    <!-- 消息提示 -->
    <p v-if="message" :class="['message', { 'error': isError }]">{{ message }}</p>

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Student } from "@/stores/student"; // 假设 Pinia store 路径正确

const store = Student();

// 分页和数据状态
const students = ref([]);
const currentPage = ref(0);
const pageSize = ref(10); // 默认每页10条，确保始终为正整数
const totalPages = ref(1);
const totalItems = ref(0);

// 搜索状态
const searchName = ref("");
const isSearching = ref(false);

// 消息提示
const message = ref("");
const isError = ref(false);

/**
 * 加载学生列表数据
 * @param {boolean} isNewQuery - 是否是新的查询（重置页码）
 */
async function loadStudents(isNewQuery = false) {
  if (isNewQuery) {
    currentPage.value = 0; // 新查询从第一页开始
  }

  message.value = "";
  isError.value = false;

  try {
    let response;
    
    if (isSearching.value && searchName.value) {
      // 执行搜索
      response = await store.search(searchName.value, currentPage.value, pageSize.value);
    } else {
      // 执行普通分页查询
      response = await store.queryStudents(currentPage.value, pageSize.value);
    }

    // 解析 Spring Page 结构
    if (response) {
      students.value = response.content || [];
      totalItems.value = response.totalElements || 0;
      totalPages.value = response.totalPages || 1;
      // ❌ 不要再写回 currentPage
    }else {
      throw new Error("后端返回了空数据或格式不正确的响应。");
    }
    
    if (isSearching.value && students.value.length > 0) {
        message.value = `搜索到 ${totalItems.value} 条结果。`;
    } else if (isSearching.value && students.value.length === 0) {
        message.value = `未搜索到匹配 "${searchName.value}" 的学生。`;
    } else if (students.value.length === 0) {
        message.value = "学生列表为空。";
    } else {
        message.value = "学生列表加载成功。";
    }

  } catch (err) {
    console.error("加载学生列表失败:", err);
    // 在控制台打印详细错误信息，辅助调试后端问题
    if (err.response) {
      console.error("后端返回状态码:", err.response.status);
      console.error("后端返回数据:", err.response.data);
    }
    message.value = "获取学生数据失败，请检查网络或API配置。";
    isError.value = true;
    students.value = [];
    totalItems.value = 0;
    totalPages.value = 1;
  }
}

/** 搜索处理函数 */
function handleSearch() {
  if (searchName.value.trim()) {
    isSearching.value = true;
  } else {
    isSearching.value = false;
  }
  loadStudents(true); // 搜索时重置到第一页
}

/** 重置搜索处理函数 */
function resetSearch() {
  searchName.value = "";
  isSearching.value = false;
  loadStudents(true); // 重置时重新加载第一页数据
}

/** 删除学生信息 */
async function handleDelete(id) {
  if (window.confirm(`确定要删除学号为 ${id} 的学生信息吗？`)) {
    try {
      await store.delete(id);
      message.value = `学号 ${id} 的学生信息删除成功。`;
      isError.value = false;
      // 删除成功后，重新加载当前页数据
      loadStudents(false);
    } catch (err) {
      console.error("删除失败:", err);
      message.value = "删除失败，请检查后端日志。";
      isError.value = true;
    }
  }
}

// 分页逻辑
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    loadStudents();
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    loadStudents();
  }
}

// 组件挂载时自动加载数据
onMounted(() => {
  loadStudents();
});

// 监听页码变化，重新加载数据
watch(currentPage, () => {
  loadStudents(false);
});
</script>

<style scoped>
.student-list-container {
    padding: 10px;
}

/* 搜索区域样式 */
.search-area {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f7f9fc;
    border-radius: 8px;
    align-items: center;
}

.search-input {
    flex-grow: 1;
}

.input {
    padding: 10px 12px;
    border: 1px solid #dcdcdc;
    border-radius: 6px;
    box-sizing: border-box;
}

.search-button, .reset-button {
    padding: 10px 18px;
    border: none;
    color: white;
    font-weight: 500;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.search-button {
    background: #409eff;
}
.search-button:hover:not(:disabled) {
    background: #3385ff;
}

.reset-button {
    background: #6c757d;
}
.reset-button:hover:not(:disabled) {
    background: #5a6268;
}

.search-button:disabled, .reset-button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

/* 表格样式 */
.table-wrapper {
    overflow-x: auto; /* 确保在小屏幕上可以横向滚动 */
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.student-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 800px; /* 最小宽度确保内容不挤压 */
}

.student-table th, .student-table td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
}

.student-table th {
    background-color: #f0f2f5;
    color: #333;
    font-weight: 600;
}

.student-table tbody tr:hover {
    background-color: #f5f5f5;
}

/* 操作按钮 */
.delete-button {
    padding: 6px 10px;
    background-color: #f44336;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.delete-button:hover:not(:disabled) {
    background-color: #d32f2f;
}

.delete-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* 分页样式 */
.pagination-area {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    gap: 15px;
}

.page-button {
    padding: 8px 15px;
    background-color: #fff;
    color: #409eff;
    border: 1px solid #409eff;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
}

.page-button:hover:not(:disabled) {
    background-color: #ecf5ff;
}

.page-button:disabled {
    color: #a0cfff;
    border-color: #a0cfff;
    cursor: not-allowed;
}

.page-info {
    font-size: 14px;
    color: #606266;
}

/* 消息样式 */
.message {
  margin-top: 15px;
  padding: 10px;
  border-radius: 6px;
  text-align: center;
  color: #1b5e20;
  background-color: #e8f5e9;
  border: 1px solid #a5d6a7;
}

.message.error {
  color: #b71c1c;
  background-color: #ffebee;
  border: 1px solid #ef9a9a;
}

/* Loading Spinner Placeholder */
.loading-spinner {
  display: inline-block;
  width: 1em;
  height: 1em;
  border: 3px solid rgba(64, 158, 255, 0.3);
  border-radius: 50%;
  border-top-color: #409eff;
  animation: spin 1s ease-in-out infinite;
  vertical-align: middle;
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>