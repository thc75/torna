<template>
  <router-link :to="to" target="_blank">
    <el-link type="primary" :underline="false" icon="el-icon-question">{{ buttonText }}</el-link>
  </router-link>
</template>
<script>
export default {
  props: {
    to: {
      type: String,
      required: true
    },
    buttonText: {
      type: String,
      default: '帮助手册'
    }
  }
}
</script>
