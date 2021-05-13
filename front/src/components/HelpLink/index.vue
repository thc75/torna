<template>
  <el-link type="primary" :underline="false" icon="el-icon-question" @click="openLink(to)">{{ buttonText }}</el-link>
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
