import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'
import AddUser from '@/views/user/AddUser.vue'
import ListUser from '@/views/user/ListUser.vue'
import AddStudent from '@/views/student/AddStudent.vue'
// import ListStudent from '@/views/student/ListStudent.vue'
// import UploadFile from '@/views/file/UploadFile.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/home', component: Home },
  { path: '/add-user', component: AddUser },
  { path: '/list-user', component: ListUser },
  { path: '/add-score', component: AddStudent },
  // { path: '/list-score', component: ListStudent },
  // { path: '/upload-file', component: UploadFile },

]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

export default router
