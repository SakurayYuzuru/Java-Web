<template>
  <form @submit.prevent="handleSubmit">

    <label class="label">用户名</label>
    <input
      v-model="name"
      class="input"
      type="text"
      placeholder="请输入用户名"
      required
    />

    <label class="label">密码</label>
    <input
      v-model="password"
      class="input"
      type="password"
      placeholder="请输入密码"
      required
    />

    <button class="button" :disabled="store.isLoading">
      {{ store.isLoading ? "提交中…" : "添加用户" }}
    </button>

    <p v-if="message" class="message">{{ message }}</p>

  </form>
</template>

<script setup>
import { ref } from "vue";
import { User } from "@/stores/user/Auth.js";

const store = User();

const name = ref("");
const password = ref("");
const message = ref("");

/** 添加用户逻辑 */
async function handleSubmit() {
  message.value = "";

  try {
    await store.register(name.value, password.value);
    message.value = "用户添加成功";

    name.value = "";
    password.value = "";
  } catch (err) {
    message.value = "添加失败，请检查后端日志或接口格式";
  }
}
</script>

<style scoped>
.label {
  margin-top: 12px;
  font-size: 14px;
}

.input {
  margin-top: 4px;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  width: 100%;
}

.button {
  margin-top: 20px;
  padding: 10px;
  width: 100%;
  background: #409eff;
  border: none;
  color: white;
  border-radius: 8px;
  cursor: pointer;
}

.button:disabled {
  opacity: 0.6;
  cursor: default;
}

.message {
  margin-top: 12px;
  font-size: 14px;
  color: #4caf50;
}
</style>
