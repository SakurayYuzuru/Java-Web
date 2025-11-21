<template>
    <OperationPanel title="用户列表">
        <ListUserForm />
    </OperationPanel>
</template>

<script setup>
import { ref } from "vue";
import ListUserForm from "@/components/forms/ListUserForm.vue";
import OperationPanel from "@/components/OperationPanel.vue";
</script>