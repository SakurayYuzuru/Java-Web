<template>
  <!-- 外层容器：使用 login-wrapper 实现全屏居中和背景色 (与 Login.vue 相同) -->
  <div class="login-wrapper">
    <!-- 注册卡片容器 (与 Login.vue 相同) -->
    <div class="login-card">
      <h2 class="login-title">
        学生管理系统 - 新用户注册
      </h2>

      <!-- 注册成功/失败 提示 (使用 error-message 类进行统一) -->
      <p v-if="message" :class="[messageClass]" class="error-message">
        {{ message }}
      </p>

      <!-- 登录表单 -->
      <form @submit.prevent="handleRegister" class="login-form">
        <div class="form-group">
          <label for="username">用户名/学号</label>
          <input id="username" v-model="username" type="text" required
                 placeholder="请输入用户名" />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input id="password" v-model="password" type="password" required
                 placeholder="设置密码" />
        </div>

        <div class="form-group">
          <label for="confirm-password">确认密码</label>
          <input id="confirm-password" v-model="confirmPassword" type="password" required
                 placeholder="请再次输入密码" />
          
          <!-- 密码不一致提示 -->
          <p v-if="!isPasswordMatch && password.length > 0" class="password-mismatch-warning">
            两次输入的密码不一致！
          </p>
        </div>

        <!-- 注册按钮，使用 button-submit 类名 -->
        <button type="submit" :disabled="userStore.isLoading || !isPasswordMatch" class="button-submit">
          <span v-if="userStore.isLoading" class="loading-content">
            <!-- 加载动画 SVG -->
            <svg class="animate-spin spinner-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            注册中...
          </span>
          <span v-else>立即注册</span>
        </button>
      </form>
      
      <!-- 辅助链接 (使用 Login.vue 的辅助链接样式) -->
      <div class="auxiliary-links">
          <router-link to="/login" class="link-register">
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
// 导入 Pinia Store
import { User } from '@/stores/user/Auth'; 

// --- Pinia / 路由实例 ---
const userStore = User(); 
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
// 为了与 Login.vue 的 error-message 样式兼容，这里使用动态类来区分成功/失败的颜色
const messageClass = computed(() => {
    // 成功时使用绿色主题，失败时使用红色主题（红色是默认 error-message）
    return isSuccess.value 
        ? 'success-message'
        : 'error-message-fail';
});


/**
 * 处理注册表单提交，调用 Pinia Store 的注册 Action
 */
const handleRegister = async () => {
  message.value = ''; // 清除之前的提示信息
  isSuccess.value = false;

  if (!isPasswordMatch.value) {
    message.value = '两次输入的密码不一致！'; // 这个会在表单下方额外显示，确保用户看到
    return;
  }
  if (userStore.isLoading) return;

  try {
    // 1. 调用 Pinia Store 中封装的注册逻辑 (POST /api/user/register)
    const result = await userStore.register(username.value, password.value);

    // 2. 注册成功
    isSuccess.value = true;
    // 假设后端返回成功消息在 result.message 中，否则提供默认消息
    message.value = result?.message || '注册成功！正在跳转至登录页...'; 

    // 清空表单
    username.value = '';
    password.value = '';
    confirmPassword.value = '';

    // 自动跳转到登录页
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

<style scoped>
/* --------------------------------------
 * 复制 Login.vue 的核心美化样式
 * -------------------------------------- */

/* 1. 外层容器：全屏青色背景和居中 */
.login-wrapper {
    min-height: 100vh;
    width: 100%;
    background-color: #36a3b2; 
    display: flex;
    align-items: center; 
    justify-content: center; 
    padding: 1rem;
    box-sizing: border-box;
}

/* 2. 登录卡片容器 */
.login-card {
    width: 100%;
    max-width: 420px; 
    background-color: #ffffff;
    padding: 2.5rem;
    border-radius: 12px; 
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1), 0 5px 15px rgba(0, 0, 0, 0.05);
    border-top: 5px solid #00796b; 
}

/* 3. 登录标题 */
.login-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1f2937;
    text-align: center;
    margin-bottom: 2rem;
}

/* 4. 表单布局 */
.login-form {
    display: flex;
    flex-direction: column;
    gap: 1.25rem; 
}

/* 5. 表单组 */
.form-group label {
    display: block;
    font-size: 0.875rem;
    font-weight: 500;
    color: #4b5563;
    margin-bottom: 0.4rem;
}

.form-group input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    transition: border-color 0.2s, box-shadow 0.2s;
    font-size: 1rem;
}

.form-group input:focus {
    outline: none;
    border-color: #36a3b2; 
    box-shadow: 0 0 0 3px rgba(54, 163, 178, 0.3); 
}

/* 6. 登录/注册按钮 */
.button-submit {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0.75rem 1rem;
    border: none;
    border-radius: 8px;
    color: white;
    font-size: 1.1rem;
    font-weight: 600;
    
    background-color: #00796b; 
    box-shadow: 0 4px 10px rgba(0, 121, 107, 0.3);
    
    cursor: pointer;
    transition: background-color 0.2s, opacity 0.2s;
}

.button-submit:hover:not(:disabled) {
    background-color: #00695c;
    box-shadow: 0 6px 12px rgba(0, 121, 107, 0.4);
}

.button-submit:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* 7. 加载动画内容 */
.loading-content {
    display: flex;
    align-items: center;
}

.spinner-icon {
    height: 1.25rem;
    width: 1.25rem;
    margin-right: 0.75rem;
    color: white;
}

/* 8. 错误/提示消息样式 (通用基础样式) */
.error-message {
    padding: 0.75rem;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    text-align: center;
    font-size: 0.875rem;
    font-weight: 500;
}

/* 9. 失败时的具体样式 (对应 isSuccess = false) */
.error-message.error-message-fail {
    color: #c53030; /* 红色 */
    background-color: #fed7d7; /* 浅红背景 */
    border: 1px solid #fc8181;
}

/* 10. 成功时的具体样式 (对应 isSuccess = true) */
.error-message.success-message {
    color: #00796b; /* 绿色文本 */
    background-color: #e0f2f1; /* 浅绿背景 */
    border: 1px solid #00bfa5;
}

/* 11. 密码不匹配时的提示 */
.password-mismatch-warning {
    color: #c53030;
    font-size: 0.75rem;
    margin-top: 5px;
}

/* 12. 辅助链接 */
.auxiliary-links {
    margin-top: 1.5rem;
    display: flex;
    justify-content: center; /* 注册页只保留一个链接，居中显示 */
    font-size: 0.875rem;
}

.auxiliary-links a {
    color: #36a3b2; 
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s;
}

.auxiliary-links a:hover {
    color: #00695c;
    text-decoration: underline;
}

/* 修复 animate-spin 样式，如果全局未定义 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.animate-spin {
  animation: spin 1s linear infinite;
}
</style>