<template>
  <span :title="time">{{ time.split(' ')[0] }}</span>
</template>
<script>
export default {
  props: {
    time: {
      type: String,
      required: true
    }
  }
}
</script>
