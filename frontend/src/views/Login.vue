<template>
  <!-- 外层容器：居中显示，设置背景 -->
  <div class="flex items-center justify-center min-h-screen bg-gray-100 p-4">
    <!-- 登录卡片容器：设置宽度、背景、阴影和圆角 -->
    <div class="w-full max-w-md bg-white p-8 rounded-xl shadow-2xl border border-gray-200">
      <h2 class="text-3xl font-extrabold text-gray-900 text-center mb-6">
        学生管理系统登录
      </h2>
      
      <!-- 错误信息提示框 -->
      <p v-if="loginError" class="text-red-600 bg-red-100 p-3 rounded-lg mb-4 text-sm text-center border border-red-200">
        {{ loginError }}
      </p>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="space-y-6">
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
                 placeholder="请输入密码" />
        </div>

        <!-- 登录按钮，禁用状态时显示加载动画 -->
        <button type="submit" :disabled="userStore.isLoading"
                class="w-full flex justify-center py-2 px-4 border border-transparent rounded-lg shadow-lg text-lg font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-300 disabled:opacity-50">
          <span v-if="userStore.isLoading" class="flex items-center">
            <!-- 加载动画 SVG -->
            <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            登录中...
          </span>
          <span v-else>登录</span>
        </button>
      </form>
      
      <!-- 辅助链接：注册和忘记密码 -->
      <div class="mt-6 flex justify-between text-sm">
          <router-link to="/register" class="font-medium text-green-600 hover:text-green-500">
              还没有账号？去注册
          </router-link>
          <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">
              忘记密码?
          </a>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * src/views/Auth/Login.vue
 * * 登录视图，只处理 UI 交互和调用 Pinia Store。
 */
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// 导入 Pinia Auth Store (Auth.js 中导出的 User)
import { User } from '@/stores/user/Auth';

// --- Pinia / 路由实例 ---
const userStore = User();
const router = useRouter();

// --- 响应式状态 ---
const username = ref('');
const password = ref('');
const loginError = ref('');

/**
 * 处理表单提交，调用 Pinia Store 的登录 Action
 */
const handleLogin = async () => {
  loginError.value = ''; // 清除之前的错误信息
  if (userStore.isLoading) return;

  try {
    // 1. 调用 Pinia Store 中封装的业务逻辑
    // Pinia action: await userStore.login(name, password)
    await userStore.login(
      username.value,
      password.value
    );

    // 2. 登录成功：重定向到学生列表页
    // 导航守卫会确保用户被导向到正确的初始页，这里我们直接推送到 /students
    router.push('/students');

  } catch (err) {
    // 3. 登录失败：显示错误信息
    // 假设 Spring Boot 返回的错误信息在 error.response.data.message 中
    const errorMessage = err.response?.data?.message || '登录失败，请检查用户名和密码。';
    loginError.value = errorMessage;
    console.error('登录组件捕获到错误:', err);

  } finally {
    // 无论成功或失败，都清除密码输入，保护安全
    password.value = '';
  }
};
</script>