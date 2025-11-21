import { defineStore } from 'pinia';
import api from '@/utils/api';

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

export const Student = defineStore('student', {
    state: () => ({
        token: localStorage.getItem('authToken') || null,
        student: null,
        isLoading: false,
    }),

    getters: {
        isLoggedIn: (state) => !!state.token,
        studentId: (state) => state.student?.id,
    },

    actions: {
        async add(name, id, school, classname, chinese, math, english, physics, chemistry) {
            this.isLoading = true;
            try {
                const result = await api.post('/student/add', {
                    studentName: name,
                    studentId: id,
                    school: school, 
                    className: classname,
                    chinese: chinese,
                    math: math,
                    english: english,
                    physics: physics,
                    chemistry: chemistry
                });

                this.isLoading = false;
                return result;
            }catch(error) {
                this.isLoading = false;
                throw error;
            }
        },

        async queryStudents(page, size) {
            this.isLoading = true;
            try {
                const response = await api.post('/student/page', {
                    params: {
                        page: page,
                        size: size
                    }
                });

                this.isLoading = false;
                return response.data;
            } catch (error) {
                this.isLoading = false;
                console.error('查询学生列表失败:', error);
                throw error;
            }
        },

        async update(id, updateData) {
            this.isLoading = true;
            try {
                const result = await api.post('/user/update', {
                    id: id,
                    studentName: updateData.name,
                    studentId: updateData.id,
                    school: updateData.school, 
                    className: updateData.classname,
                    chinese: updateData.chinese,
                    math: math,
                    english: updateData.english,
                    physics: updateData.physics,
                    chemistry: updateData.chemistry
                });

                this.isLoading = false;
                return result.data;
            }catch(error) {
                this.isLoading = false;
                console.error('更新学生信息失败', error);
                throw error;
            }
        },

        async delete(id) {
            this.isLoading = true;
            try {
                const result = await api.post('student/delete', {
                    id: id
                });

                this.isLoading = false;
                return result.data;
            }catch(error) {
                this.isLoading = false;
                console.error('删除学生失败', error);
                throw error;
            }
        },

        async search(name, page, size) {
            this.isLoading = true;
            try {
                const response = await api.post('student/search', {
                    name: name,
                    page: page,
                    size: size
                });

                this.isLoading = false;
                return response.data;
            }catch(error) {
                this.isLoading = false;
                console.error('查询失败', error);
                throw error;
            }
        }
    }
})