import axios from 'axios'

// 使用环境变量配置基础URL
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

const request = axios.create({
    baseURL: baseURL,
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        console.log('发送请求:', config.method, config.url, config.data)
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        console.error('API请求错误:', error)
        if (error.response) {
            const message = error.response.data.message || '请求失败'
            // 在生产环境中不显示alert
            if (process.env.NODE_ENV !== 'production') {
                alert(message)
            }
        }
        return Promise.reject(error)
    }
)

export default request
