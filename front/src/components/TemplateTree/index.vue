<template>
  <div>
    <el-tree
      :data="list"
      :props="defaultProps"
      :highlight-current="true"
      :expand-on-click-node="true"
      :empty-text="$t('noData')"
      node-key="id"
      style="margin-top: 20px"
      :default-expand-all="defaultExpand"
      @node-click="onNodeClick"
    >
      <span slot-scope="{ data }">
        <el-link
          v-if="btnText && (data.children == null || data.children.length === 0)"
          type="primary"
          style="margin-right: 5px;"
          @click="btnClick(data)"
        >
          {{ btnText }}
        </el-link>
        {{ data.label }}
      </span>
    </el-tree>
  </div>
</template>
<script>

export default {
  name: 'TemplateTree',
  props: {
    initLoad: {
      type: Boolean,
      default: true
    },
    nodeClick: {
      type: Function,
      default: () => {}
    },
    defaultExpand: {
      type: Boolean,
      default: true
    },
    btnText: {
      type: String,
      default: null
    },
    btnClick: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      list: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  created() {
    if (this.initLoad) {
      this.reload()
    }
  },
  methods: {
    reload() {
      this.get('admin/gen/template/tree', {}, resp => {
        this.list = resp.data
      })
    },
    onNodeClick(data, node, tree) {
      this.nodeClick(data, node, tree)
    }
  }
}
</script>
