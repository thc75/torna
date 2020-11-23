<template>
  <el-table
    :data="data"
    border
    row-key="id"
    default-expand-all
    :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    :cell-style="cellStyleSmall()"
    :header-cell-style="headCellStyleSmall()"
    :row-class-name="tableRowClassName"
    :empty-text="emptyText"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      label="名称"
      width="250"
    />
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
    />
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      label="描述"
    />
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      label="示例值"
    >
      <template slot-scope="scope">
        <div v-if="scope.row.type === 'enum'">
          {{ (scope.row.enums || []).join('、') }}
        </div>
        <div v-else>
          {{ scope.row.example }}
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  name: 'ParameterTable',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    emptyText: {
      type: String,
      default: '无参数'
    },
    hiddenColumns: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    tableRowClassName({ row, index }) {
      if (row.isDeleted) {
        row.hidden = true
        return 'hidden-row'
      }
      return ''
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    }
  }
}
</script>
