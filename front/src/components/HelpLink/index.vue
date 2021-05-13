<template>
  <el-link type="primary" :underline="false" icon="el-icon-question" @click="openLink(to)">{{ btnText }}</el-link>
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
      default: ''
    }
  },
  computed: {
    btnText() {
      return this.buttonText || this.$ts('helpBook')
    }
  }
}
</script>
