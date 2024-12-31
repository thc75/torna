<template>
    <el-input v-model="value1" size="small"  @change="onChange" :placeholder="placeholder1" :size="size" :maxlength="maxlength || ''" :show-word-limit="showWordLimit"></el-input>
</template>
<script>
export default {
  name: 'Input',
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder:{
      type: String,
      default: ''
    },
    size:{
      type: String,
      default: ''
    },
    maxlength:{
      type: Number,
      default: 0
    },
    showWordLimit:{
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      value1: this.value,
      placeholder1:this.placeholder,
    }
  },
  watch: {
    data(val) {
        console.log(val)
      this.value = val
    }
  },
  methods: {
    onChange() {
        console.log(this.value1)
        this.$emit('change',this.value1)
    },
  }
}
</script>
 
<style scoped lang="scss">
 
</style>