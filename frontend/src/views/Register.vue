<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100 p-4">
    <div class="w-full max-w-md bg-white p-8 rounded-xl shadow-2xl border border-gray-200">
      <h2 class="text-3xl font-extrabold text-gray-900 text-center mb-6">
        新用户注册
      </h2>

      <!-- 注册成功/失败 提示 -->
      <p v-if="message" :class="messageClass" class="p-3 rounded-lg mb-4 text-sm text-center border">
        {{ message }}
      </p>

      <form @submit.prevent="handleRegister" class="space-y-6">
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">用户名/学号</label>
          <input id="username" v-model="username" type="text" required
                 class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500 transition duration-150"
                 placeholder="请输入用户名" />
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <input id="password" v-model="password" type="password" required
                 class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500 transition duration-150"
                 placeholder="设置密码" />
        </div>

        <div>
          <label for="confirm-password" class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
          <input id="confirm-password" v-model="confirmPassword" type="password" required
                 class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500 transition duration-150"
                 placeholder="请再次输入密码" />
        </div>

        <button type="submit" :disabled="userStore.isLoading || !isPasswordMatch"
                class="w-full flex justify-center py-2 px-4 border border-transparent rounded-lg shadow-lg text-lg font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition duration-300 disabled:opacity-50">
          <span v-if="userStore.isLoading" class="flex items-center">
            <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            注册中...
          </span>
          <span v-else>立即注册</span>
        </button>
      </form>
      
      <div class="mt-6 text-center">
          <router-link to="/login" class="font-medium text-indigo-600 hover:text-indigo-500 text-sm">
              已有账号？点此返回登录
          </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * src/views/Auth/Register.vue
 * * 注册视图，负责 UI 交互和调用 Pinia Store 中的注册逻辑。
 */
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
// 导入我们最新的 Pinia Store (路径已修正为 '@/stores/Auth')
import { User } from '@/stores/user/Auth'; 

// --- Pinia / 路由实例 ---
const userStore = User(); // 注意这里使用 User()
const router = useRouter();

// --- 响应式状态 ---
const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const message = ref(''); // 成功或失败的提示信息
const isSuccess = ref(false); // 标记是否成功

// --- 计算属性：检查两次密码是否一致 ---
const isPasswordMatch = computed(() => {
    return password.value === confirmPassword.value && password.value.length > 0;
});

// --- 计算属性：动态设置提示框样式 ---
const messageClass = computed(() => {
    return isSuccess.value 
        ? 'text-green-600 bg-green-100 border-green-200'
        : 'text-red-600 bg-red-100 border-red-200';
});


/**
 * 处理注册表单提交，调用 Pinia Store 的注册 Action
 */
const handleRegister = async () => {
  message.value = ''; // 清除之前的提示信息
  isSuccess.value = false;

  if (!isPasswordMatch.value) {
    message.value = '两次输入的密码不一致！';
    return;
  }
  if (userStore.isLoading) return;

  try {
    // 1. 调用 Pinia Store 中封装的注册逻辑 (POST /api/user/register)
    const result = await userStore.register(username.value, password.value);

    // 2. 注册成功
    isSuccess.value = true;
    // 假设后端返回成功消息在 result.message 中，否则提供默认消息
    message.value = result?.message || '注册成功！请前往登录页面。'; 

    // 清空表单
    username.value = '';
    password.value = '';
    confirmPassword.value = '';

    // 自动跳转到登录页（可选，可以保持在当前页等待用户点击跳转）
    setTimeout(() => {
        router.push('/login');
    }, 2000);

  } catch (err) {
    // 3. 注册失败：显示错误信息
    isSuccess.value = false;
    // 假设 Spring Boot 返回的错误信息在 error.response.data.message 中
    const errorMessage = err.response?.data?.message || '注册失败，请稍后再试。';
    message.value = errorMessage;
    console.error('注册组件捕获到错误:', err);

  }
};
</script>