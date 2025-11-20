<template>
  <!-- 外层容器：使用 login-wrapper 实现全屏居中和背景色 -->
  <div class="login-wrapper">
    <!-- 登录卡片容器 -->
    <div class="login-card">
      <h2 class="login-title">
        学生管理系统登录
      </h2>
      
      <!-- 错误信息提示框 -->
      <p v-if="loginError" class="error-message">
        {{ loginError }}
      </p>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名/学号</label>
          <input id="username" v-model="username" type="text" required
                 placeholder="请输入用户名" />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input id="password" v-model="password" type="password" required
                 placeholder="请输入密码" />
        </div>

        <!-- 登录按钮，使用 button-submit 类名 -->
        <button type="submit" :disabled="userStore.isLoading" class="button-submit">
          <span v-if="userStore.isLoading" class="loading-content">
            <!-- 加载动画 SVG (需要确保 animate-spin 样式在全局或这里有定义) -->
            <svg class="animate-spin spinner-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            登录中...
          </span>
          <span v-else>登录</span>
        </button>
      </form>
      
      <!-- 辅助链接 -->
      <div class="auxiliary-links">
          <router-link to="/register" class="link-register">
              还没有账号？去注册
          </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * src/views/Login.vue
 * 登录视图
 */
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// 确保这里的导入路径正确
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
  loginError.value = ''; 
  if (userStore.isLoading) return;

  try {
    await userStore.login(username.value, password.value);
    // 假设登录成功后跳转到 '/home'
    router.push('/home'); 

  } catch (err) {
    const errorMessage = err.response?.data?.message || '登录失败，请检查用户名和密码。';
    loginError.value = errorMessage;
    console.error('登录组件捕获到错误:', err);

  } finally {
    password.value = '';
  }
};
</script>

<style scoped>
/* --------------------------------------
 * 核心美化样式 (Scoped CSS)
 * -------------------------------------- */

/* 1. 外层容器：全屏青色背景和居中 */
.login-wrapper {
    /* 确保全屏覆盖 */
    min-height: 100vh;
    width: 100%;
    
    /* 清新专业的青色背景 */
    background-color: #36a3b2; /* 浅青色/蓝绿色 */
    
    /* Flexbox 实现完美居中 */
    display: flex;
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中 */
    padding: 1rem;
    box-sizing: border-box;
}

/* 2. 登录卡片容器 */
.login-card {
    width: 100%;
    max-width: 420px; /* 适中宽度 */
    background-color: #ffffff;
    padding: 2.5rem;
    border-radius: 12px; /* 圆角 */
    
    /* 立体阴影效果 */
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1), 0 5px 15px rgba(0, 0, 0, 0.05);
    border-top: 5px solid #00796b; /* 顶部分割线，增加设计感 */
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
    gap: 1.25rem; /* 表单项间距 */
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
    border-color: #36a3b2; /* 聚焦边框色 */
    box-shadow: 0 0 0 3px rgba(54, 163, 178, 0.3); /* 聚焦外光晕 */
}

/* 6. 登录按钮 */
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
    
    background-color: #00796b; /* 深青色按钮 */
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

/* 8. 错误消息样式 */
.error-message {
    color: #c53030; /* 红色 */
    background-color: #fed7d7; /* 浅红背景 */
    padding: 0.75rem;
    border-radius: 6px;
    margin-bottom: 1.5rem;
    text-align: center;
    border: 1px solid #fc8181;
    font-size: 0.875rem;
}

/* 9. 辅助链接 */
.auxiliary-links {
    margin-top: 1.5rem;
    display: flex;
    justify-content: space-between;
    font-size: 0.875rem;
}

.auxiliary-links a {
    color: #36a3b2; /* 链接颜色使用背景色主题 */
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s;
}

.auxiliary-links a:hover {
    color: #00796b;
    text-decoration: underline;
}

/* 修复 animate-spin 样式，如果全局未定义 */
/* 必须在全局或这里定义 @keyframes spin 才能让 SVG 旋转 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.animate-spin {
  animation: spin 1s linear infinite;
}
</style>