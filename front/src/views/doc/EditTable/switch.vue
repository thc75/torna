<template>
    <el-switch
      v-model="value1"
      :active-color="activeColor"
      :active-value="activeVlaue"
      :inactive-value="inactiveValue"
      @change="onChange" 
    />
</template>
<script>
export default {
  name: 'Switch1',
  props: {
    value: {
      type: Number,
      default: null
    },
    activeColor:{
      type: String,
      default: ''
    },
    activeVlaue:{
      type: Number,
      default: 1
    },
    inactiveValue:{
      type: Number,
      default: 0
    },
  },
  data() {
    return {
      value1: this.value,
    }
  },
  watch: {
    data(val) {
      this.value = val
    }
  },
  methods: {
    onChange() {
      this.$emit('change',this.value1)
    },
  }
}
</script>
 
<style scoped lang="scss">
 
</style>