/**
 * src/stores/File.js
 * 文件管理状态管理 Store，用于与后端 /api/files 接口交互。
 */
import { defineStore } from 'pinia';
import api from '@/utils/api'; // 假设这是配置好的 Axios 实例

export const FileStore = defineStore('file', {
    state: () => ({
        // 文件列表
        files: [],
        // 分页信息
        pagination: {
            // Spring Pageable Response 字段：
            page: 0, // 当前页码 (从 0 开始)
            size: 10, // 每页大小
            totalElements: 0, // 总记录数
            totalPages: 0, // 总页数
            last: true, // 是否最后一页
        },
        isLoading: false,
        error: null,
    }),

    getters: {
        /** 获取当前页的文件列表 */
        getFileList: (state) => state.files,
        /** 获取分页信息 */
        getPagination: (state) => state.pagination,
        /** 判断是否正在加载数据 */
        isStoreLoading: (state) => state.isLoading,
    },

    actions: {
        /**
         * [GET /api/files] 分页获取文件列表
         * @param {object} params - 分页和排序参数
         * @param {number} params.page - 页码 (0-based, 默认为 0)
         * @param {number} params.size - 每页大小 (默认为 10)
         * @param {string} params.sortBy - 排序字段 (默认为 uploadTime)
         * @param {string} params.sortDirection - 排序方向 (asc 或 desc)
         */
        async fetchFiles(params = {}) {
            this.isLoading = true;
            this.error = null;
            try {
                // 默认参数与后端 FilePageDTO 保持一致
                const defaultParams = {
                    page: 0,
                    size: 10,
                    sortBy: 'uploadTime',
                    sortDirection: 'desc',
                    ...params
                };

                const response = await api.get('/files', { params: defaultParams });
                const pageData = response.data;

                // 更新文件列表和分页信息
                this.files = pageData.content;
                this.pagination = {
                    page: pageData.number,
                    size: pageData.size,
                    totalElements: pageData.totalElements,
                    totalPages: pageData.totalPages,
                    last: pageData.last,
                };

                console.log('文件列表加载成功。');
            } catch (err) {
                this.error = '加载文件列表失败。请检查后端服务是否正常。';
                console.error('Fetch Files Error:', err);
                throw err;
            } finally {
                this.isLoading = false;
            }
        },

        /**
         * [POST /api/files/upload] 上传文件及元数据
         * @param {File} file - 要上传的 File 对象
         * @param {string} description - 文件描述
         * @returns {FileDTO} 上传成功后的文件信息
         */
        async uploadFile(file, description = '') {
            this.isLoading = true;
            this.error = null;
            try {
                const formData = new FormData();
                formData.append('file', file);
                formData.append('description', description);

                const response = await api.post('/files/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                // 上传成功后，将新文件添加到列表开头
                this.files.unshift(response.data);
                // 仅更新总数，以便刷新分页显示
                this.pagination.totalElements += 1;

                console.log('文件上传成功:', response.data.name);
                return response.data;
            } catch (err) {
                this.error = '文件上传失败。';
                console.error('Upload File Error:', err);
                throw err;
            } finally {
                this.isLoading = false;
            }
        },

        /**
         * [PUT /api/files/{id}] 更新文件元数据 (名称和描述)
         * @param {number} id - 文件ID
         * @param {object} updateData - { name: string, description: string }
         * @returns {FileDTO} 更新后的文件信息
         */
        async updateFile(id, updateData) {
            this.isLoading = true;
            this.error = null;
            try {
                const response = await api.put(`/files/${id}`, updateData);
                const updatedFile = response.data;

                // 在本地列表中更新文件信息
                const index = this.files.findIndex(f => f.id === id);
                if (index !== -1) {
                    this.files[index] = updatedFile;
                }

                console.log(`文件 ID ${id} 更新成功。`);
                return updatedFile;
            } catch (err) {
                this.error = '更新文件信息失败。';
                console.error('Update File Error:', err);
                throw err;
            } finally {
                this.isLoading = false;
            }
        },

        /**
         * [GET /api/files/download/{id}] 下载文件
         * @param {number} id - 文件ID
         * @param {string} filename - 文件名 (可选，用于本地保存)
         */
        async downloadFile(id, filename) {
            this.error = null;
            try {
                // Axios 配置 responseType: 'blob' 以接收二进制数据
                const response = await api.get(`/files/download/${id}`, {
                    responseType: 'blob', 
                });

                // 获取响应头中的文件名 (后端设置的 Content-Disposition)
                const contentDisposition = response.headers['content-disposition'];
                let actualFilename = filename || `file-${id}`; // 默认文件名
                if (contentDisposition) {
                    const match = contentDisposition.match(/filename\*=UTF-8''(.+?)(;|$)/);
                    if (match && match[1]) {
                        // 解码 URI 编码的文件名
                        actualFilename = decodeURIComponent(match[1]);
                    }
                }
                
                // 使用 Blob 创建下载链接
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', actualFilename); // 设置下载文件名
                document.body.appendChild(link);
                link.click();
                link.remove();
                window.URL.revokeObjectURL(url); // 清理 URL 对象

                console.log(`文件 ID ${id} 下载成功。`);
            } catch (err) {
                this.error = '文件下载失败。';
                console.error('Download File Error:', err);
                throw err;
            }
        },

        /**
         * [DELETE /api/files/{id}] 删除文件
         * @param {number} id - 文件ID
         */
        async deleteFile(id) {
            this.isLoading = true;
            this.error = null;
            try {
                await api.delete(`/files/${id}`);

                // 从本地列表中移除文件
                this.files = this.files.filter(f => f.id !== id);
                this.pagination.totalElements -= 1;

                console.log(`文件 ID ${id} 删除成功。`);
            } catch (err) {
                this.error = '文件删除失败。';
                console.error('Delete File Error:', err);
                throw err;
            } finally {
                this.isLoading = false;
            }
        }
    }
});