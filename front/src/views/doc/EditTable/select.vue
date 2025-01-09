<template>
    <span>
      <el-select v-if="type === 'arr'" v-model="value1" @change="onChange" :clearable="clearable" :size="'mini'" :filterable="filterable" :allowCreate="allowCreate">
        <el-option v-for="type in list" :key="type" :label="type" :value="type"></el-option>
      </el-select>
      <el-select v-if="type === 'obj'" v-model="value1" @change="onChange" :clearable="clearable" :size="'mini'" :filterable="filterable" :allowCreate="allowCreate">
        <el-option v-for="enumInfo in list" :key="enumInfo.id" :label="enumInfo.name" :value="enumInfo.id">
          {{ enumInfo.name }}
        </el-option>
      </el-select>
    </span>
</template>
<script>
export default {
  name: 'Select',
  props: {
    value: {
      type: String,
      default: ''
    },
    size:{
      type: String,
      default: ''
    },
    type:{
      type: String,
      default: ''
    },
    clearable:{
      type: Boolean,
      default: false
    },
    list:{
      type: Array,
      default: []
    },
    filterable:{
      type: Boolean,
      default: false
    },
    allowCreate:{
      type: Boolean,
      default: false
    }
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