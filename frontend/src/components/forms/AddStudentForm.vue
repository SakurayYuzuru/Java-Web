<template>
  <form @submit.prevent="handleSubmit" class="space-y-6">
    <!-- 基本信息 -->
    <h3 class="text-lg font-semibold border-b pb-2 text-gray-700">学生基本信息</h3>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="flex flex-col">
        <label class="label">姓名</label>
        <input v-model.trim="formData.name" class="input" type="text" placeholder="学生姓名" required />
      </div>
      <div class="flex flex-col">
        <label class="label">学号</label>
        <input v-model.trim="formData.id" class="input" type="text" placeholder="学号" required />
      </div>
      <div class="flex flex-col">
        <label class="label">学校</label>
        <input v-model.trim="formData.school" class="input" type="text" placeholder="所在学校" required />
      </div>
      <div class="flex flex-col md:col-span-1">
        <label class="label">班级</label>
        <input v-model.trim="formData.classname" class="input" type="text" placeholder="所在班级" required />
      </div>
    </div>

    <!-- 成绩信息 -->
    <h3 class="text-lg font-semibold border-b pb-2 pt-4 text-gray-700">各科成绩 (0-100)</h3>
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
      <div v-for="key in scoreKeys" :key="key" class="flex flex-col">
        <label class="label">{{ scoreLabels[key] }}</label>
        <input 
          v-model.number="formData[key]" 
          class="input" 
          type="number" 
          min="0" 
          max="100" 
          placeholder="分数" 
          required 
        />
      </div>
    </div>

    <!-- 提交按钮 -->
    <button class="button" :disabled="store.isLoading || !isFormValid">
      {{ store.isLoading ? "添加中…" : "确认添加学生" }}
    </button>

    <!-- 消息提示 -->
    <p v-if="message" :class="['message', { 'error': isError }]">{{ message }}</p>

  </form>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import { Student } from "@/stores/student"; // 假设 Pinia store 路径正确

const store = Student();

// 表单数据模型
const formData = reactive({
  name: "",
  id: "",
  school: "",
  classname: "",
  chinese: null,
  math: null,
  english: null,
  physics: null,
  chemistry: null,
});

const message = ref("");
const isError = ref(false);

const scoreKeys = ['chinese', 'math', 'english', 'physics', 'chemistry'];
const scoreLabels = {
  chinese: '语文',
  math: '数学',
  english: '英语',
  physics: '物理',
  chemistry: '化学',
};

// 检查表单验证
const isFormValid = computed(() => {
  const baseValid = formData.name && formData.id && formData.school && formData.classname;
  
  const scoresValid = scoreKeys.every(key => 
    typeof formData[key] === 'number' && 
    formData[key] >= 0 && 
    formData[key] <= 100
  );

  return baseValid && scoresValid;
});

// 重置表单
const resetForm = () => {
  formData.name = "";
  formData.id = "";
  formData.school = "";
  formData.classname = "";
  scoreKeys.forEach(key => formData[key] = null);
};

/** 添加学生逻辑 */
async function handleSubmit() {
  if (!isFormValid.value) {
    message.value = "请确保所有信息填写完整且分数在 0-100 范围内。";
    isError.value = true;
    return;
  }

  message.value = "";
  isError.value = false;

  try {
    await store.add(
      formData.name,
      formData.id,
      formData.school,
      formData.classname,
      formData.chinese,
      formData.math,
      formData.english,
      formData.physics,
      formData.chemistry
    );

    message.value = `学生 ${formData.name} (学号: ${formData.id}) 信息添加成功！`;
    resetForm();
    
  } catch (err) {
    console.error("添加学生信息请求失败:", err);
    let errorMessage = "添加失败，请检查网络或后端服务。";
    
    if (err.response) {
      const status = err.response.status;
      const dataMessage = err.response.data?.message || err.response.data?.error;
      
      if (status === 409) {
        errorMessage = `添加失败 (状态码: 409)：学号已存在。`;
      } else if (dataMessage) {
        errorMessage = `添加失败 (HTTP ${status})：${dataMessage}`;
      } else {
        errorMessage = `添加失败 (HTTP 错误: ${status})。`;
      }
    } else if (err.request) {
      errorMessage = "网络连接失败或服务器无响应。";
    }
    
    message.value = errorMessage;
    isError.value = true;
  }
}
</script>

<style scoped>
.label {
  margin-top: 4px;
  font-size: 14px;
  color: #555;
  font-weight: 500;
}

.input {
  margin-top: 4px;
  padding: 8px 10px;
  border: 1px solid #dcdcdc;
  border-radius: 6px;
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.input:focus {
    border-color: #409eff;
    outline: none;
    box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.2);
}

.button {
  padding: 12px;
  width: 100%;
  background: #409eff;
  border: none;
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.button:hover:not(:disabled) {
  background: #3385ff;
}

.button:disabled {
  background: #a0cfff;
  opacity: 0.8;
  cursor: not-allowed;
}

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
</style>