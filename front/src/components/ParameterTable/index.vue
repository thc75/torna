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
    @cell-mouse-enter="onCellMouseEnter"
    @cell-mouse-leave="onCellMouseLeave"
  >
    <el-table-column
      v-if="isColumnShow('name')"
      prop="name"
      :label="nameLabel"
      width="300"
    >
      <template slot-scope="scope">
        <span :class="hasNoParentAndChildren(scope.row) ? 'el-table--row-no-parent-children' : ''" style="white-space: nowrap">
          {{ scope.row.name }}<el-tag v-show="rowId === scope.row.id" effect="plain" class="copyBtn" @click.stop="copy(scope.row.name)">{{ $ts('copy') }}</el-tag>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('type')"
      prop="type"
      :label="$ts('type')"
      width="100"
    >
      <template slot-scope="scope">
        <span>{{ scope.row.type }}</span>
        <span v-show="scope.row.type === 'array' && scope.row.elementType">
          <el-tooltip effect="dark" :content="$ts('elType', scope.row.elementType)" placement="top">
            <i class="el-icon-info"></i>
          </el-tooltip>
        </span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('required')"
      prop="required"
      :label="$ts('require')"
      :width="$width(60, { 'en': 75 })"
    >
      <template slot-scope="scope">
        <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? $ts('yes') : $ts('no') }}</span>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('maxLength')"
      prop="maxLength"
      :label="$ts('maxLength')"
      :width="$width(80, { 'en': 100 })"
    />
    <el-table-column
      v-if="isColumnShow('description')"
      prop="description"
      :label="descriptionLabel"
    >
      <template slot-scope="scope">
        <div v-if="scope.row.enumId" style="display: inline-block;">
          {{ scope.row.description }}
          <enum-item-view :ref="`enumRef_${scope.row.id}`" :enum-id="scope.row.enumId" mounted-load />
        </div>
        <div v-else style="display: inline-block;">
          <div v-if="scope.row.description.length < 100" v-html="scope.row.description"></div>
          <div v-else>
            <div style="height: 100px;overflow-y: auto" v-html="scope.row.description"></div>
          </div>
        </div>
        <el-tag v-if="scope.row.description && scope.row.description.length > 0" v-show="rowId === scope.row.id" effect="plain" class="copyBtn" @click.stop="copy(scope.row.description)">{{ $ts('copy') }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column
      v-if="isColumnShow('example')"
      prop="example"
      :label="exampleLabel"
    >
      <template slot-scope="scope">
        {{ scope.row.example }}
      </template>
    </el-table-column>
  </el-table>
</template>
<style>
.el-table .warning-row {
  background: oldlace;
}
.el-table .copyBtn {
  margin-left: 10px;
  cursor: pointer;
  position: absolute;
  right: 5px;
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
      default: $ts('emptyParam')
    },
    nameLabel: {
      type: String,
      default: $ts('name')
    },
    descriptionLabel: {
      type: String,
      default: $ts('description')
    },
    exampleLabel: {
      type: String,
      default: $ts('example')
    },
    hiddenColumns: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      enumId: '',
      rowId: ''
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
    isColumnShow(label) {
      return this.hiddenColumns.filter(lb => lb === label).length === 0
    },
    onCellMouseEnter(row, column, cell, event) {
      this.rowId = row.id
    },
    onCellMouseLeave(row, column, cell, event) {
      this.rowId = ''
    },
    copy(text) {
      this.copyText(text)
    }
  }
}
</script>
