<template>
  <el-tooltip :effect="effect" :content="time" :placement="placement">
    <span>{{ time.split(' ')[0] }}</span>
  </el-tooltip>
</template>
<script>
export default {
  props: {
    time: {
      type: String,
      required: true
    },
    effect: {
      type: String,
      default: 'dark'
    },
    placement: {
      type: String,
      default: 'top'
    }
  }
}
</script>
