<template>
  <el-table
    :data="data"
    border
    row-key="id"
    default-expand-all
    :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    highlight-current-row
    :empty-text="emptyText"
    :indent="20"
    class="el-table-tree"
  >
    <el-table-column
      prop="name"
      :label="nameLabel"
      width="250"
    >
      <template slot-scope="scope">
        <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''">
          {{ scope.row.name }}
        </span>
      </template>
    </el-table-column>
    <el-table-column
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
      prop="required"
      label="必须"
      width="60"
    >
      <template slot-scope="scope">
        <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? '是' : '否' }}</span>
      </template>
    </el-table-column>
    <el-table-column
      prop="maxLength"
      label="最大长度"
      width="100"
    />
    <el-table-column
      prop="description"
      :label="descriptionLabel"
    />
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
      show-overflow-tooltip
    />
  </el-table>
</template>

<script>
export default {
  name: 'ApiParamTable',
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
  methods: {
    onEnumPopoverShow(ref) {
      this.$refs[ref].reload()
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    }
  }
}
</script>
