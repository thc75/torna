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
      prop="id"
      label="参数ID"
      width="160"
    />
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      :label="nameLabel"
      width="250"
    >
      <template slot-scope="scope">
        <span>
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
    />
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      :label="descriptionLabel"
    />
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
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
      return ''
    },
    onEnumPopoverShow(ref) {
      this.$refs[ref].reload()
    },
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    }
  }
}
</script>
