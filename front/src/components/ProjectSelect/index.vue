<template>
  <el-tree
    ref="tree"
    :data="treeData"
    :props="props"
    node-key="id"
    show-checkbox
    @check="onCheckClick"
  />
</template>
<script>
export default {
  name: 'ProjectSelect',
  data() {
    return {
      treeData: [],
      props: {
        label: 'name',
        children: 'children',
        disabled: 'disabled'
      }
    }
  },
  created() {
    this.get('/admin/project/tree', {}, resp => {
      this.treeData = resp.data
    })
  },
  methods: {
    onCheckClick() {

    }
  }
}
</script>
