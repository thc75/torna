<template>
  <el-table
    :data="data"
    border
    highlight-current-row
  >
    <el-table-column :label="$ts('value')" prop="value">
      <template slot-scope="scope">
        <span v-if="scope.row.name === scope.row.value">
          {{ scope.row.value }}
        </span>
        <span v-else>
          {{ `${scope.row.name}(${scope.row.value})` }}
        </span>
      </template>
    </el-table-column>
    <el-table-column :label="$ts('type')" prop="type" />
    <el-table-column :label="$ts('description')" prop="description" />
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
