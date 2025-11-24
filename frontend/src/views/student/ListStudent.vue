<template>
  <div class="p-6 md:p-10 bg-gray-50 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-800 mb-6 border-b pb-2">学生信息列表与查询</h1>
      <div class="bg-white p-6 rounded-xl shadow-lg">
        <!-- 引入实际的列表/查询组件 -->
        <ListStudentForm />
      </div>
    </div>
  </div>
</template>

<script setup>
import ListStudentForm from '@/components/forms/ListStudentForm.vue';
</script>

<style scoped>
/* 容器样式使用 Tailwind CSS 类名实现，这里保持简洁 */
</style>