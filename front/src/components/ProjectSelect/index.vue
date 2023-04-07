<template>
  <div style="margin-bottom: 50px">
    <el-tree
      ref="tree"
      :data="treeData"
      :props="props"
      node-key="id"
      show-checkbox
      check-on-click-node
      :expand-on-click-node="false"
    />
    <div style="padding-top: 20px;float: right">
      <el-button type="text" @click="on_cancel" style="margin-right: 10px">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="on_ok">{{ $t('save') }}</el-button>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ProjectSelect',
  props: {
    onOk: {
      type: Function,
      default: () => {}
    },
    onCancel: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      treeData: [],
      props: {
        label: 'label',
        children: 'children',
        disabled: 'disabled'
      }
    }
  },
  methods: {
    reload(projectIds) {
      this.get('/admin/project/tree', {}, resp => {
        this.treeData = resp.data

        this.$nextTick(() => {
          const nodesMap = this.$refs.tree.store.nodesMap
          for (const i in nodesMap) {
            nodesMap[i].expanded = true
          }
          if (projectIds) {
            this.$refs.tree.setCheckedKeys(projectIds)
          }
        })
      })
    },
    on_ok() {
      const nodes = this.$refs.tree.getCheckedNodes(true)
      const projectIds = nodes.map(row => row.id)
      this.onOk(projectIds)
    },
    on_cancel() {
      this.onCancel()
    }
  }
}
</script>
