<template>
  <div style="display: inline-block">
    <span>{{ value.oldValue }}</span> <span v-show="showArrow" class="el-icon-right"></span> <span>{{ value.newValue }}</span>
  </div>
</template>
<script>
export default {
  name: 'DocChangelogSimpleDiff',
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  computed: {
    showArrow() {
      return this.value.oldValue && this.value.newValue
    }
  }
}
</script>
