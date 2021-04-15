<template>
  <el-table
    :data="data"
    row-key="id"
    :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    :row-class-name="tableRowClassName"
    :indent="20"
    :empty-text="emptyText"
    border
    highlight-current-row
    default-expand-all
    class="el-table-tree"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      :label="nameLabel"
      width="250"
    >
      <template slot-scope="scope">
        <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''">
          {{ scope.row.name }}
          <el-tooltip content="查看字典" placement="top">
            <el-popover
              placement="right"
              width="500"
              trigger="click"
              @show="onEnumPopoverShow(`enumRef_${scope.row.name}`)"
            >
              <enum-item-view :ref="`enumRef_${scope.row.name}`" :enum-id="scope.row.enumId" />
              <el-button v-if="scope.row.enumId" slot="reference" type="text" icon="el-icon-tickets" />
            </el-popover>
          </el-tooltip>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('type')"
      prop="type"
      label="类型"
      width="100"
    >
      <template slot-scope="scope">
        <span>{{ scope.row.type }}</span>
        <span v-show="scope.row.type === 'array' && scope.row.elementType">
          <el-tooltip effect="dark" :content="`元素类型：${scope.row.elementType}`" placement="top">
            <i class="el-icon-info"></i>
          </el-tooltip>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('required')"
      prop="required"
      label="必须"
      width="60"
    >
      <template slot-scope="scope">
        <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? '是' : '否' }}</span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('maxLength')"
      prop="maxLength"
      label="最大长度"
      width="80"
    />
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      :label="descriptionLabel"
    >
      <template slot-scope="scope">
        <div v-if="scope.row.description.length < 100" v-html="scope.row.description"></div>
        <div v-else>
          <el-popover
            placement="left"
            trigger="click"
          >
            <div v-html="scope.row.description"></div>
            <el-button slot="reference" type="text">点击查看</el-button>
          </el-popover>
        </div>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
    />
  </el-table>
</template>
<style>
.el-table .warning-row {
  background: oldlace;
}
</style>
<script>
import EnumItemView from '../EnumItemView'
export default {
  name: 'ParameterTable',
  components: { EnumItemView },
  props: {
    data: {
      type: Array,
      default: () => []
    },
    emptyText: {
      type: String,
      default: '无参数'
    },
    nameLabel: {
      type: String,
      default: '名称'
    },
    descriptionLabel: {
      type: String,
      default: '描述'
    },
    exampleLabel: {
      type: String,
      default: '示例值'
    },
    hiddenColumns: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      enumId: ''
    }
  },
  methods: {
    tableRowClassName({ row, index }) {
      if (row.isDeleted) {
        row.hidden = true
        return 'hidden-row'
      }
      if (row._changed) {
        return 'warning-row'
      }
      return ''
    },
    onEnumPopoverShow(ref) {
      this.$refs[ref].reload()
    },
    hasNoParentAndChildren(row) {
      const children = row.children
      const noChildren = !children || children.length === 0
      return !row.parentId && noChildren
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    }
  }
}
</script>
