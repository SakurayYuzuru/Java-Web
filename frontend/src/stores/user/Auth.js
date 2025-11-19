/**
 * src/stores/Auth.js
 * * 认证状态管理 Store，直接使用 api (Axios 实例) 进行请求。
 */
import { defineStore } from 'pinia';
import api from '@/utils/api'; // Axios 实例，baseURL: http://localhost:8080/api

// --- 辅助函数：Token 管理 (直接操作 localStorage) ---
const setAuthToken = (token) => {
    if (token) {
        localStorage.setItem('authToken', token);
        // 确保 Axios 拦截器能拿到最新的 Token
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`; 
    } else {
        localStorage.removeItem('authToken');
        delete api.defaults.headers.common['Authorization'];
    }
};

export const User = defineStore('user', {
    state: () => ({
        // 尝试从本地存储读取 token，以在刷新时保持登录状态
        token: localStorage.getItem('authToken') || null,
        user: null, // 存储已登录用户的基本信息
        isLoading: false,
    }),

    getters: {
        // 判断用户是否登录
        isLoggedIn: (state) => !!state.token,
        // 获取当前用户ID (可选，用于权限控制)
        userId: (state) => state.user?.id,
    },

    actions: {
        /**
         * 用户登录逻辑
         * @param {string} name - 用户名/学号 (对应后端 DTO 的 username)
         * @param {string} password - 密码
         */
        async login(name, password) {
            this.isLoading = true;
            this.user = null;
            this.token = null; // 清除旧状态
            setAuthToken(null);
            
            try {
                // POST /api/user/login
                // 假设后端接受的字段是 username 和 password
                const result = await api.post('/user/login', { 
                    username: name, 
                    password: password 
                });
                
                // 假设 Spring Boot 成功返回 { token: 'jwt-token', user: { ... } }
                this.token = result.token;
                this.user = result.user;
                setAuthToken(result.token); // 存储到本地

                this.isLoading = false;
                return true; // 登录成功
            } catch (error) {
                this.isLoading = false;
                // 抛出错误，由 Vue 组件负责展示用户提示
                throw error;
            }
        },

        /**
         * 用户注册逻辑
         * @param {string} name - 用户名
         * @param {string} password - 密码
         */
        async register(name, password) {
            this.isLoading = true;
            try {
                // POST /api/user/register
                // 假设注册只需要用户名和密码
                const result = await api.post('/user/register', {
                    username: name,
                    password: password
                    // 实际中可能需要更多字段如 email, role
                });
                
                this.isLoading = false;
                return result; // 返回注册结果信息
            } catch (error) {
                this.isLoading = false;
                throw error;
            }
        },

        /**
         * 用户登出逻辑
         */
        logout() {
            this.token = null;
            this.user = null;
            setAuthToken(null); // 清除本地存储的 Token
            console.log('用户已安全登出。');
            // 实际应用中，您会在这里使用 router 跳转到登录页
        },

        /**
         * 尝试通过现有 Token 获取用户信息 (用于应用刷新时保持登录状态)
         */
        async initializeAuth() {
            // 如果有 Token 但没有 user 信息，则尝试获取 profile
            if (this.token && !this.user) {
                try {
                    // 假设 /user/profile 接口返回当前用户详情
                    const userProfile = await api.get('/user/profile'); 
                    this.user = userProfile;
                } catch (error) {
                    // Token 无效、过期或接口失败
                    console.warn('Token 验证失败，已执行强制登出。', error);
                    this.logout();
                }
            }
        }
    }
});