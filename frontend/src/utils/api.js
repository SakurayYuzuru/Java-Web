import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 5000,
})

api.interceptors.request.use(
    config => {
        return config
    },

    error => Promise.reject(error)
)

export default api