<template>
  <!-- 使用用户提供的 CSS class 结构 -->
  <div class="panel">
    <h2 class="panel-title">{{ title }}</h2>
    <div class="panel-body">
      <!-- 核心修复：使用 <slot> 接受调用组件（如 AddUser.vue）传入的内容 -->
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'; // 确保导入 defineProps

// 仅定义 title 属性，作为通用面板，不应包含路由或视图逻辑。
defineProps({
  title: {
    type: String,
    default: "操作区"
  }
});

// 移除了所有动态组件加载 (currentView, computed, UserList, etc.)
// 因为这些逻辑应该在 Home.vue 或顶层组件中处理。
</script>

<style scoped>
/* 采用用户提供的样式并进行美化和响应式调整 */
.panel {
  width: 100%; /* 扩展到父容器宽度 */
  max-width: 800px; /* 设定最大宽度，使内容不会太宽 */
  margin: 0 auto; /* 居中显示 */
  padding: 30px; /* 增大内边距 */
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1); /* 优化阴影 */
  transition: all 0.3s ease;
}

.panel-title {
  font-size: 24px; /* 略微增大 */
  margin-bottom: 20px; 
  padding-bottom: 8px;
  border-bottom: 3px solid #00796b; /* 添加主题色分隔线 */
  text-align: left; 
  font-weight: 700;
  color: #00796b; /* 使用主题色 */
}

.panel-body {
  padding-top: 15px;
}

/* 移动端适应 */
@media (max-width: 768px) {
    .panel {
        padding: 20px;
        border-radius: 8px;
    }
    .panel-title {
        font-size: 20px;
    }
}
</style>