<template>
  <router-link :to="to" target="_blank">
    <el-link type="primary" :underline="false" icon="el-icon-question">{{ btnText }}</el-link>
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
