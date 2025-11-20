<template>
  <!-- 根容器：实现全屏三列布局 -->
  <div class="layout-root">

    <!-- 顶部区域 -->
    <header class="top">
      学生信息管理系统
    </header>

    <div class="middle">

      <!-- 左侧导航 -->
      <aside class="left">
        <!-- 导航链接，点击时修改 currentPageKey -->
        <ul class="nav-list">
          <li><a @click="currentPageKey='dashboard'" :class="{ active: currentPageKey === 'dashboard' }">系统首页</a></li>
          <li><a @click="currentPageKey='addUser'" :class="{ active: currentPageKey === 'addUser' }">用户添加</a></li>
          <li><a @click="currentPageKey='listUser'" :class="{ active: currentPageKey === 'listUser' }">用户信息</a></li>
          <li><a @click="currentPageKey='addScore'" :class="{ active: currentPageKey === 'addScore' }">成绩录入</a></li>
          <li><a @click="currentPageKey='listScore'" :class="{ active: currentPageKey === 'listScore' }">成绩信息</a></li>
        </ul>
      </aside>

      <!-- 中间操作区：动态渲染当前选中的组件 -->
      <main class="center">
        <!-- 使用计算属性 currentComponent 来动态加载视图 -->
        <component :is="currentComponent" />
      </main>

      <!-- 右侧区域 -->
      <aside class="right">
        操作区
      </aside>

    </div>

    <!-- 底部区域 -->
    <footer class="bottom">
      版权信息：贵州师范大学国际教育学院2025
    </footer>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
// 导入实际的用户添加组件 (请确保路径正确)
import AddUser from '@/views/AddUser.vue';
import ListUser from '@/views/ListUser.vue';
import Dashboard from '@/views/Dashboard.vue';
const AddScore = { template: '<div class="p-4 text-xl font-bold text-blue-600">成绩录入视图 (待实现)</div>' };
const ListScore = { template: '<div class="p-4 text-xl font-bold text-blue-600">成绩信息列表视图 (待实现)</div>' };
// ----------------------------------------------------------------------

// 1. 定义响应式状态：当前选中的页面 (默认首页改为 'dashboard')
const currentPageKey = ref('dashboard');

// 2. 定义组件映射表：添加所有导航目标
const componentMap = {
  dashboard: Dashboard, // 对应系统首页
  addUser: AddUser,
  listUser: ListUser, 
  addScore: AddScore,
  listScore: ListScore, 
};

// 3. 计算属性：根据 key 返回要渲染的组件对象
// 如果找不到对应的组件，默认返回 Dashboard
const currentComponent = computed(() => componentMap[currentPageKey.value] || Dashboard);
</script>

<style scoped>
/* ---------------------------------------------------- */
/* 整体布局样式 (从 ThreeColumn.vue 复制而来) */
/* ---------------------------------------------------- */

.layout-root {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'Inter', sans-serif;
}

.top {
  height: 6%;
  background: #00796b; /* 深青色 */
  text-align: center;
  padding-top: 8px;
  font-size: 20px;
  color: white; 
  font-weight: bold;
}

.middle {
  flex: 1;
  display: flex;
}

.left {
  width: 200px; /* 导航栏固定宽度 */
  min-width: 180px;
  background: #f4f6f9; /* 浅灰色背景 */
  border-right: 1px solid #e0e0e0;
  padding: 0; 
}

.center {
  flex: 1;
  padding: 20px;
  background: #fff;
  overflow-y: auto; /* 允许内容区滚动 */
}

.right {
  width: 10%;
  min-width: 150px; /* 确保右侧有最小宽度 */
  background: #fafafa;
  border-left: 1px solid #e0e0e0;
  padding: 20px;
}

.bottom {
  height: 5%;
  background: #00796b; /* 深青色 */
  text-align: center;
  line-height: 25px;
  font-size: 14px;
  color: #cfd8dc; 
}


/* ---------------------------------------------------- */
/* 导航链接样式 (从 ThreeColumn.vue 复制而来) */
/* ---------------------------------------------------- */

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-list li {
    margin-bottom: 0px;
}

.nav-list a {
    /* 关键修复：设置为块级元素，扩大点击区域，解决“无法点击”问题 */
    display: block; 
    padding: 12px 16px; 
    color: #4b5563;
    text-decoration: none;
    cursor: pointer; 
    transition: background-color 0.2s, color 0.2s;
    border-left: 3px solid transparent; 
    font-size: 15px;
}

.nav-list a:hover {
    background-color: #e0f2f1; 
    color: #00796b;
}

.nav-list a.active {
    /* 激活状态样式 */
    background-color: #b2dfdb; 
    color: #004d40;
    font-weight: bold;
    border-left-color: #00796b; 
}
</style>