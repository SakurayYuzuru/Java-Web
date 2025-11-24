<template>
  <div class="p-6 md:p-10 bg-gray-50 min-h-screen">
    <div class="max-w-4xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-800 mb-6 border-b pb-2">添加新学生信息</h1>
      <div class="bg-white p-6 rounded-xl shadow-lg">
        <!-- 引入实际的表单组件 -->
        <AddStudentForm />
      </div>
    </div>
  </div>
</template>

<script setup>
import AddStudentForm from '@/components/forms/AddStudentForm.vue';
</script>

<style scoped>
/* 容器样式使用 Tailwind CSS 类名实现，这里保持简洁 */
</style>