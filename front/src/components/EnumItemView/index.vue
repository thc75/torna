<template>
  <el-table
    :data="data"
    border
    highlight-current-row
  >
    <el-table-column :label="$t('value')" prop="value">
      <template slot-scope="scope">
        <span v-if="scope.row.name === scope.row.value">
          {{ scope.row.value }}
        </span>
        <span v-else>
          {{ `${scope.row.name}(${scope.row.value})` }}
        </span>
      </template>
    </el-table-column>
    <el-table-column :label="$t('type')" prop="type" width="100" />
    <el-table-column :label="$t('description')" prop="description" />
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
    },
    list: {
      type: Array,
      default: () => []
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
    if (this.list.length > 0) {
      this.data = this.list
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
