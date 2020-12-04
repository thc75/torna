<template>
  <el-table
    :data="data"
    border
    :header-cell-style="cellStyleSmall()"
    :cell-style="cellStyleSmall()"
  >
    <el-table-column label="名称" prop="name"/>
    <el-table-column label="类型" prop="type"/>
    <el-table-column label="值" prop="value"/>
    <el-table-column label="描述" prop="description"/>
  </el-table>
</template>
<script>
export default {
  name: 'EnumItemView',
  props: {
    enumId: {
      type: String,
      default: ''
    },
    mountedLoad: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      data: []
    }
  },
  watch: {
    enumId(enumId) {
      this.loadData(enumId)
    }
  },
  mounted() {
    if (this.mountedLoad) {
      this.loadData(this.enumId)
    }
  },
  methods: {
    loadData(enumId) {
      if (enumId) {
        this.loadEnumItem(enumId).then(data => {
          this.data = data
        })
      }
    },
    reload() {
      this.loadData(this.enumId)
    }
  }
}
</script>
