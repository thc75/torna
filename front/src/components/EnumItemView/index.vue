<template>
  <el-table
    :data="data"
    border
    highlight-current-row
  >
    <el-table-column label="字典值" prop="name">
      <template slot-scope="scope">
        <div v-if="scope.row.name === scope.row.value">
          {{ scope.row.name }}
        </div>
        <div v-else>
          {{ scope.row.name }}({{ scope.row.value }})
        </div>
      </template>
    </el-table-column>
    <el-table-column label="类型" prop="type" />
    <el-table-column label="描述" prop="description" />
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
